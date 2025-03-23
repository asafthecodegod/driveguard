package com.example.asaf_avisar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.asaf_avisar.activitys.Post;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;

public class UploadPhoto extends Fragment implements FirebaseCallback {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Bitmap selectedImageBitmap;
    private ImageView uploadIcon, selectedImageView, profileImage;
    private TextView userName, selectPhotoText;
    private TabLayout tabLayout;
    private MaterialButton postButton;
    private FireBaseManager fireBaseManager;
    private String userNameString, profileImageUrl;

    public UploadPhoto() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout that includes selectedImageView, uploadIcon, and selectPhotoText
        View view = inflater.inflate(R.layout.fragment_upload_photo, container, false);

        userName = view.findViewById(R.id.userName);
        profileImage = view.findViewById(R.id.profileImage);
        tabLayout = view.findViewById(R.id.tabLayout);
        postButton = view.findViewById(R.id.postButton);
        uploadIcon = view.findViewById(R.id.uploadIcon);
        selectedImageView = view.findViewById(R.id.selectedImageView);
        selectPhotoText = view.findViewById(R.id.selectPhotoText);

        fireBaseManager = new FireBaseManager(requireContext());
        fireBaseManager.readData(this, "Student", fireBaseManager.getUserid());

        uploadIcon.setOnClickListener(v -> openImageChooser());

        // If an image Bitmap is passed via arguments, use it.
        if (getArguments() != null && getArguments().getParcelable("imageBitmap") != null) {
            selectedImageBitmap = getArguments().getParcelable("imageBitmap");
            selectedImageView.setImageBitmap(selectedImageBitmap);
            selectedImageView.setVisibility(View.VISIBLE);
            uploadIcon.setVisibility(View.GONE);
            selectPhotoText.setVisibility(View.GONE);
        }

        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.selectTab(tabLayout.getTabAt(1));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    navigateToUploadNote();
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        postButton.setOnClickListener(v -> uploadPost());
        return view;
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null && requestCode == PICK_IMAGE_REQUEST) {
            try {
                // Convert the returned image data directly to a Bitmap.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data.getData());
                selectedImageBitmap = bitmap;
                selectedImageView.setImageBitmap(bitmap);
                selectedImageView.setVisibility(View.VISIBLE);
                // Hide uploadIcon and selectPhotoText once an image is selected.
                uploadIcon.setVisibility(View.GONE);
                selectPhotoText.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadPost() {
        if (selectedImageBitmap != null) {
            String base64Image = convertTo64Base(selectedImageBitmap);
            if (!base64Image.isEmpty()) {
                Post post = new Post(userNameString, "Image Post", base64Image, profileImageUrl, new Date());
                fireBaseManager.savePost(post);
                navigateToHomeFragment();
                Toast.makeText(getContext(), "Post uploaded successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "You need to upload a photo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static String convertTo64Base(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }

    public static Bitmap resizeBitmap(Bitmap bitmap, int maxSize) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scale = Math.min(((float) maxSize / width), ((float) maxSize / height));
        return Bitmap.createScaledBitmap(bitmap, (int) (width * scale), (int) (height * scale), true);
    }


    private void navigateToHomeFragment() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putInt("selectedTab", 1);
        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setArguments(bundle);
        transaction.replace(R.id.fragment_container, homeFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void navigateToUploadNote() {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        UploadNote uploadNoteFragment = new UploadNote();
        transaction.replace(R.id.fragment_container, uploadNoteFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void oncallbackArryStudent(ArrayList<StudentUser> students) {}

    @Override
    public void oncallbackStudent(StudentUser student) {
        if (student != null) {
            userNameString = student.getName();
            profileImageUrl = student.getProfilePhotoBase64();
            userName.setText(userNameString);
            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                byte[] decodedString = Base64.decode(profileImageUrl, Base64.DEFAULT);
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                profileImage.setImageBitmap(decodedBitmap);
            } else {
                profileImage.setImageResource(R.drawable.ic_default_profile);
            }
        }
    }

    @Override
    public void onCallbackTeacher(ArrayList<TeacherUser> teachers) {}
}

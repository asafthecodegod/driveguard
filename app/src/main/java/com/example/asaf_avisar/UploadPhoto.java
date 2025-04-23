package com.example.asaf_avisar;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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

import com.example.asaf_avisar.activitys.LoginOrRegistretionActivity;
import com.example.asaf_avisar.activitys.Post;
import com.example.asaf_avisar.activitys.RegisterPageActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;

public class UploadPhoto extends Fragment implements FirebaseCallback {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Bitmap selectedImageBitmap;
    private ImageView uploadIcon, selectedImageView, profileImage;
    private TextView userName, selectPhotoText,descriptionEditText;
    private TabLayout tabLayout;
    private MaterialButton postButton;
    private FireBaseManager fireBaseManager;
    private String userNameString, profileImageUrl;
    private String userId;

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
        descriptionEditText =view.findViewById(R.id.descriptionEditText);
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
                Uri imageUri = data.getData();
                String imagePath = getRealPathFromURI(imageUri);

                if (imagePath != null) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                    selectedImageBitmap = ImageUtils.rotateImageIfRequired(bitmap, imagePath); // 🔥 Fix image rotation
                    selectedImageView.setImageBitmap(selectedImageBitmap);
                    selectedImageView.setVisibility(View.VISIBLE);
                    uploadIcon.setVisibility(View.GONE);
                    selectPhotoText.setVisibility(View.GONE);
                } else {
                    Toast.makeText(getContext(), "Failed to get image path", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error loading image", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void uploadPost() {
        if (selectedImageBitmap != null) {
            String base64Image = ImageUtils.convertTo64Base(selectedImageBitmap);
//            if (!descriptionEditText.getText().toString().isEmpty()) {
//                Toast.makeText(getContext(), "Please add a description", Toast.LENGTH_SHORT).show();
//                return; // Prevent posting without a description
//            }

            if (!base64Image.isEmpty()) {

                Post post = new Post(userId, userNameString, profileImageUrl, base64Image, descriptionEditText.getText().toString(), "", new Date(), 1);
                fireBaseManager.savePost(post);
                navigateToHomeFragment();
                Toast.makeText(getContext(), "Post uploaded successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "You need to upload a photo", Toast.LENGTH_SHORT).show();
            }
        }
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
    private String getRealPathFromURI(Uri uri) {
        String filePath = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = requireContext().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
        }
        return filePath;
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
            userId = student.getId();
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

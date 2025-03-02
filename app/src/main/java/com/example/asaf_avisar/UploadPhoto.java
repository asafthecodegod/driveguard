package com.example.asaf_avisar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asaf_avisar.activitys.Post;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;

public class UploadPhoto extends Fragment implements FirebaseCallback {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView uploadIcon, imageView;
    private TextView userName;
    private TabLayout tabLayout;
    private MaterialButton postButton;
    private Uri selectedImageUri;
    private FireBaseManager fireBaseManager;
    private String userNameString;

    public UploadPhoto() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload_photo, container, false);

        userName = view.findViewById(R.id.userName);
        tabLayout = view.findViewById(R.id.tabLayout);
        postButton = view.findViewById(R.id.postButton);
        uploadIcon = view.findViewById(R.id.uploadIcon);
        imageView = view.findViewById(R.id.imageView); // Initialize imageView here

        fireBaseManager = new FireBaseManager(requireContext());
        String studentId = getArguments() != null ? getArguments().getString("STUDENT_ID") : fireBaseManager.getUserid();
        fireBaseManager.readData(this, "Student", studentId);

        userName.setText("Username");

        uploadIcon.setOnClickListener(v -> openImageChooser());

        // Restore image when switching tabs
        if (getArguments() != null && getArguments().getParcelable("imageUri") != null) {
            selectedImageUri = getArguments().getParcelable("imageUri");
            imageView.setImageURI(selectedImageUri);
            imageView.setVisibility(View.VISIBLE);
            uploadIcon.setVisibility(View.GONE);
        }

        // Disable tab switching when fragment is first loaded, and select the "Image Only" tab
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.selectTab(tabLayout.getTabAt(1));  // Manually select "Image Only" tab (index 1)

        // Add the tab selection listener to handle "Text Only" tab selection
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

        postButton.setOnClickListener(v -> {
            if (selectedImageUri != null) {
                // Convert image URI to base64 string
                String base64Image = convertImageToBase64(selectedImageUri);
                if (!base64Image.isEmpty()) {
                    // Create Post object with base64 image string
                    Post post = new Post(userNameString, "Image Post", 0, base64Image, 0, new Date());
                    fireBaseManager.savePost(post);
                    navigateToHomeFragment();
                    Toast.makeText(getContext(), "Post uploaded successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "You need to upload a photo", Toast.LENGTH_SHORT).show();
                }
            }
        });

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
            selectedImageUri = data.getData();
            imageView.setImageURI(selectedImageUri);
            imageView.setVisibility(View.VISIBLE);
            uploadIcon.setVisibility(View.GONE);
        }
    }

    private String convertImageToBase64(Uri imageUri) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            // Convert URI to Bitmap (not shown here)
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    private void navigateToHomeFragment() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Pass the flag to select the "Image Only" tab
        Bundle bundle = new Bundle();
        bundle.putInt("selectedTab", 1); // 1 for "Image Only"
        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setArguments(bundle);

        transaction.replace(R.id.fragment_container, homeFragment);
        transaction.addToBackStack(null);  // Adds the transaction to the back stack
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
        userNameString = student.getName();
    }

    @Override
    public void onCallbackTeacher(ArrayList<TeacherUser> teachers) {}
}

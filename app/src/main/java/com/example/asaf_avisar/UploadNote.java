package com.example.asaf_avisar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

import com.example.asaf_avisar.activitys.Post;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.Date;

/**
 * The type Upload note.
 */
public class UploadNote extends Fragment implements FirebaseCallback {

    private TextInputEditText noteInput;
    private TextInputEditText noteBodyInput;
    private MaterialButton saveNoteButton;
    private TabLayout tabLayout;
    private FireBaseManager fireBaseManager;
    private String userNameString, profileImageUrl;
    private TextView userName;
    private ImageView profileImage,imageView;
    private String userId;

    /**
     * Instantiates a new Upload note.
     */
    public UploadNote() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upload_note, container, false);

        fireBaseManager = new FireBaseManager(requireContext());
        String studentId = getArguments() != null ? getArguments().getString("STUDENT_ID") : fireBaseManager.getUserid();
        fireBaseManager.readData(this, "Student", studentId);

        // Initialize views
        userName = view.findViewById(R.id.userName);
        profileImage = view.findViewById(R.id.profileImage);
        imageView = view.findViewById(R.id.imageView);
        noteInput = view.findViewById(R.id.noteTitleInput);
        noteBodyInput = view.findViewById(R.id.noteBodyInput);
        saveNoteButton = view.findViewById(R.id.postButton);
        tabLayout = view.findViewById(R.id.tabLayout);

        // Handle the "Save Note" button click
        saveNoteButton.setOnClickListener(v -> {
            String note = noteBodyInput.getText().toString();
            if (!note.isEmpty()) {
                // Create a post with the provided note and the user's name

                Post post = new Post(userNameString, noteInput.getText().toString(), note, profileImageUrl, new Date());
                fireBaseManager.savePost(post);
                navigateToHomeFragment();
                Toast.makeText(getContext(), "Note uploaded successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Note cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle tab selection
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        // "Text Only" tab selected, stay here
                        break;
                    case 1:
                        // "Image Only" tab selected, navigate to the Add Photo Fragment
                        navigateToAddPhotoFragment();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        return view;
    }

    // Method to navigate to the Add Photo Fragment
    private void navigateToAddPhotoFragment() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Pass the flag to select the "Image Only" tab
        Bundle bundle = new Bundle();
        bundle.putInt("selectedTab", 1); // 1 for "Image Only"
        UploadPhoto uploadPhotoFragment = new UploadPhoto();
        uploadPhotoFragment.setArguments(bundle);

        transaction.replace(R.id.fragment_container, uploadPhotoFragment);
        transaction.addToBackStack(null);  // Adds the transaction to the back stack
        transaction.commit();
    }

    // Method to navigate to the Home Fragment
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

    @Override
    public void oncallbackArryStudent(ArrayList<StudentUser> students) {
        // You can implement this if needed
    }

    @Override
    public void oncallbackStudent(StudentUser student) {
        if (student != null) {
            userNameString = student.getName();
            userId = student.getId();
            profileImageUrl = student.getProfilePhotoBase64();
            userName.setText(userNameString);
            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                    byte[] decodedString = Base64.decode(profileImageUrl, Base64.DEFAULT);
                    Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    profileImage.setImageBitmap(decodedBitmap);
                } else {
                    profileImage.setImageResource(R.drawable.ic_default_profile);
                }

            } else {
                profileImage.setImageResource(R.drawable.ic_default_profile);
            }
        }
    }

    @Override
    public void onCallbackTeacher(ArrayList<TeacherUser> teachers) {
        // You can implement this if needed
    }
}

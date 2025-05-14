package com.example.asaf_avisar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
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
 * The type Upload note - Fragment for creating and uploading text notes.
 */
public class UploadNote extends Fragment implements FirebaseCallback {

    //==========================================================================================
    // DISPLAY LAYER - UI Components and Display Methods
    //==========================================================================================

    // UI components
    private TextInputEditText noteInput;
    private TextInputEditText noteBodyInput;
    private MaterialButton saveNoteButton;
    private TabLayout tabLayout;
    private TextView userName;
    private ImageView profileImage, imageView;

    /**
     * Empty constructor required for fragments
     */
    public UploadNote() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upload_note, container, false);

        // Initialize data components
        initializeDataComponents();

        // Set up UI components
        initializeUIComponents(view);
        setupEventListeners();

        return view;
    }

    /**
     * Initialize all UI components
     */
    private void initializeUIComponents(View view) {
        // Bind all views
        userName = view.findViewById(R.id.userName);
        profileImage = view.findViewById(R.id.profileImage);
        imageView = view.findViewById(R.id.imageView);
        noteInput = view.findViewById(R.id.noteTitleInput);
        noteBodyInput = view.findViewById(R.id.noteBodyInput);
        saveNoteButton = view.findViewById(R.id.postButton);
        tabLayout = view.findViewById(R.id.tabLayout);
    }

    /**
     * Set up event listeners for interactive UI elements
     */
    private void setupEventListeners() {
        // Handle the "Save Note" button click
        saveNoteButton.setOnClickListener(v -> handleSaveNoteClick());

        // Handle tab selection
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                handleTabSelection(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    /**
     * Update UI with user profile data
     */
    private void updateProfileUI(StudentUser student) {
        if (student != null) {
            // Set username
            userName.setText(student.getName());

            // Set profile image
            displayProfileImage(student.getProfilePhotoBase64());
        }
    }

    /**
     * Display profile image from base64 string
     */
    private void displayProfileImage(String profileImageUrl) {
        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            byte[] decodedString = Base64.decode(profileImageUrl, Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            profileImage.setImageBitmap(decodedBitmap);
        } else {
            profileImage.setImageResource(R.drawable.ic_default_profile);
        }
    }

    /**
     * Display success message to user
     */
    private void showSuccessMessage() {
        Toast.makeText(getContext(), "Note uploaded successfully", Toast.LENGTH_SHORT).show();
    }

    /**
     * Display error message to user
     */
    private void showEmptyNoteError() {
        Toast.makeText(getContext(), "Note cannot be empty", Toast.LENGTH_SHORT).show();
    }

    //==========================================================================================
    // LOGIC LAYER - Business Logic and Data Management
    //==========================================================================================

    private FireBaseManager fireBaseManager;
    private String userNameString, profileImageUrl;
    private String userId;

    /**
     * Initialize data-related components
     */
    private void initializeDataComponents() {
        fireBaseManager = new FireBaseManager(requireContext());
        String studentId = getArguments() != null ?
                getArguments().getString("STUDENT_ID") :
                fireBaseManager.getUserid();

        // Load user data
        loadUserData(studentId);
    }

    /**
     * Load user data from Firebase
     */
    private void loadUserData(String studentId) {
        fireBaseManager.readData(this, "Student", studentId);
    }

    /**
     * Handle save note button click
     */
    private void handleSaveNoteClick() {
        String noteBody = noteBodyInput.getText().toString();
        String noteTitle = noteInput.getText().toString();

        if (validateNote(noteBody)) {
            // Create and save post
            createAndSavePost(noteTitle, noteBody);
            navigateToHomeFragment();
            showSuccessMessage();
        } else {
            showEmptyNoteError();
        }
    }

    /**
     * Validate the note content
     */
    private boolean validateNote(String noteBody) {
        return noteBody != null && !noteBody.isEmpty();
    }

    /**
     * Create and save a new post
     */
    private void createAndSavePost(String title, String content) {
        Post post = new Post(userNameString, title, content, profileImageUrl, new Date());
        fireBaseManager.savePost(post);
    }

    /**
     * Handle tab selection
     */
    private void handleTabSelection(int position) {
        switch (position) {
            case 0:
                // "Text Only" tab selected, stay here
                break;
            case 1:
                // "Image Only" tab selected, navigate to the Add Photo Fragment
                navigateToAddPhotoFragment();
                break;
        }
    }

    /**
     * Navigate to the Add Photo Fragment
     */
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

    /**
     * Navigate to the Home Fragment
     */
    private void navigateToHomeFragment() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Create and configure the home fragment
        Bundle bundle = new Bundle();
        bundle.putInt("selectedTab", 1);
        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setArguments(bundle);

        // Execute the transition
        transaction.replace(R.id.fragment_container, homeFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    //==========================================================================================
    // CALLBACK IMPLEMENTATIONS - Firebase Data Handling
    //==========================================================================================

    @Override
    public void oncallbackStudent(StudentUser student) {
        if (student != null) {
            // Store data in class fields
            userNameString = student.getName();
            userId = student.getId();
            profileImageUrl = student.getProfilePhotoBase64();

            // Update UI
            updateProfileUI(student);
        }
    }

    @Override
    public void oncallbackArryStudent(ArrayList<StudentUser> students) {
        // Not used in this fragment
    }

    @Override
    public void onCallbackTeacher(ArrayList<TeacherUser> teachers) {
        // Not used in this fragment
    }

    @Override
    public void onCallbackSingleTeacher(TeacherUser teacher) {

    }
}
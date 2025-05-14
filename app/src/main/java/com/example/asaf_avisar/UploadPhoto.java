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
import java.util.ArrayList;
import java.util.Date;

/**
 * The type Upload photo - Fragment for uploading photo posts.
 */
public class UploadPhoto extends Fragment implements FirebaseCallback {

    //==========================================================================================
    // DISPLAY LAYER - UI Components and Display Methods
    //==========================================================================================

    // Constants
    private static final int PICK_IMAGE_REQUEST = 1;

    // UI components
    private Bitmap selectedImageBitmap;
    private ImageView uploadIcon, selectedImageView, profileImage;
    private TextView userName, selectPhotoText, descriptionEditText;
    private TabLayout tabLayout;
    private MaterialButton postButton;

    /**
     * Required empty constructor
     */
    public UploadPhoto() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout
        View view = inflater.inflate(R.layout.fragment_upload_photo, container, false);

        // Initialize components
        initializeUIComponents(view);
        initializeDataComponents();
        setupEventListeners();
        configureInitialState();

        return view;
    }

    /**
     * Initialize all UI components
     */
    private void initializeUIComponents(View view) {
        userName = view.findViewById(R.id.userName);
        profileImage = view.findViewById(R.id.profileImage);
        tabLayout = view.findViewById(R.id.tabLayout);
        postButton = view.findViewById(R.id.postButton);
        uploadIcon = view.findViewById(R.id.uploadIcon);
        descriptionEditText = view.findViewById(R.id.descriptionEditText);
        selectedImageView = view.findViewById(R.id.selectedImageView);
        selectPhotoText = view.findViewById(R.id.selectPhotoText);
    }

    /**
     * Set up event listeners for interactive UI elements
     */
    private void setupEventListeners() {
        uploadIcon.setOnClickListener(v -> openImageChooser());
        postButton.setOnClickListener(v -> handlePostButtonClick());

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
    }

    /**
     * Configure initial state of UI elements
     */
    private void configureInitialState() {
        // Check if an image bitmap was passed in arguments
        if (getArguments() != null && getArguments().getParcelable("imageBitmap") != null) {
            selectedImageBitmap = getArguments().getParcelable("imageBitmap");
            displaySelectedImage(selectedImageBitmap);
        }
    }

    /**
     * Display the selected image and update UI visibility
     */
    private void displaySelectedImage(Bitmap bitmap) {
        if (bitmap != null) {
            selectedImageView.setImageBitmap(bitmap);
            selectedImageView.setVisibility(View.VISIBLE);
            uploadIcon.setVisibility(View.GONE);
            selectPhotoText.setVisibility(View.GONE);
        }
    }

    /**
     * Update UI with user profile data
     */
    private void updateProfileUI(StudentUser student) {
        if (student != null) {
            userName.setText(student.getName());
            displayProfileImage(student.getProfilePhotoBase64());
        }
    }

    /**
     * Display profile image from base64 string
     */
    private void displayProfileImage(String base64Image) {
        if (base64Image != null && !base64Image.isEmpty()) {
            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            profileImage.setImageBitmap(decodedBitmap);
        } else {
            profileImage.setImageResource(R.drawable.ic_default_profile);
        }
    }

    /**
     * Show success message to user
     */
    private void showSuccessMessage() {
        Toast.makeText(getContext(), "Post uploaded successfully", Toast.LENGTH_SHORT).show();
    }

    /**
     * Show error message to user
     */
    private void showErrorMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
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
        fireBaseManager.readData(this, "Student", fireBaseManager.getUserid());
    }

    /**
     * Handle post button click - validate and create post
     */
    private void handlePostButtonClick() {
        if (validatePostData()) {
            createAndUploadPost();
            navigateToHomeFragment();
            showSuccessMessage();
        } else {
            showErrorMessage("You need to upload a photo");
        }
    }

    /**
     * Validate all required post data
     */
    private boolean validatePostData() {
        return selectedImageBitmap != null &&
                !ImageUtils.convertTo64Base(selectedImageBitmap).isEmpty();
    }

    /**
     * Create and upload post to Firebase
     */
    private void createAndUploadPost() {
        String base64Image = ImageUtils.convertTo64Base(selectedImageBitmap);
        String description = descriptionEditText.getText().toString();

        Post post = new Post(
                userId,
                userNameString,
                profileImageUrl,
                base64Image,
                description,
                base64Image,
                new Date(),
                1  // Type photo
        );

        fireBaseManager.savePost(post);
    }

    /**
     * Open image chooser intent
     */
    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /**
     * Process selected image from gallery
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null && requestCode == PICK_IMAGE_REQUEST) {
            processSelectedImage(data);
        }
    }

    /**
     * Process the selected image data
     */
    private void processSelectedImage(Intent data) {
        try {
            Uri imageUri = data.getData();
            String imagePath = getRealPathFromURI(imageUri);

            if (imagePath != null) {
                // Load and rotate image if needed
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                selectedImageBitmap = ImageUtils.rotateImageIfRequired(bitmap, imagePath);

                // Update UI
                displaySelectedImage(selectedImageBitmap);
            } else {
                showErrorMessage("Failed to get image path");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage("Error loading image");
        }
    }

    /**
     * Get real file path from content URI
     */
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

    /**
     * Navigate to home fragment
     */
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

    /**
     * Navigate to upload note fragment
     */
    private void navigateToUploadNote() {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();

        UploadNote uploadNoteFragment = new UploadNote();

        transaction.replace(R.id.fragment_container, uploadNoteFragment);
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
            userId = student.getId();
            userNameString = student.getName();
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
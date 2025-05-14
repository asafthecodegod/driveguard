package com.example.asaf_avisar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.asaf_avisar.activitys.DetailsActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Activity for adding a profile picture to the user account.
 */
public class Addpfp extends AppCompatActivity implements FirebaseCallback {

    //==========================================================================================
    // DISPLAY LAYER - UI Components and Display Methods
    //==========================================================================================

    // UI components
    private ImageView profileImageView;
    private Button addPhotoButton, skipButton, nextButton;

    // Image selection launcher
    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpfp);

        // Initialize UI components
        initializeUIComponents();

        // Initialize logic components
        initializeLogicComponents();

        // Set up activity result launcher
        setupGalleryLauncher();

        // Set up event listeners
        setupEventListeners();

        // Load existing profile picture if available
        loadExistingProfilePicture();
    }

    /**
     * Initialize all UI components
     */
    private void initializeUIComponents() {
        profileImageView = findViewById(R.id.profileImageView);
        addPhotoButton = findViewById(R.id.addPhotoButton);
        skipButton = findViewById(R.id.skipButton);
        nextButton = findViewById(R.id.nextButton);
    }

    /**
     * Set up event listeners for buttons
     */
    private void setupEventListeners() {
        nextButton.setOnClickListener(view -> navigateToDetailsActivity());

        addPhotoButton.setOnClickListener(view -> openGalleryForImageSelection());

        skipButton.setOnClickListener(view -> {
            setSkipFlag();
            showToast("Profile picture skipped.");
            navigateToDetailsActivity();
        });
    }

    /**
     * Set up the gallery image selection launcher
     */
    private void setupGalleryLauncher() {
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleActivityResult
        );
    }

    /**
     * Open the gallery for image selection
     */
    private void openGalleryForImageSelection() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    /**
     * Display the profile image in the ImageView
     */
    private void displayProfileImage(Bitmap bitmap) {
        if (bitmap != null) {
            profileImageView.setImageBitmap(bitmap);
        }
    }

    /**
     * Show a toast message to the user
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Navigate to the details activity
     */
    private void navigateToDetailsActivity() {
        startActivity(new Intent(this, DetailsActivity.class));
    }

    //==========================================================================================
    // LOGIC LAYER - Business Logic and Data Management
    //==========================================================================================

    private FireBaseManager fireBaseManager;
    private String profilePhotoBase64;
    private boolean flag = true; // Flag for tracking if user skipped

    /**
     * Initialize logic components
     */
    private void initializeLogicComponents() {
        fireBaseManager = new FireBaseManager(this);
    }

    /**
     * Load existing profile picture if available
     */
    private void loadExistingProfilePicture() {
        fireBaseManager.readData(this, "Student", fireBaseManager.getUserid());
    }

    /**
     * Set the skip flag to indicate user skipped adding a profile picture
     */
    private void setSkipFlag() {
        flag = false;
    }

    /**
     * Handle the result from gallery image selection
     */
    private void handleActivityResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            processSelectedImage(result.getData());
        }
    }

    /**
     * Process the selected image data
     */
    private void processSelectedImage(Intent data) {
        if (data != null && data.getData() != null) {
            try {
                Uri imageUri = data.getData();
                Bitmap bitmap = loadBitmapFromUri(imageUri);

                // Fix image rotation using EXIF data
                Bitmap rotatedBitmap = rotateImageIfRequired(bitmap, imageUri);

                // Save and display the image
                saveAndDisplayImage(rotatedBitmap);

                showToast("Profile picture added.");
            } catch (IOException e) {
                e.printStackTrace();
                showToast("Failed to load the image.");
            }
        } else {
            // Use default profile image if no image is selected
            useDefaultProfileImage();
        }
    }

    /**
     * Load bitmap from URI
     */
    private Bitmap loadBitmapFromUri(Uri imageUri) throws IOException {
        return MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
    }

    /**
     * Save the image to Firebase and display it
     */
    private void saveAndDisplayImage(Bitmap bitmap) {
        // Convert to Base64 and save
        profilePhotoBase64 = convertTo64Base(bitmap);
        fireBaseManager.saveImage(profilePhotoBase64);

        // Display the selected photo
        displayProfileImage(bitmap);
    }

    /**
     * Use default profile image when no image is selected
     */
    private void useDefaultProfileImage() {
        Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_default_profile);
        String defaultBase64 = convertTo64Base(defaultBitmap);
        fireBaseManager.saveImage(defaultBase64);
    }

    /**
     * Fix image rotation based on EXIF metadata
     */
    private Bitmap rotateImageIfRequired(Bitmap img, Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            ExifInterface exif = new ExifInterface(inputStream);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int rotationAngle = determineRotationAngle(orientation);

            if (rotationAngle != 0) {
                return rotateBitmap(img, rotationAngle);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return img;
    }

    /**
     * Determine rotation angle based on EXIF orientation
     */
    private int determineRotationAngle(int orientation) {
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return 90;
            case ExifInterface.ORIENTATION_ROTATE_180:
                return 180;
            case ExifInterface.ORIENTATION_ROTATE_270:
                return 270;
            default:
                return 0;
        }
    }

    /**
     * Rotate bitmap by specified angle
     */
    private Bitmap rotateBitmap(Bitmap bitmap, int angle) {
        android.graphics.Matrix matrix = new android.graphics.Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * Convert Bitmap to Base64 String
     */
    private String convertTo64Base(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    //==========================================================================================
    // CALLBACK IMPLEMENTATIONS - Firebase Data Handling
    //==========================================================================================

    @Override
    public void oncallbackStudent(StudentUser student) {
        if (student != null && student.getProfilePhotoBase64() != null) {
            Bitmap pic = ImageUtils.convert64base(student.getProfilePhotoBase64());
            displayProfileImage(pic);
        }
    }

    @Override
    public void oncallbackArryStudent(ArrayList<StudentUser> students) {
        // Not used in this activity
    }

    @Override
    public void onCallbackTeacher(ArrayList<TeacherUser> teachers) {
        // Not used in this activity
    }
}
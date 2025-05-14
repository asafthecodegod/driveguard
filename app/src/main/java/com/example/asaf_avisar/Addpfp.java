package com.example.asaf_avisar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.asaf_avisar.activitys.DetailsActivity;
import com.example.asaf_avisar.activitys.TeacherDetailsActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Addpfp extends AppCompatActivity implements FirebaseCallback {

    private static final String TAG = "AddpfpActivity";

    // UI components
    private ImageView profileImageView;
    private Button addPhotoButton, skipButton, nextButton;

    // Firebase and logic
    private FireBaseManager fireBaseManager;
    private String profilePhotoBase64;
    private boolean flag = true;
    private boolean isTeacher = false;

    // Image selection launcher
    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpfp);

        initializeUIComponents();
        initializeLogicComponents();
        setupGalleryLauncher();
        setupEventListeners();

        // Check user type - just read directly from Teacher collection
        checkUserType();
    }

    private void initializeUIComponents() {
        profileImageView = findViewById(R.id.profileImageView);
        addPhotoButton = findViewById(R.id.addPhotoButton);
        skipButton = findViewById(R.id.skipButton);
        nextButton = findViewById(R.id.nextButton);
    }

    private void setupEventListeners() {
        nextButton.setOnClickListener(view -> navigateToDetailsActivity());
        addPhotoButton.setOnClickListener(view -> openGalleryForImageSelection());
        skipButton.setOnClickListener(view -> {
            setSkipFlag();
            showToast("Profile picture skipped.");
            navigateToDetailsActivity();
        });
    }

    private void setupGalleryLauncher() {
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleActivityResult
        );
    }

    private void openGalleryForImageSelection() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    private void displayProfileImage(Bitmap bitmap) {
        if (bitmap != null) {
            profileImageView.setImageBitmap(bitmap);
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void navigateToDetailsActivity() {
        Intent intent;

        // Simply use the isTeacher value to determine which activity to open
        if (isTeacher) {
            intent = new Intent(this, TeacherDetailsActivity.class);
            Log.d(TAG, "Navigating to TeacherDetailsActivity");
            showToast("Proceeding to teacher profile setup...");
        } else {
            intent = new Intent(this, DetailsActivity.class);
            Log.d(TAG, "Navigating to DetailsActivity");
            showToast("Proceeding to student details...");
        }

        intent.putExtra("isTeacher", isTeacher);
        startActivity(intent);
        finish();
    }

    private void initializeLogicComponents() {
        fireBaseManager = new FireBaseManager(this);
    }

    private void checkUserType() {
        // Try to read from Teacher collection first
        fireBaseManager.readData(this, "Teacher", fireBaseManager.getUserid());
    }

    private void setSkipFlag() {
        flag = false;
    }

    private void handleActivityResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            processSelectedImage(result.getData());
        }
    }

    private void processSelectedImage(Intent data) {
        if (data != null && data.getData() != null) {
            try {
                Uri imageUri = data.getData();
                Bitmap bitmap = loadBitmapFromUri(imageUri);
                Bitmap rotatedBitmap = rotateImageIfRequired(bitmap, imageUri);
                saveAndDisplayImage(rotatedBitmap);
                showToast("Profile picture added.");
            } catch (IOException e) {
                e.printStackTrace();
                showToast("Failed to load the image.");
            }
        } else {
            useDefaultProfileImage();
        }
    }

    private Bitmap loadBitmapFromUri(Uri imageUri) throws IOException {
        return MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
    }

    private void saveAndDisplayImage(Bitmap bitmap) {
        profilePhotoBase64 = convertTo64Base(bitmap);

        if (isTeacher) {
            // Save to Teacher collection
            TeacherUser teacher = new TeacherUser();
            teacher.setProfilePhotoBase64(profilePhotoBase64);
            teacher.setTeacher(true); // Explicitly set the teacher flag
            fireBaseManager.updateUser(teacher);
        } else {
            // Save to Student collection
            StudentUser student = new StudentUser();
            student.setProfilePhotoBase64(profilePhotoBase64);
            student.setTeacher(false); // Make sure student flag is false
            fireBaseManager.updateUser(student);
        }

        displayProfileImage(bitmap);
    }

    private void useDefaultProfileImage() {
        Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_default_profile);
        String defaultBase64 = convertTo64Base(defaultBitmap);

        if (isTeacher) {
            // Save to Teacher collection
            TeacherUser teacher = new TeacherUser();
            teacher.setProfilePhotoBase64(defaultBase64);
            teacher.setTeacher(true); // Explicitly set the teacher flag
            fireBaseManager.updateUser(teacher);
        } else {
            // Save to Student collection
            StudentUser student = new StudentUser();
            student.setProfilePhotoBase64(defaultBase64);
            student.setTeacher(false); // Make sure student flag is false
            fireBaseManager.updateUser(student);
        }
    }

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

    private Bitmap rotateBitmap(Bitmap bitmap, int angle) {
        android.graphics.Matrix matrix = new android.graphics.Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private String convertTo64Base(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    // Firebase Callback Implementations

    @Override
    public void onCallbackSingleTeacher(TeacherUser teacher) {
        if (teacher != null) {
            // Get the isTeacher flag directly from the TeacherUser object
            isTeacher = teacher.isTeacher(); // Use the boolean field from TeacherUser
            Log.d(TAG, "Teacher found with isTeacher=" + isTeacher);

            if (teacher.getProfilePhotoBase64() != null) {
                Bitmap pic = ImageUtils.convert64base(teacher.getProfilePhotoBase64());
                displayProfileImage(pic);
            }

            if (isTeacher) {
                showToast("Welcome, Teacher! Please complete your profile.");
            }
        } else {
            // No teacher found, check if they're a student
            Log.d(TAG, "Teacher not found, checking student collection");
            isTeacher = false;
            fireBaseManager.readData(this, "Student", fireBaseManager.getUserid());
        }
    }

    @Override
    public void oncallbackStudent(StudentUser student) {
        if (student != null) {
            // If we found a student, use the isTeacher flag from the StudentUser object
            // This lets us support a scenario where a student might also be a teacher
            isTeacher = student.isTeacher(); // Use the boolean field from StudentUser
            Log.d(TAG, "Student found with isTeacher=" + isTeacher);

            if (student.getProfilePhotoBase64() != null) {
                Bitmap pic = ImageUtils.convert64base(student.getProfilePhotoBase64());
                displayProfileImage(pic);
            }

            if (isTeacher) {
                showToast("Welcome, Teacher! Please complete your profile.");
            }
        } else {
            Log.d(TAG, "No student found");
            isTeacher = false;
        }
    }

    @Override
    public void oncallbackArryStudent(ArrayList<StudentUser> students) {
    }

    @Override
    public void onCallbackTeacher(ArrayList<TeacherUser> teachers) {

    }
}
package com.example.asaf_avisar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.example.asaf_avisar.activitys.menu;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Addpfp extends AppCompatActivity implements FirebaseCallback {

    private ImageView profileImageView;
    private Button addPhotoButton, skipButton,nextButton;
    private String profilePhotoBase64;
    private FireBaseManager fireBaseManager;
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpfp);

        flag = true;

        fireBaseManager = new FireBaseManager(this);
        fireBaseManager.studentData(this);
        profileImageView = findViewById(R.id.profileImageView);
        addPhotoButton = findViewById(R.id.addPhotoButton);
        skipButton = findViewById(R.id.skipButton);
        nextButton = findViewById(R.id.nextButton);

        // Activity result launcher for selecting a photo
        ActivityResultLauncher<Intent> getGalleryActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleActivityResult
        );

        nextButton.setOnClickListener(view -> {
            startActivity(new Intent(this, DetailsActivity.class));
        });

        // Add photo button click listener
        addPhotoButton.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            getGalleryActivityResultLauncher.launch(intent);
        });

        // Skip button click listener
        skipButton.setOnClickListener(view -> {
            // Handle skipping the photo selection (e.g., save without photo)
            flag = false;
            Toast.makeText(this, "Profile picture skipped.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, DetailsActivity.class));
        });
    }

    private void handleActivityResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            Intent data = result.getData();
            if (data != null && data.getData() != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    // Convert to Base64 and save
                    profilePhotoBase64 = convertTo64Base(bitmap);
                    fireBaseManager.saveImage(profilePhotoBase64);


                    // Display the selected photo
                    profileImageView.setImageBitmap(bitmap);

                    Toast.makeText(this, "Profile picture added.", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to load the image.", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_default_profile);
                String defaultBase64 = convertTo64Base(defaultBitmap);
                fireBaseManager.saveImage(defaultBase64);


            }
        }
    }

    // Convert Bitmap to Base64 String
    private String convertTo64Base(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    @Override
    public void oncallbackArryStudent(ArrayList<StudentUser> students) {

    }

    @Override
    public void oncallbackStudent(StudentUser student) {

            StudentUser studentUser = student;

            Bitmap pic = studentUser.convert64BaseToBitmap(student.getProfilePhotoBase64());
            profileImageView.setImageBitmap(pic);


    }

    @Override
    public void onCallbackTeacher(ArrayList<TeacherUser> teachers) {

    }
}

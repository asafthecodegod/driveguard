package com.example.asaf_avisar;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class StudentProflie extends AppCompatActivity {

    private ImageView profilePicture;
    private TextView studentName, driverType, studentCity, licenseDate, theoryStatus, greenFormStatus, lessonCounter;
    private Button btnAddFriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_proflie);

        // Initialize views
        profilePicture = findViewById(R.id.profile_picture);
        studentName = findViewById(R.id.student_name);
        driverType = findViewById(R.id.driver_type);
        studentCity = findViewById(R.id.student_city);
        licenseDate = findViewById(R.id.license_date);
        theoryStatus = findViewById(R.id.theory_status);
        greenFormStatus = findViewById(R.id.green_form_status);
        lessonCounter = findViewById(R.id.lesson_counter);
        btnAddFriend = findViewById(R.id.btn_add_friend);

        // Retrieve the StudentUser object passed from previous activity
        StudentUser studentUser = (StudentUser) getIntent().getSerializableExtra("STUDENT_USER");

        // Populate data
        if (studentUser != null) {
            studentName.setText(studentUser.getName());
            driverType.setText("Driver Type: " + studentUser.isType());
            studentCity.setText("City: " + studentUser.getCity());
            licenseDate.setText("License: " + (studentUser.getLicenseDate() != null ? studentUser.getLicenseDate().toString() : "On the Way"));
            theoryStatus.setText(studentUser.isTheory() ? "Completed" : "Not Completed");
            greenFormStatus.setText(studentUser.isGreenform() ? "Submitted" : "Not Submitted");
            lessonCounter.setText(String.valueOf(studentUser.getLessonCounter()));
        }

        // Set up "Add Friend" button
        btnAddFriend.setOnClickListener(v -> {
            // Handle friend request logic here (e.g., Firebase update)
            Toast.makeText(this, "Friend Request Sent!", Toast.LENGTH_SHORT).show();
        });
    }
}

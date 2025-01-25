package com.example.asaf_avisar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class StudentProflie extends AppCompatActivity implements FirebaseCallback {

    private ImageView profilePicture;
    private TextView studentName, driverType, studentCity, licenseDate, theoryStatus, greenFormStatus, lessonCounter;
    private Button btnAddFriend;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FireBaseManager fireBaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_proflie);
        fireBaseManager = new FireBaseManager(this);
        Intent intent = getIntent();
        if(intent.getStringExtra("STUDENT_ID") != null)
            fireBaseManager.readData(this,"Student",intent.getStringExtra("STUDENT_ID"));
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


        // Set up "Add Friend" button
        btnAddFriend.setOnClickListener(v -> {
            // Handle friend request logic here (e.g., Firebase update)
            Toast.makeText(this, "Friend Request Sent!", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void oncallbackArryStudent(ArrayList<StudentUser> students) {

    }

    @Override
    public void oncallbackStudent(StudentUser student) {

                studentName.setText(student.getName());
        driverType.setText("Driver Type: " + student.isType());
        studentCity.setText("City: " + student.getCity());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        licenseDate.setText("License: " + (student.getLicenseDate() != null ?
                dateFormat.format(student.getLicenseDate()) : "On the Way"));

        theoryStatus.setText(student.isTheory() ? "Completed" : "Not Completed");
        greenFormStatus.setText(student.isGreenform() ? "Submitted" : "Not Submitted");
        lessonCounter.setText("Lessons: " + student.getLessonCounter());

    }

    @Override
    public void onCallbackTeacher(ArrayList<TeacherUser> teachers) {

    }
}

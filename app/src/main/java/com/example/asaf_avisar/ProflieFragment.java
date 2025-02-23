package com.example.asaf_avisar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ProflieFragment extends Fragment implements FirebaseCallback {

    private ImageView profilePicture;
    private TextView studentName, driverType, studentCity, licenseDate, theoryStatus, greenFormStatus, lessonCounter;
    private Button btnAddFriend;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FireBaseManager fireBaseManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fireBaseManager = new FireBaseManager(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_proflie, container, false);

        // Initialize views
        profilePicture = view.findViewById(R.id.profile_image);
        studentName = view.findViewById(R.id.student_name);
        driverType = view.findViewById(R.id.driver_type);
        studentCity = view.findViewById(R.id.student_city);
        licenseDate = view.findViewById(R.id.license_date);
        theoryStatus = view.findViewById(R.id.theory_status);
        greenFormStatus = view.findViewById(R.id.green_form_status);
        lessonCounter = view.findViewById(R.id.lesson_counter);
        btnAddFriend = view.findViewById(R.id.btn_add_friend);

        // Load data
        String studentId = getArguments() != null ? getArguments().getString("STUDENT_ID") : fireBaseManager.getUserid();
        fireBaseManager.readData(this, "Student", studentId);

        // Set up "Add Friend" button
        btnAddFriend.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Friend Request Sent!", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    @Override
    public void oncallbackArryStudent(ArrayList<StudentUser> students) {
        // Not used for now
    }

    @Override
    public void oncallbackStudent(StudentUser student) {
        String base64Photo = student.getProfilePhotoBase64();
        Log.d("ProfilePhoto", "Base64: " + base64Photo);
        if (base64Photo != null) {
            Bitmap bitmapPhoto = StudentUser.convert64BaseToBitmap(base64Photo);
            if (bitmapPhoto != null) {
                profilePicture.setImageBitmap(bitmapPhoto);
            } else {
                profilePicture.setImageResource(R.drawable.headerbkg);
            }
        }

        studentName.setText(student.getName());
        driverType.setText(student.isType() == 0 ? "Driver Type: manual" : "Driver Type: automatic");
        studentCity.setText("City: " + student.getCity());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        licenseDate.setText("License: " + (student.getLicenseDate() != null ? dateFormat.format(student.getLicenseDate()) : "On the Way There!"));

        theoryStatus.setText(student.isTheory() ? "Completed" : "Not Completed");
        greenFormStatus.setText(student.isGreenform() ? "Submitted" : "Not Submitted");
        lessonCounter.setText("Lessons: " + student.getLessonCounter());
    }

    @Override
    public void onCallbackTeacher(ArrayList<TeacherUser> teachers) {
        // Not used for now
    }
}

//package com.example.asaf_avisar;
//
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.firebase.auth.FirebaseAuth;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Locale;
//
//public class StudentProfile extends AppCompatActivity implements FirebaseCallback {
//
//    private ImageView profilePicture;
//    private TextView studentName, driverType, studentCity, licenseDate, theoryStatus, greenFormStatus, lessonCounter;
//    private Button btnAddFriend;
//    private FirebaseAuth auth = FirebaseAuth.getInstance();
//    private FireBaseManager fireBaseManager;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_student_proflie);
//        fireBaseManager = new FireBaseManager(this);
//        Intent intent = getIntent();
//        if (intent.getStringExtra("STUDENT_ID") != null)
//            fireBaseManager.readData(this, "Student", intent.getStringExtra("STUDENT_ID"));
//        else
//            fireBaseManager.readData(this, "Student", fireBaseManager.getUserid());
//        // Initialize views
//        profilePicture = findViewById(R.id.profile_image);
//        studentName = findViewById(R.id.student_name);
//        driverType = findViewById(R.id.driver_type);
//        studentCity = findViewById(R.id.student_city);
//        licenseDate = findViewById(R.id.license_date);
//        theoryStatus = findViewById(R.id.theory_status);
//        greenFormStatus = findViewById(R.id.green_form_status);
//        lessonCounter = findViewById(R.id.lesson_counter);
//        btnAddFriend = findViewById(R.id.btn_add_friend);
//
//
//        // Set up "Add Friend" button
//        btnAddFriend.setOnClickListener(v -> {
//            // Handle friend request logic here (e.g., Firebase update)
//            Toast.makeText(this, "Friend Request Sent!", Toast.LENGTH_SHORT).show();
//        });
//    }
//
//    @Override
//    public void oncallbackArryStudent(ArrayList<StudentUser> students) {
//
//    }
//
////    @Override
////    public void oncallbackStudent(StudentUser student) {
////        String base64Photo = student.getProfilePhotoBase64();
////        Log.d("ProfilePhoto", "Base64: " + base64Photo);
//        //if (base64Photo != null) {
//
////            Bitmap bitmapPhoto = StudentUser.convert64BaseToBitmap(base64Photo);
////            if (bitmapPhoto != null) {
////                profilePicture.setImageBitmap(bitmapPhoto);  // Correct ImageView
////            } else {
////               // Optionally handle the case where the image couldn't be decoded
////               profilePicture.setImageResource(R.drawable.headerbkg);
////            }
//            // Setting other data
////        studentName.setText(student.getName());
////        if(student.isType() == 0)
////            driverType.setText("Driver Type:manual");
////        else
////            driverType.setText("Driver Type: automatic");
////
////        studentCity.setText("City: " + student.getCity() );
////
////        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
////        licenseDate.setText("License: " + (student.getLicenseDate() != null ?
////                dateFormat.format(student.getLicenseDate()) : "On the Way There!"));
////
////        theoryStatus.setText(student.isTheory() ? "Completed" : "Not Completed");
////        greenFormStatus.setText(student.isGreenform() ? "Submitted" : "Not Submitted");
////        lessonCounter.setText("Lessons: " + student.getLessonCounter());
////    }
//
//
////    @Override
////    public void onCallbackTeacher(ArrayList<TeacherUser> teachers) {
////
////    }
////}
//
//    }
//}
//
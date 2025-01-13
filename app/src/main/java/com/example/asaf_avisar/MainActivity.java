package com.example.asaf_avisar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, FirebaseCallback {
    private ImageButton logout;
    private FireBaseManager fireBaseManager;
    private ArrayList<TeacherUser> teacher = new ArrayList<>();
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the logout button and set its click listener
        logout = findViewById(R.id.logoutButton);
        logout.setOnClickListener(this);

        // Initialize Firebase manager to fetch teacher data
        fireBaseManager = new FireBaseManager(this);
        fireBaseManager.teacherData(this);

        // Adding some sample data for testing purposes (Replace this with Firebase data)
        for (int i = 0; i < 50; i++) {
            teacher.add(new TeacherUser("gili", "gili@gmail.com", "123456", new Date(2000, 8, 1), 234));
        }

        // Set up the main RecyclerView
        recyclerView = findViewById(R.id.viewteacher);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Add a button to trigger the dialog
        Button showDialogButton = findViewById(R.id.showDialogButton);
        showDialogButton.setOnClickListener(v -> showRecyclerViewDialog());
    }

    @Override
    public void onClick(View v) {
        if (v == logout) {
            fireBaseManager.logout();
        }
    }

    @Override
    public void oncallbackStudent(StudentUser user) {
        // Handle student callback
    }

    @Override
    public void onCallbackTeacher(ArrayList<TeacherUser> teachers) {
        // Update the main RecyclerView with teacher data
        TeacherUserAdapter teacherUserAdapter = new TeacherUserAdapter(teachers);
        recyclerView.setAdapter(teacherUserAdapter);
    }

    // Method to show the RecyclerView dialog
    private void showRecyclerViewDialog() {
        // Inflate the custom dialog layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.teacher_list, null);

        // Find the RecyclerView inside the dialog layout (correct ID)
        RecyclerView dialogRecyclerView = dialogView.findViewById(R.id.viewteacher);

        // Set up the layout manager for the RecyclerView inside the dialog
        dialogRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create the adapter for the RecyclerView inside the dialog and set it
        TeacherUserAdapter adapter = new TeacherUserAdapter(teacher);
        dialogRecyclerView.setAdapter(adapter);

        // Create and configure the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(dialogView);

        // Create the dialog instance
        AlertDialog dialog = builder.create();

        // Dismiss button functionality
        Button dismissButton = dialogView.findViewById(R.id.dismissDialogButton);
        dismissButton.setOnClickListener(v -> dialog.dismiss());

        // Show the dialog
        dialog.show();
    }
}

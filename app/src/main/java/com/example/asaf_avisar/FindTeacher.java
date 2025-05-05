package com.example.asaf_avisar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Date;

/**
 * Activity for finding and displaying teachers.
 * This class is organized in layers: View (main class), Controller (inner class),
 * and Callback interface (inner interface).
 */
public class FindTeacher extends AppCompatActivity implements View.OnClickListener, FirebaseCallback {

    // ============================
    // === VIEW LAYER (UI COMPONENTS) ===
    // ============================
    private RecyclerView recyclerView;
    private TeacherController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_teacher);

        // Initialize controller
        controller = new TeacherController();

        // Set up the main RecyclerView
        recyclerView = findViewById(R.id.viewteacher);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Add a button to trigger the dialog
        Button showDialogButton = findViewById(R.id.showDialogButton);
        showDialogButton.setOnClickListener(v -> showRecyclerViewDialog());

        // Load teacher data
        controller.loadTeacherData();
    }

    /**
     * Shows a dialog with the RecyclerView of teachers
     */
    private void showRecyclerViewDialog() {
        // Inflate the custom dialog layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.teacher_list, null);

        // Find the RecyclerView inside the dialog layout
        RecyclerView dialogRecyclerView = dialogView.findViewById(R.id.viewteacher);

        // Set up the layout manager for the RecyclerView inside the dialog
        dialogRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create the adapter for the RecyclerView inside the dialog and set it
        TeacherUserAdapter adapter = new TeacherUserAdapter(controller.getTeacherList());
        dialogRecyclerView.setAdapter(adapter);

        // Create and configure the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(FindTeacher.this);
        builder.setView(dialogView);

        // Create the dialog instance
        AlertDialog dialog = builder.create();

        // Show the dialog
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        // Implemented for View.OnClickListener but not used
    }

    // FirebaseCallback implementations (View layer)
    @Override
    public void oncallbackArryStudent(ArrayList<StudentUser> students) {
        // Not used in this activity
    }

    @Override
    public void oncallbackStudent(StudentUser user) {
        // Not used in this activity
    }

    @Override
    public void onCallbackTeacher(ArrayList<TeacherUser> teachers) {
        // Update the UI with teacher data
        controller.updateTeacherList(teachers);
        updateTeacherList();
    }

    /**
     * Updates the RecyclerView with the current list of teachers
     */
    private void updateTeacherList() {
        TeacherUserAdapter teacherUserAdapter = new TeacherUserAdapter(controller.getTeacherList());
        recyclerView.setAdapter(teacherUserAdapter);
    }

    // ============================
    // === CONTROLLER LAYER (BUSINESS LOGIC) ===
    // ============================
    /**
     * Controller class responsible for business logic related to teachers
     */
    private class TeacherController {
        private final ArrayList<TeacherUser> teacherList = new ArrayList<>();
        private final FireBaseManager fireBaseManager;

        /**
         * Constructor for TeacherController
         */
        public TeacherController() {
            fireBaseManager = new FireBaseManager(FindTeacher.this);
        }

        /**
         * Loads teacher data from Firebase
         */
        public void loadTeacherData() {
            fireBaseManager.teacherData(FindTeacher.this);

            // Add sample data for testing (should be removed in production)
            addSampleTeacherData();
        }

        /**
         * Adds sample teacher data for testing
         */
        private void addSampleTeacherData() {
            for (int i = 0; i < 50; i++) {
                teacherList.add(new TeacherUser("gili", "gili@gmail.com", "123456", new Date(2000, 8, 1), 234));
            }
        }

        /**
         * Updates the teacher list with data from Firebase
         * @param teachers List of teachers from Firebase
         */
        public void updateTeacherList(ArrayList<TeacherUser> teachers) {
            teacherList.clear();
            teacherList.addAll(teachers);
        }

        /**
         * Gets the current list of teachers
         * @return ArrayList of TeacherUser objects
         */
        public ArrayList<TeacherUser> getTeacherList() {
            return teacherList;
        }
    }
}
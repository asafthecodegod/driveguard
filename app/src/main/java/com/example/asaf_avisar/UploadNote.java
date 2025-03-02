package com.example.asaf_avisar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

import com.example.asaf_avisar.activitys.Post;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.Date;

public class UploadNote extends Fragment implements FirebaseCallback {

    private TextInputEditText noteInput;
    private TextInputEditText noteBodyInput;
    private MaterialButton saveNoteButton;
    private TabLayout tabLayout;
    private FireBaseManager fireBaseManager;
    private String userName;

    public UploadNote() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upload_note, container, false);



        fireBaseManager = new FireBaseManager(requireContext());
        String studentId = getArguments() != null ? getArguments().getString("STUDENT_ID") : fireBaseManager.getUserid();
        fireBaseManager.readData(this, "Student", studentId);
        // Initialize views
        noteInput = view.findViewById(R.id.noteTitleInput);
        noteBodyInput = view.findViewById(R.id.noteBodyInput);
        saveNoteButton = view.findViewById(R.id.postButton);
        tabLayout = view.findViewById(R.id.tabLayout);

        // Handle the "Save Note" button click
        saveNoteButton.setOnClickListener(v -> {
            String note = noteBodyInput.getText().toString();
            if (!note.isEmpty()) {
                // Process the note (for example, save it to the database)
                // You can add logic here to pass the note back to the previous fragment or activity
                Post post = new Post(userName,noteInput.getText().toString(),0,note,0,new Date());
                fireBaseManager.savePost(post);

                Toast.makeText(getContext(), "Note saved: " + note, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Note cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle tab selection
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        // "Text Only" tab selected
                        // Stay in the current fragment or update UI as needed
                        break;
                    case 1:
                        // "Image Only" tab selected, navigate to the Add Photo Fragment
                        navigateToAddPhotoFragment();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Handle tab unselection if needed
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Handle reselection if needed
            }
        });

        return view;
    }

    // Method to navigate to the Add Photo Fragment
    private void navigateToAddPhotoFragment() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new UploadPhoto()); // Make sure the container is defined in your activity
        transaction.addToBackStack(null);  // Adds the transaction to the back stack
        transaction.commit();
    }

    @Override
    public void oncallbackArryStudent(ArrayList<StudentUser> students) {

    }

    @Override
    public void oncallbackStudent(StudentUser student) {
        userName =student.getName();

    }

    @Override
    public void onCallbackTeacher(ArrayList<TeacherUser> teachers) {

    }
}

package com.example.asaf_avisar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView; // Correct import
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Date;

public class FriendFragment extends Fragment implements FirebaseCallback {

    private RecyclerView recyclerView;
    private SearchView searchBar; // Keep the correct SearchView import
    private StudentUserAdapter studentUserAdapter; // Adapter for StudentUser
    private ArrayList<StudentUser> students = new ArrayList<>(); // Full student list
    private ArrayList<StudentUser> filteredStudents = new ArrayList<>(); // Filtered student list
    private FireBaseManager fireBaseManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        View rootView = inflater.inflate(R.layout.fragment_friend, container, false);

        // Initialize views
        searchBar = rootView.findViewById(R.id.searchBar); // Use the correct SearchView reference
        recyclerView = rootView.findViewById(R.id.viewteacher); // Assuming the same RecyclerView ID is used

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        studentUserAdapter = new StudentUserAdapter(getContext(), filteredStudents); // Set up adapter for StudentUser
        recyclerView.setAdapter(studentUserAdapter);

        // Initialize Firebase manager to fetch student data
        fireBaseManager = new FireBaseManager(getContext());
        fireBaseManager.studentData(this); // Fetch student data using FirebaseManager

        // Add sample data for testing (Replace this with Firebase data)
        for (int i = 0; i < 50; i++) {
            students.add(new StudentUser("student" + i, "student" + i + "@gmail.com", "123456", new Date(2000, 8, 1)));
        }
        filteredStudents.addAll(students); // Initially display all students
        studentUserAdapter.notifyDataSetChanged();

        // Set up search bar functionality
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // We don't handle the submit action
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterStudents(newText); // Call filter method on text change
                return true;
            }
        });

        return rootView;
    }

    // Filter the student list based on the search query
    private void filterStudents(String query) {
        filteredStudents.clear();
        for (StudentUser studentUser : students) {
            if (studentUser.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredStudents.add(studentUser);
            }
        }
        studentUserAdapter.notifyDataSetChanged();
    }

    @Override
    public void oncallbackArryStudent(ArrayList<StudentUser> studentsFromFirebase) {
        students.clear();
        students.addAll(studentsFromFirebase); // Populate the full student list
        filteredStudents.clear();
        filteredStudents.addAll(studentsFromFirebase); // Update filtered list
        studentUserAdapter.notifyDataSetChanged();
    }

    @Override
    public void oncallbackStudent(StudentUser user) {
        // Handle a single student callback if needed
    }

    @Override
    public void onCallbackTeacher(ArrayList<TeacherUser> teachers) {
        // This method is unused in this fragment for StudentUser
    }
}

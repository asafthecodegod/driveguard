package com.example.asaf_avisar.activitys;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asaf_avisar.FireBaseManager;
import com.example.asaf_avisar.callbacks.FirebaseCallback;
import com.example.asaf_avisar.R;
import com.example.asaf_avisar.objects.StudentUser;
import com.example.asaf_avisar.objects.TeacherUser;
import com.example.asaf_avisar.adapters.TeacherUserAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * Activity for finding and displaying teachers with search and sort functionality.
 * This class is organized in layers: View (main class), Controller (inner class).
 */
public class FindTeacher extends AppCompatActivity implements FirebaseCallback, TeacherUserAdapter.OnTeacherClickListener {

    // ============================
    // === VIEW LAYER (UI COMPONENTS) ===
    // ============================
    private RecyclerView recyclerView;
    private SearchView searchView;
    private Spinner sortSpinner;
    private TextView teacherCountTextView;
    private ProgressBar progressBar;
    private TeacherController controller;

    // Sort options
    private static final String[] SORT_OPTIONS = {
            "Name (A-Z)",
            "Name (Z-A)",
            "Rating (High to Low)",
            "Rating (Low to High)"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_teacher);

        // Initialize controller
        controller = new TeacherController();

        // Initialize UI components
        initializeViews();
        setupSearchView();
        setupSortSpinner();

        // Load teacher data
        showProgress(true);
        controller.loadTeacherData();
    }

    /**
     * Initialize all view components
     */
    private void initializeViews() {
        recyclerView = findViewById(R.id.viewteacher);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchView = findViewById(R.id.searchView);
        sortSpinner = findViewById(R.id.sortSpinner);
        teacherCountTextView = findViewById(R.id.teacherCountTextView);
        progressBar = findViewById(R.id.progressBar);
    }

    /**
     * Set up the search view functionality
     */
    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                controller.filterTeachers(query);
                updateTeacherList();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                controller.filterTeachers(newText);
                updateTeacherList();
                return true;
            }
        });
    }

    /**
     * Set up the sort spinner functionality
     */
    private void setupSortSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, SORT_OPTIONS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter);

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                controller.sortTeachers(position);
                updateTeacherList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    /**
     * Show or hide progress indicator
     */
    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }


    /**
     * Navigate to the teacher profile activity
     * @param teacher The selected teacher
     */
//    private void navigateToTeacherProfile(TeacherUser teacher) {
//        Intent intent = new Intent(this, TeacherProfileActivity.class);
//        // Pass teacher data to the profile activity
//        intent.putExtra("TEACHER_ID", teacher.getTeacherId());
//        intent.putExtra("TEACHER_NAME", teacher.getName());
//        // Add any other teacher data you want to pass
//
//        startActivity(intent);
//    }

    // FirebaseCallback implementations
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
        showProgress(false);
        updateTeacherList();
    }

    @Override
    public void onCallbackSingleTeacher(TeacherUser teacher) {

    }

    /**
     * Updates the RecyclerView with the current filtered and sorted list of teachers
     */
    private void updateTeacherList() {
        ArrayList<TeacherUser> displayList = controller.getDisplayList();
        TeacherUserAdapter teacherUserAdapter = new TeacherUserAdapter(displayList, this);
        recyclerView.setAdapter(teacherUserAdapter);

        // Update teacher count text
        int count = displayList.size();
        teacherCountTextView.setText(count + " teacher" + (count != 1 ? "s" : "") + " found");
    }

    @Override
    public void onTeacherClick(TeacherUser teacher, int position) {

    }

    // ============================
    // === CONTROLLER LAYER (BUSINESS LOGIC) ===
    // ============================
    /**
     * Controller class responsible for business logic related to teachers
     */
    private class TeacherController {
        private final ArrayList<TeacherUser> masterTeacherList = new ArrayList<>();
        private final ArrayList<TeacherUser> filteredTeacherList = new ArrayList<>();
        private final FireBaseManager fireBaseManager;
        private String currentQuery = "";
        private int currentSortOption = 0;

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
            // Add different names and ratings for better testing
            String[] names = {"Alice", "Bob", "Charlie", "David", "Emma", "Frank", "Grace", "Hannah", "Ian", "Julia"};
            int[] ratings = {5, 4, 3, 4, 5, 2, 4, 3, 5, 4};

            for (int i = 0; i < 10; i++) {
                TeacherUser teacher = new TeacherUser(
                        names[i],
                        names[i].toLowerCase() + "@example.com",
                        "123456",
                        new Date(2000, 8, 1),
                        ratings[i]
                );
                // Set a unique ID for each sample teacher
                teacher.setTeacherId("sample_" + i);
                masterTeacherList.add(teacher);
            }

            // Initialize filtered list with all teachers
            filteredTeacherList.clear();
            filteredTeacherList.addAll(masterTeacherList);

            // Apply initial sort
            sortTeachers(currentSortOption);
        }

        /**
         * Updates the master teacher list with data from Firebase
         * @param teachers List of teachers from Firebase
         */
        public void updateTeacherList(ArrayList<TeacherUser> teachers) {
            masterTeacherList.clear();
            masterTeacherList.addAll(teachers);

            // Re-apply current filter and sort
            filterTeachers(currentQuery);
        }

        /**
         * Filters the teacher list based on search query
         * @param query Search query string
         */
        public void filterTeachers(String query) {
            currentQuery = query.toLowerCase().trim();

            filteredTeacherList.clear();

            if (currentQuery.isEmpty()) {
                // No filter, add all teachers
                filteredTeacherList.addAll(masterTeacherList);
            } else {
                // Filter teachers by name
                for (TeacherUser teacher : masterTeacherList) {
                    if (teacher.getName().toLowerCase().contains(currentQuery)) {
                        filteredTeacherList.add(teacher);
                    }
                }
            }

            // Re-apply current sort
            sortTeachers(currentSortOption);
        }

        /**
         * Sorts the filtered teacher list based on selected sort option
         * @param sortOption Sort option index
         */
        public void sortTeachers(int sortOption) {
            currentSortOption = sortOption;

            switch (sortOption) {
                case 0: // Name (A-Z)
                    Collections.sort(filteredTeacherList,
                            Comparator.comparing(TeacherUser::getName));
                    break;

                case 1: // Name (Z-A)
                    Collections.sort(filteredTeacherList,
                            Comparator.comparing(TeacherUser::getName).reversed());
                    break;

                case 2: // Rating (High to Low)
                    Collections.sort(filteredTeacherList,
                            Comparator.comparing(TeacherUser::getRank).reversed());
                    break;

                case 3: // Rating (Low to High)
                    Collections.sort(filteredTeacherList,
                            Comparator.comparing(TeacherUser::getRank));
                    break;
            }
        }

        /**
         * Gets the current filtered and sorted list of teachers for display
         * @return ArrayList of filtered and sorted TeacherUser objects
         */
        public ArrayList<TeacherUser> getDisplayList() {
            return filteredTeacherList;
        }

        /**
         * Gets the complete unfiltered list of teachers
         * @return ArrayList of all TeacherUser objects
         */
        public ArrayList<TeacherUser> getTeacherList() {
            return masterTeacherList;
        }
    }
}
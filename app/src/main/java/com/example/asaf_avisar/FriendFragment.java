package com.example.asaf_avisar;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Fragment for searching and displaying student profiles with enhanced UI,
 * search capabilities, and sorting options.
 */
public class FriendFragment extends Fragment {

    private static final String TAG = "FriendFragment";

    // UI Components
    private RecyclerView recyclerView;
    private SearchView searchBar;
    private Spinner sortSpinner;
    private TextView friendCountTextView;
    private ProgressBar progressBar;
    private LinearLayout emptyStateView;

    // Data
    private StudentUserAdapter studentUserAdapter;
    private ArrayList<StudentUser> students = new ArrayList<>();
    private ArrayList<StudentUser> filteredStudents = new ArrayList<>();

    // Sort options
    private static final String[] SORT_OPTIONS = {
            "Name (A-Z)",
            "Name (Z-A)",
            "Recent License First",
            "City",
            "Driver Type"
    };

    // Current state
    private String currentQuery = "";
    private int currentSortOption = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_friend, container, false);

        // Initialize views
        initializeViews(rootView);

        // Setup UI components
        setupRecyclerView();
        setupSearchBar();
        setupSortSpinner();

        // Load initial data
        showLoading(true);
        loadFirstPage();

        return rootView;
    }

    /**
     * Initialize all view references
     */
    private void initializeViews(View rootView) {
        searchBar = rootView.findViewById(R.id.searchBar);
        recyclerView = rootView.findViewById(R.id.viewteacher);
        sortSpinner = rootView.findViewById(R.id.sortSpinner);
        friendCountTextView = rootView.findViewById(R.id.friendCountTextView);
        progressBar = rootView.findViewById(R.id.progressBar);
        emptyStateView = rootView.findViewById(R.id.emptyStateView);
    }

    /**
     * Setup RecyclerView and its adapter
     */
    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        studentUserAdapter = new StudentUserAdapter(getContext(), filteredStudents);
        recyclerView.setAdapter(studentUserAdapter);
    }

    /**
     * Setup search bar functionality
     */
    private void setupSearchBar() {
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentQuery = newText;
                filterAndSortStudents();
                return true;
            }
        });
    }

    /**
     * Setup sort spinner with options
     */
    private void setupSortSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                SORT_OPTIONS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter);

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentSortOption = position;
                filterAndSortStudents();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    /**
     * Show or hide loading indicator
     */
    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    /**
     * Show empty state if no results
     */
    private void updateEmptyState() {
        if (filteredStudents.isEmpty()) {
            emptyStateView.setVisibility(View.VISIBLE);
        } else {
            emptyStateView.setVisibility(View.GONE);
        }
    }

    /**
     * Load first page of student data from Firebase
     */
    private void loadFirstPage() {
        DatabaseReference studentsRef = FirebaseDatabase
                .getInstance()
                .getReference("Student");

        // Query for first 15 students
        Query q = studentsRef.limitToFirst(15);
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                students.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    StudentUser u = child.getValue(StudentUser.class);
                    if (u != null) {
                        u.setId(child.getKey());
                        students.add(u);
                    }
                }

                showLoading(false);
                filterAndSortStudents();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "loadFirstPage:onCancelled", error.toException());
                showLoading(false);
            }
        });
    }

    /**
     * Combined method to filter and sort students based on current criteria
     */
    private void filterAndSortStudents() {
        // Step 1: Filter
        filteredStudents.clear();
        String query = currentQuery.toLowerCase().trim();

        if (query.isEmpty()) {
            // No filter, add all
            filteredStudents.addAll(students);
        } else {
            // Filter by name
            for (StudentUser u : students) {
                if (u.getName() != null && u.getName().toLowerCase().contains(query)) {
                    filteredStudents.add(u);
                }
            }
        }

        // Step 2: Sort
        sortStudents();

        // Step 3: Update UI
        studentUserAdapter.notifyDataSetChanged();
        updateFriendCount();
        updateEmptyState();
    }

    /**
     * Sort the filtered student list based on selected criteria
     */
    private void sortStudents() {
        switch (currentSortOption) {
            case 0: // Name (A-Z)
                Collections.sort(filteredStudents, new Comparator<StudentUser>() {
                    @Override
                    public int compare(StudentUser u1, StudentUser u2) {
                        if (u1.getName() == null) return 1;
                        if (u2.getName() == null) return -1;
                        return u1.getName().compareToIgnoreCase(u2.getName());
                    }
                });
                break;

            case 1: // Name (Z-A)
                Collections.sort(filteredStudents, new Comparator<StudentUser>() {
                    @Override
                    public int compare(StudentUser u1, StudentUser u2) {
                        if (u1.getName() == null) return 1;
                        if (u2.getName() == null) return -1;
                        return u2.getName().compareToIgnoreCase(u1.getName());
                    }
                });
                break;

            case 2: // Recent License First
                Collections.sort(filteredStudents, new Comparator<StudentUser>() {
                    @Override
                    public int compare(StudentUser u1, StudentUser u2) {
                        if (u1.getLicenseDate() == null) return 1;
                        if (u2.getLicenseDate() == null) return -1;
                        return u2.getLicenseDate().compareTo(u1.getLicenseDate());
                    }
                });
                break;

            case 3: // City
                Collections.sort(filteredStudents, new Comparator<StudentUser>() {
                    @Override
                    public int compare(StudentUser u1, StudentUser u2) {
                        if (u1.getCity() == null) return 1;
                        if (u2.getCity() == null) return -1;
                        return u1.getCity().compareToIgnoreCase(u2.getCity());
                    }
                });
                break;

            case 4: // Driver Type
                Collections.sort(filteredStudents, new Comparator<StudentUser>() {
                    @Override
                    public int compare(StudentUser u1, StudentUser u2) {
                        // Sort automatic first, then manual
                        return Boolean.compare(u1.isDriverType(), u2.isDriverType());
                    }
                });
                break;
        }
    }

    /**
     * Update the friend count text with current results
     */
    private void updateFriendCount() {
        int count = filteredStudents.size();
        friendCountTextView.setText(count + " friend" + (count != 1 ? "s" : "") + " found");
    }
}
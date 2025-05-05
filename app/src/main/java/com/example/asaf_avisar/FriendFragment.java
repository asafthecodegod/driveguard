package com.example.asaf_avisar;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

/**
 * The type Friend fragment.
 */
public class FriendFragment extends Fragment {

    private static final String TAG = "FriendFragment";
    private RecyclerView recyclerView;
    private SearchView searchBar;
    private StudentUserAdapter studentUserAdapter;
    private ArrayList<StudentUser> students = new ArrayList<>();
    private ArrayList<StudentUser> filteredStudents = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_friend, container, false);

        // find views
        searchBar    = rootView.findViewById(R.id.searchBar);
        recyclerView = rootView.findViewById(R.id.viewteacher);

        // setup RecyclerView + adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        studentUserAdapter = new StudentUserAdapter(getContext(), filteredStudents);
        recyclerView.setAdapter(studentUserAdapter);

        // load the first 50 students only
        loadFirstPage();

        // wire up search filter
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) { return false; }
            @Override public boolean onQueryTextChange(String newText) {
                filterStudents(newText);
                return true;
            }
        });

        return rootView;
    }

    private void loadFirstPage() {
        DatabaseReference studentsRef = FirebaseDatabase
                .getInstance()
                .getReference("Student");

        // only the first 50 entries
        Query q = studentsRef.limitToFirst(15);
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                students.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    StudentUser u = child.getValue(StudentUser.class);
                    if (u != null) {
                        u.setId(child.getKey());
                        students.add(u);
                    }
                }
                // update filtered & adapter
                filteredStudents.clear();
                filteredStudents.addAll(students);
                studentUserAdapter.notifyDataSetChanged();
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "loadFirstPage:onCancelled", error.toException());
            }
        });
    }

    private void filterStudents(String query) {
        filteredStudents.clear();
        String lower = query.toLowerCase();
        for (StudentUser u : students) {
            if (u.getName() != null && u.getName().toLowerCase().contains(lower)) {
                filteredStudents.add(u);
            }
        }
        studentUserAdapter.notifyDataSetChanged();
    }
}

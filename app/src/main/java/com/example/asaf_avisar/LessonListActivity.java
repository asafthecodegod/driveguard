package com.example.asaf_avisar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asaf_avisar.activitys.menu;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Lesson list activity.
 */
public class LessonListActivity extends AppCompatActivity implements FirebaseCallbackLessons {

    private RecyclerView recyclerView;
    private LessonAdapter adapter;
    private List<Lesson> lessonList;
    private Button addEventButton;
    private FireBaseManager fireBaseManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_list);
        fireBaseManager = new FireBaseManager(this);
        fireBaseManager.getEvent(this);

        recyclerView = findViewById(R.id.recycler_view_lessons);
        addEventButton = findViewById(R.id.button_add_event);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize lesson list
        lessonList = new ArrayList<>();
      //  loadSampleData();

        // Set up adapter
//        adapter = new LessonAdapter(this, lessonList);
//        recyclerView.setAdapter(adapter);

        // Set up the Add Event button
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LessonListActivity.this, AddLesson.class));
            }
        });
    }

    private void loadSampleData() {
        // Add some example lessons
        lessonList.add(new Lesson("Lesson", "01/01/2025", "10:00 AM", false));
        lessonList.add(new Lesson("Double Lesson", "05/01/2025", "12:00 PM", true));
    }

    private void addNewLesson() {
        // Add a new lesson to the list and notify the adapter
        lessonList.add(new Lesson("New Lesson", "01/02/2025", "2:00 PM", false));
        adapter.notifyItemInserted(lessonList.size() - 1); // Update the RecyclerView
    }

    @Override
    public void oncallbackArryLessons(ArrayList<Lesson> lessons) {
        adapter = new LessonAdapter(this, lessons);
        recyclerView.setAdapter(adapter);
    }
}

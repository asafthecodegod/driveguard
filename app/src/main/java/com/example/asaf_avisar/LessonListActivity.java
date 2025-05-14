package com.example.asaf_avisar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
public class LessonListActivity extends AppCompatActivity implements FirebaseCallbackLessons, FirebaseCallback {

    private RecyclerView recyclerView;
    private LessonAdapter adapter;
    private List<Lesson> lessonList;
    private Button addEventButton, goBackButton;
    private TextView investmentTitleTextView;
    private FireBaseManager fireBaseManager;
    private StudentUser currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_list);

        // Initialize Firebase
        fireBaseManager = new FireBaseManager(this);

        // Initialize UI components
        initializeUIComponents();

        // Fetch user data to get investment amount
        fireBaseManager.readData(this, "Student", fireBaseManager.getUserid());

        // Fetch lessons from Firebase
        fireBaseManager.getEvent(this);
    }

    /**
     * Initialize UI components and set up click listeners
     */
    private void initializeUIComponents() {
        // RecyclerView setup
        recyclerView = findViewById(R.id.recycler_view_lessons);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Investment title
        investmentTitleTextView = findViewById(R.id.tv_investment_title);
        investmentTitleTextView.setText("Your Learning Investment: Loading...");

        // Add Event button
        addEventButton = findViewById(R.id.button_add_event);
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LessonListActivity.this, AddLesson.class));
            }
        });

        // Go Back button
        goBackButton = findViewById(R.id.button_go_back);
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the main menu/home
                Intent intent = new Intent(LessonListActivity.this, menu.class);
                startActivity(intent);
                finish(); // Close this activity
            }
        });

        // Initialize lesson list
        lessonList = new ArrayList<>();
    }

    /**
     * Update the investment title with the user's investment amount
     */
    private void updateInvestmentTitle() {
        if (currentUser != null) {
            int investmentAmount = currentUser.getDrivingInvestment();
            investmentTitleTextView.setText("Your Learning Investment: ₪" + investmentAmount);
        } else {
            investmentTitleTextView.setText("Your Learning Investment: Not Available");
        }
    }

    @Override
    public void oncallbackArryLessons(ArrayList<Lesson> lessons) {
        if (lessons != null) {
            // Set up adapter with lessons
            adapter = new LessonAdapter(this, lessons);
            recyclerView.setAdapter(adapter);
        }
    }

    // Firebase callback for Student User data
    @Override
    public void oncallbackStudent(StudentUser user) {
        if (user != null) {
            this.currentUser = user;
            updateInvestmentTitle();
        }
    }

    @Override
    public void oncallbackArryStudent(ArrayList<StudentUser> students) {
        // Not used in this activity
    }

    @Override
    public void onCallbackTeacher(ArrayList<TeacherUser> teachers) {
        // Not used in this activity
    }

    @Override
    public void onCallbackSingleTeacher(TeacherUser teacher) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh lessons when returning to this activity
        fireBaseManager.getEvent(this);
        // Also refresh user data in case investment amount changed
        fireBaseManager.readData(this, "Student", fireBaseManager.getUserid());
    }
}
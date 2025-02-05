package com.example.asaf_avisar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyLessons extends AppCompatActivity {

    private CalendarView calendarView;
    private ListView eventListView;
    private TextView emptyMessage;
    private Button addEventButton;
    private DatabaseReference databaseReference;
    private ArrayList<String> eventList;
    private EventAdapter adapter;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_lessons);

        calendarView = findViewById(R.id.calendarView);
        eventListView = findViewById(R.id.eventListView);
        emptyMessage = findViewById(R.id.emptyMessage);
        addEventButton = findViewById(R.id.addEventButton);

        databaseReference = FirebaseDatabase.getInstance().getReference("events");
        eventList = new ArrayList<>();
        //adapter = new EventAdapter(this, eventList);
        //eventListView.setAdapter(adapter);

        selectedDate = ""; // Default until a date is selected

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
            loadEventsForDate(selectedDate);
        });

        addEventButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddLesson.class);
            intent.putExtra("selectedDate", selectedDate);
            startActivity(intent);
        });
    }

    private void loadEventsForDate(String date) {
        databaseReference.child(date).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        String event = data.getValue(String.class);
                        eventList.add(event);
                    }
                    emptyMessage.setVisibility(View.GONE);
                    eventListView.setVisibility(View.VISIBLE);
                } else {
                    emptyMessage.setVisibility(View.VISIBLE);
                    eventListView.setVisibility(View.GONE);
                }
               //adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}

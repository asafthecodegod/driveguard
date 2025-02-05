package com.example.asaf_avisar;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class AddLesson extends AppCompatActivity {

    private Spinner eventTypeSpinner;
    private TextView selectedTimeText,selectTimeButton;
    private Button addEventButton;
    private DatabaseReference databaseReference;
    private String selectedDate, selectedTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lesson);

        eventTypeSpinner = findViewById(R.id.spinner_event_type);
        selectedTimeText = findViewById(R.id.edit_text_time);
        selectTimeButton = findViewById(R.id.edit_text_date);
        addEventButton = findViewById(R.id.button_add_event);

        databaseReference = FirebaseDatabase.getInstance().getReference("events");

        selectedDate = getIntent().getStringExtra("selectedDate");

        String[] eventTypes = {"Lesson", "Double Lesson", "One and a Half Lesson", "Interior Test", "External Test"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, eventTypes);
        eventTypeSpinner.setAdapter(adapter);

        selectTimeButton.setOnClickListener(v -> openTimePicker());

        addEventButton.setOnClickListener(v -> saveEvent());
    }

    private void openTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
            selectedTime = hourOfDay + ":" + (minute1 < 10 ? "0" + minute1 : minute1);
            selectedTimeText.setText("Selected Time: " + selectedTime);
        }, hour, minute, true);

        timePickerDialog.show();
    }

    private void saveEvent() {
        if (selectedTime.isEmpty()) {
            Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show();
            return;
        }

        String eventType = eventTypeSpinner.getSelectedItem().toString();
        String eventDetails = eventType + " at " + selectedTime;

        databaseReference.child(selectedDate).push().setValue(eventDetails)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Event added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to add event", Toast.LENGTH_SHORT).show());
    }
}

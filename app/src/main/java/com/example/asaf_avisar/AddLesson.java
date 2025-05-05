package com.example.asaf_avisar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * The type Add lesson.
 */
public class AddLesson extends AppCompatActivity implements FirebaseCallbackLessons {

    private Spinner eventTypeSpinner;
    private EditText dateEditText, timeEditText;
    private Button addEventButton;
    private FireBaseManager fireBaseManager;
    private String selectedDate = "", selectedTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lesson);

        // Initialize UI elements
        eventTypeSpinner = findViewById(R.id.spinner_lesson_type);
        dateEditText = findViewById(R.id.edit_text_date);
        timeEditText = findViewById(R.id.edit_text_time);
        addEventButton = findViewById(R.id.button_add_lesson);

        // Firebase reference setup
        fireBaseManager = new FireBaseManager(this);
      //  fireBaseManager.getEvent(this);
        // Set up event types in the spinner
//        String[] eventTypes = {"Lesson", "Double Lesson", "One and a Half Lesson", "Interior Test", "External Test"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, eventTypes);
//        eventTypeSpinner.setAdapter(adapter);

        // Date and time pickers
        dateEditText.setOnClickListener(v -> openDatePicker());
        timeEditText.setOnClickListener(v -> openTimePicker());

        // Add lesson on button click
        addEventButton.setOnClickListener(v -> saveLesson());
    }

    private void openDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
            dateEditText.setText(selectedDate);
        }, year, month, day);
        datePickerDialog.show();
    }

    private void openTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
            selectedTime = String.format("%02d:%02d", hourOfDay, minute1);
            timeEditText.setText(selectedTime);
        }, hour, minute, true);
        timePickerDialog.show();
    }

    private void saveLesson() {
        if (selectedDate.isEmpty() || selectedTime.isEmpty()) {
            Toast.makeText(this, "Please select date and time", Toast.LENGTH_SHORT).show();
            return;
        }

        String lessonType = eventTypeSpinner.getSelectedItem().toString();
        Lesson lesson = new Lesson(lessonType, selectedDate, selectedTime, false);
        fireBaseManager.saveEvent(lesson);
        startActivity(new Intent(this, LessonListActivity.class));



    }

    @Override
    public void oncallbackArryLessons(ArrayList<Lesson> lessons) {


    }
}

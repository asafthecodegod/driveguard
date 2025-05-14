package com.example.asaf_avisar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Activity for adding new driving lessons to the schedule.
 */
public class AddLesson extends AppCompatActivity implements FirebaseCallbackLessons {

    //==========================================================================================
    // DISPLAY LAYER - UI Components and Display Methods
    //==========================================================================================

    // UI components
    private Spinner eventTypeSpinner;
    private EditText dateEditText, timeEditText;
    private Button addEventButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lesson);

        // Initialize UI and logic components
        initializeUIComponents();
        initializeLogicComponents();

        // Set up event listeners
        setupEventListeners();
    }

    /**
     * Initialize all UI components
     */
    private void initializeUIComponents() {
        eventTypeSpinner = findViewById(R.id.spinner_lesson_type);
        dateEditText = findViewById(R.id.edit_text_date);
        timeEditText = findViewById(R.id.edit_text_time);
        addEventButton = findViewById(R.id.button_add_lesson);
    }

    /**
     * Set up event listeners for interactive UI elements
     */
    private void setupEventListeners() {
        // Set up date and time picker dialogs
        dateEditText.setOnClickListener(v -> showDatePickerDialog());
        timeEditText.setOnClickListener(v -> showTimePickerDialog());

        // Set up add lesson button
        addEventButton.setOnClickListener(v -> handleAddLessonClick());
    }

    /**
     * Display date picker dialog
     */
    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Update selected date and display
                    String formattedDate = formatDate(selectedDay, selectedMonth, selectedYear);
                    updateSelectedDate(formattedDate);
                    dateEditText.setText(formattedDate);
                }, year, month, day);

        datePickerDialog.show();
    }

    /**
     * Display time picker dialog
     */
    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, selectedHour, selectedMinute) -> {
                    // Update selected time and display
                    String formattedTime = formatTime(selectedHour, selectedMinute);
                    updateSelectedTime(formattedTime);
                    timeEditText.setText(formattedTime);
                }, hour, minute, true);

        timePickerDialog.show();
    }

    /**
     * Display an error message to the user
     */
    private void showErrorMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Navigate to the lesson list screen
     */
    private void navigateToLessonList() {
        startActivity(new Intent(this, LessonListActivity.class));
        finish(); // Close this activity
    }

    //==========================================================================================
    // LOGIC LAYER - Business Logic and Data Management
    //==========================================================================================

    private FireBaseManager fireBaseManager;
    private String selectedDate = "";
    private String selectedTime = "";

    /**
     * Initialize logic components
     */
    private void initializeLogicComponents() {
        // Initialize Firebase manager
        fireBaseManager = new FireBaseManager(this);
    }

    /**
     * Handle add lesson button click
     */
    private void handleAddLessonClick() {
        if (validateInputs()) {
            createAndSaveLesson();
            navigateToLessonList();
        }
    }

    /**
     * Validate user inputs
     * @return true if inputs are valid, false otherwise
     */
    private boolean validateInputs() {
        if (selectedDate.isEmpty() || selectedTime.isEmpty()) {
            showErrorMessage("Please select date and time");
            return false;
        }
        return true;
    }

    /**
     * Create and save lesson to Firebase
     */
    private void createAndSaveLesson() {
        String lessonType = eventTypeSpinner.getSelectedItem().toString();
        Lesson lesson = new Lesson(lessonType, selectedDate, selectedTime, false);
        fireBaseManager.saveEvent(lesson);
    }

    /**
     * Update the selected date value
     */
    private void updateSelectedDate(String date) {
        selectedDate = date;
    }

    /**
     * Update the selected time value
     */
    private void updateSelectedTime(String time) {
        selectedTime = time;
    }

    /**
     * Format date values into a string
     */
    private String formatDate(int day, int month, int year) {
        return day + "/" + (month + 1) + "/" + year;
    }

    /**
     * Format time values into a string
     */
    private String formatTime(int hour, int minute) {
        return String.format("%02d:%02d", hour, minute);
    }

    //==========================================================================================
    // CALLBACK IMPLEMENTATIONS - Firebase Data Handling
    //==========================================================================================

    @Override
    public void oncallbackArryLessons(ArrayList<Lesson> lessons) {
        // Not used in this activity, but required by the interface
    }
}
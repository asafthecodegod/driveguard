package com.example.asaf_avisar.activitys;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.asaf_avisar.FireBaseManager;
import com.example.asaf_avisar.callbacks.FirebaseCallback;
import com.example.asaf_avisar.MyReceiver;
import com.example.asaf_avisar.R;
import com.example.asaf_avisar.objects.StudentUser;
import com.example.asaf_avisar.objects.TeacherUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Activity for capturing additional user details like license information,
 * driving preferences, and city.
 */
public class DetailsActivity extends AppCompatActivity implements FirebaseCallback {

    //==========================================================================================
    // DISPLAY LAYER - UI Components and Display Methods
    //==========================================================================================

    // View components
    private TextView licenseDateLabel, hello;
    private RadioGroup radioGroupDriveType, radioGroupTheory, radioGroupGreenFile, radioGroupLicense;
    private RadioButton radioManual, radioAutomatic, radioYesTheory, radioYesGreenFile;
    private Spinner citySpinner;
    private DatePicker licenseDatePicker;
    private Button submitButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Initialize UI components
        initializeUIComponents();

        // Initialize data components
        initializeDataComponents();

        // Set up UI event listeners
        setupEventListeners();
    }

    /**
     * Initialize all UI components
     */
    private void initializeUIComponents() {
        hello = findViewById(R.id.userName);
        licenseDateLabel = findViewById(R.id.licenseDateLabel);
        citySpinner = findViewById(R.id.citySpinner);
        radioGroupDriveType = findViewById(R.id.radioGroupDriveType);
        radioGroupTheory = findViewById(R.id.radioGroupTheory);
        radioGroupGreenFile = findViewById(R.id.radioGroupGreenFile);
        radioGroupLicense = findViewById(R.id.radioGroupLicense);
        radioManual = findViewById(R.id.radioManual);
        radioAutomatic = findViewById(R.id.radioAutomatic);
        radioYesTheory = findViewById(R.id.radioYesTheory);
        radioYesGreenFile = findViewById(R.id.radioYesGreenFile);
        licenseDatePicker = findViewById(R.id.licenseDatePicker);
        submitButton = findViewById(R.id.submitButton);

        // Initially hide the license date picker and label
        licenseDateLabel.setVisibility(View.GONE);
        licenseDatePicker.setVisibility(View.GONE);

        // Restrict future dates in DatePicker
        licenseDatePicker.setMaxDate(System.currentTimeMillis());
    }

    /**
     * Set up UI event listeners
     */
    private void setupEventListeners() {
        // Set up city spinner
        setupCitySpinner();

        // Show/hide license date picker based on selection
        radioGroupLicense.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioYesLicense) {
                showLicenseDatePicker(true);
            } else {
                showLicenseDatePicker(false);
            }
        });

        // Handle submit button click
        submitButton.setOnClickListener(v -> handleSubmit());
    }

    /**
     * Set up the city spinner with adapter and listener
     */
    private void setupCitySpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.israel_cities,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(adapter);

        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCity = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCity = "Not selected";
            }
        });
    }

    /**
     * Show or hide license date picker
     */
    private void showLicenseDatePicker(boolean show) {
        int visibility = show ? View.VISIBLE : View.GONE;
        licenseDateLabel.setVisibility(visibility);
        licenseDatePicker.setVisibility(visibility);
    }

    /**
     * Update greeting with user name
     */
    private void updateGreeting(String name) {
        if (name != null) {
            hello.setText("Hi, " + name);
        }
    }

    /**
     * Show a toast message to the user
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Navigate to menu activity
     */
    private void navigateToMenuActivity(Date licenseDate) {
        Intent intent = new Intent(DetailsActivity.this, menu.class);
        if (licenseDate != null) {
            intent.putExtra("LICENSE_DATE", licenseDate);
        }
        startActivity(intent);
    }

    //==========================================================================================
    // LOGIC LAYER - Business Logic and Data Management
    //==========================================================================================

    private String selectedCity = "Not selected"; // Default value
    private FireBaseManager fireBaseManager;
    private String userName;
    private StudentUser studentUser;

    /**
     * Initialize data components
     */
    private void initializeDataComponents() {
        fireBaseManager = new FireBaseManager(this);
        fireBaseManager.readData(this, "Student", fireBaseManager.getUserid());
    }

    /**
     * Handle submit button click
     */
    private void handleSubmit() {
        // Get all user input values
        int driveType = getDriveType();
        boolean theory = getTheoryStatus();
        boolean greenFile = getGreenFileStatus();
        boolean license = getLicenseStatus();
        Date licenseDate = getLicenseDate();

        // Validate license date if user has a license
        if (license && licenseDate == null) {
            showToast("Please provide your license date");
            return;
        }

        // Update student user object with new details
        updateStudentUser(driveType, theory, greenFile, license, licenseDate);

        // Save to Firebase
        fireBaseManager.updateUser(studentUser);

        // Navigate to menu activity
        navigateToMenuActivity(licenseDate);
    }

    /**
     * Update the StudentUser object with user inputs
     */
    private void updateStudentUser(int driveType, boolean theory, boolean greenFile,
                                   boolean license, Date licenseDate) {
        studentUser.setHasGreenForm(greenFile);
        studentUser.setHasLicense(license);
        studentUser.setLicenseDate(licenseDate);
        studentUser.setPassedTheory(theory);
        studentUser.setDriverType(driveType == 1);  // 1 = automatic
        studentUser.setCity(selectedCity);
    }

    /**
     * Get user-selected drive type
     * @return 0 for manual, 1 for automatic, -1 if none selected
     */
    private int getDriveType() {
        int selectedId = radioGroupDriveType.getCheckedRadioButtonId();
        if (selectedId == radioManual.getId()) return 0;
        if (selectedId == radioAutomatic.getId()) return 1;
        return -1;
    }

    /**
     * Get user-selected theory status
     */
    private boolean getTheoryStatus() {
        return radioGroupTheory.getCheckedRadioButtonId() == radioYesTheory.getId();
    }

    /**
     * Get user-selected green file status
     */
    private boolean getGreenFileStatus() {
        return radioGroupGreenFile.getCheckedRadioButtonId() == radioYesGreenFile.getId();
    }

    /**
     * Get user-selected license status
     */
    private boolean getLicenseStatus() {
        return radioGroupLicense.getCheckedRadioButtonId() == R.id.radioYesLicense;
    }

    /**
     * Get user-selected license date
     */
    private Date getLicenseDate() {
        if (licenseDatePicker.getVisibility() == View.VISIBLE) {
            int day = licenseDatePicker.getDayOfMonth();
            int month = licenseDatePicker.getMonth();
            int year = licenseDatePicker.getYear();

            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, day);

            // Validate: prevent future dates
            if (!validateLicenseDate(selectedDate)) {
                return null;
            }

            // Schedule notification for 3 months later
            scheduleNotification(selectedDate);

            return selectedDate.getTime();
        }
        return null;
    }

    /**
     * Validate the license date
     */
    private boolean validateLicenseDate(Calendar selectedDate) {
        // Prevent future dates
        Calendar today = Calendar.getInstance();
        if (selectedDate.after(today)) {
            showToast("Future dates are not allowed!");
            return false;
        }
        return true;
    }

    /**
     * Schedule notification for 3 months after license date
     */
    private void scheduleNotification(Calendar licenseDate) {
        Calendar notificationDate = (Calendar) licenseDate.clone();
        notificationDate.add(Calendar.MONTH, 3);

        Intent intent = new Intent(this, MyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, notificationDate.getTimeInMillis(), pendingIntent);
        }
    }

    //==========================================================================================
    // CALLBACK IMPLEMENTATIONS - Firebase Data Handling
    //==========================================================================================

    @Override
    public void oncallbackStudent(StudentUser user) {
        if (user != null) {
            userName = user.getName();
            studentUser = user;

            // Update UI with user data
            updateGreeting(userName);
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
}
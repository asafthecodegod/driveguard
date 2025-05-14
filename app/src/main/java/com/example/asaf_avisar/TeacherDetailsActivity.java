package com.example.asaf_avisar.activitys;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.asaf_avisar.FireBaseManager;
import com.example.asaf_avisar.FirebaseCallback;
import com.example.asaf_avisar.HomeFragment;
import com.example.asaf_avisar.R;
import com.example.asaf_avisar.StudentUser;
import com.example.asaf_avisar.TeacherUser;

import java.util.ArrayList;

/**
 * Activity for capturing additional teacher details like experience,
 * pricing, teaching area, and specialization.
 */
public class TeacherDetailsActivity extends AppCompatActivity implements FirebaseCallback {

    private static final String TAG = "TeacherDetailsActivity";

    //==========================================================================================
    // DISPLAY LAYER - UI Components and Display Methods
    //==========================================================================================

    // View components
    private TextView hello, priceValueTextView, experienceValueTextView;
    private RadioGroup radioGroupDriveType, radioGroupAvailability;
    private RadioButton radioManual, radioAutomatic, radioAvailableYes;
    private Spinner citySpinner;
    private EditText bioEditText, phoneEditText;
    private SeekBar priceSeekBar, experienceSeekBar;
    private Button submitButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_details);

        // Initialize UI components
        initializeUIComponents();

        // Initialize teacher user to prevent null pointer exception
        teacherUser = new TeacherUser();
        teacherUser.setTeacher(true);

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
        radioGroupDriveType = findViewById(R.id.radioGroupDriveType);
        radioGroupAvailability = findViewById(R.id.radioGroupAvailability);
        radioManual = findViewById(R.id.radioManual);
        radioAutomatic = findViewById(R.id.radioAutomatic);
        radioAvailableYes = findViewById(R.id.radioAvailableYes);
        citySpinner = findViewById(R.id.citySpinner);
        bioEditText = findViewById(R.id.bioEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        priceSeekBar = findViewById(R.id.priceSeekBar);
        experienceSeekBar = findViewById(R.id.experienceSeekBar);
        priceValueTextView = findViewById(R.id.priceValueTextView);
        experienceValueTextView = findViewById(R.id.experienceValueTextView);
        submitButton = findViewById(R.id.submitButton);
    }

    /**
     * Set up UI event listeners
     */
    private void setupEventListeners() {
        // Set up city spinner
        setupCitySpinner();

        // Set up price seek bar
        setupPriceSeekBar();

        // Set up experience seek bar
        setupExperienceSeekBar();

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
     * Set up the price seek bar with listener
     */
    private void setupPriceSeekBar() {
        // Set initial value (150 is default)
        priceSeekBar.setProgress(15); // 150 ÷ 10 = 15
        priceValueTextView.setText("₪150");

        priceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Calculate price (min 100, max 300, step 10)
                int price = (progress + 10) * 10; // +10 ensures minimum of 100
                priceValueTextView.setText("₪" + price);
                selectedPrice = price;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Not needed
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Not needed
            }
        });
    }

    /**
     * Set up the experience seek bar with listener
     */
    private void setupExperienceSeekBar() {
        // Set initial value
        experienceSeekBar.setProgress(3);
        experienceValueTextView.setText("3 years");

        experienceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                experienceValueTextView.setText(progress + " years");
                selectedExperience = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Not needed
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Not needed
            }
        });
    }

    /**
     * Update greeting with user name
     */
    private void updateGreeting(String name) {
        if (name != null) {
            hello.setText("Welcome, " + name);
        }
    }

    /**
     * Show a toast message to the user
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Navigate to menu activity with HomeFragment
     */
    private void navigateToMenuActivity() {
        // Create intent to go to menu activity
        Intent intent = new Intent(TeacherDetailsActivity.this, menu.class);
        intent.putExtra("isTeacher", true);
        startActivity(intent);
        finish();
    }

    //==========================================================================================
    // LOGIC LAYER - Business Logic and Data Management
    //==========================================================================================

    private String selectedCity = "Not selected"; // Default value
    private String selectedSpecialty = "General"; // Default value
    private int selectedPrice = 150; // Default value
    private int selectedExperience = 3; // Default value
    private FireBaseManager fireBaseManager;
    private String userName;
    private TeacherUser teacherUser;

    /**
     * Initialize data components
     */
    private void initializeDataComponents() {
        fireBaseManager = new FireBaseManager(this);
        // Read teacher data from Firebase
        fireBaseManager.readData(this, "Teacher", fireBaseManager.getUserid());
    }

    /**
     * Handle submit button click
     */
    private void handleSubmit() {
        Log.d(TAG, "Submit button clicked");

        // Validate inputs
        if (!validateInputs()) {
            return;
        }

        // Get all user input values
        String bio = bioEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        int driveType = getDriveType();
        boolean available = getAvailabilityStatus();

        try {
            // Update teacher user object with new details
            updateTeacherUser(driveType, bio, phone, available);

            // Save to Firebase
            fireBaseManager.updateUser(teacherUser);

            // Show success message
            showToast("Profile updated successfully");

            // Navigate to menu activity with HomeFragment
            navigateToMenuActivity();

        } catch (Exception e) {
            Log.e(TAG, "Error in handleSubmit: " + e.getMessage());
            e.printStackTrace();
            showToast("Error updating profile: " + e.getMessage());
        }
    }

    /**
     * Validate user inputs
     */
    private boolean validateInputs() {
        // Validate phone number
        String phone = phoneEditText.getText().toString().trim();
        if (phone.isEmpty()) {
            showToast("Please enter your phone number");
            phoneEditText.requestFocus();
            return false;
        }

        // Validate bio (optional but with length limit)
        String bio = bioEditText.getText().toString().trim();
        if (bio.length() > 200) {
            showToast("Bio is too long (maximum 200 characters)");
            bioEditText.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Update the TeacherUser object with user inputs
     */
    private void updateTeacherUser(int driveType, String bio, String phone, boolean available) {
        Log.d(TAG, "Updating TeacherUser object");

        // Make sure teacherUser is not null
        if (teacherUser == null) {
            teacherUser = new TeacherUser();
            teacherUser.setTeacher(true);
            Log.d(TAG, "Created new TeacherUser in updateTeacherUser");
        }

        // Set all the values
        teacherUser.setDriverType(driveType == 1);  // 1 = automatic
        teacherUser.setLocation(selectedCity);
        teacherUser.setBio(bio);
        teacherUser.setPhone(phone);
        teacherUser.setAvailable(available);
        teacherUser.setPrice(selectedPrice);
        teacherUser.setExperience(selectedExperience);
        teacherUser.setTeacher(true);  // Ensure teacher flag is set

        // Specialty is not directly stored in TeacherUser,
        // but you could add it to the bio or as a new field
        if (!selectedSpecialty.equals("General")) {
            String bioWithSpecialty = "Specialty: " + selectedSpecialty + "\n\n" + bio;
            teacherUser.setBio(bioWithSpecialty);
        }

        Log.d(TAG, "TeacherUser updated successfully");
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
     * Get user-selected availability status
     */
    private boolean getAvailabilityStatus() {
        return radioGroupAvailability.getCheckedRadioButtonId() == radioAvailableYes.getId();
    }

    //==========================================================================================
    // CALLBACK IMPLEMENTATIONS - Firebase Data Handling
    //==========================================================================================

    @Override
    public void oncallbackStudent(StudentUser user) {
        // Not used in this activity
    }

    @Override
    public void oncallbackArryStudent(ArrayList<StudentUser> students) {
        // Not used in this activity
    }

    @Override
    public void onCallbackTeacher(ArrayList<TeacherUser> teachers) {
        Log.d(TAG, "Teacher array callback with: " + (teachers != null ? teachers.size() + " teachers" : "null"));

        if (teachers != null && !teachers.isEmpty()) {
            // Assuming the first teacher in the list is the current user
            TeacherUser user = teachers.get(0);
            if (user != null) {
                userName = user.getName();
                teacherUser = user;

                // Update UI with user data
                updateGreeting(userName);
                loadTeacherData(user);
            }
        }
    }

    @Override
    public void onCallbackSingleTeacher(TeacherUser teacher) {
        Log.d(TAG, "Single teacher callback with: " + (teacher != null ? "data" : "null"));

        if (teacher != null) {
            userName = teacher.getName();
            teacherUser = teacher;
            teacherUser.setTeacher(true); // Ensure teacher flag is set

            // Update UI with user data
            updateGreeting(userName);
            loadTeacherData(teacher);
        }
    }

    /**
     * Load teacher data into UI components
     */
    private void loadTeacherData(TeacherUser teacher) {
        try {
            // Load existing teacher data into UI components if available
            if (teacher.getLocation() != null && !teacher.getLocation().isEmpty()) {
                // Set city spinner
                setSpinnerSelection(citySpinner, teacher.getLocation());
            }

            // Set drive type
            radioGroupDriveType.check(teacher.isDriverType() ?
                    R.id.radioAutomatic : R.id.radioManual);

            // Set availability
            radioGroupAvailability.check(teacher.isAvailable() ?
                    R.id.radioAvailableYes : R.id.radioAvailableNo);

            // Set phone
            if (teacher.getPhone() != null && !teacher.getPhone().isEmpty()) {
                phoneEditText.setText(teacher.getPhone());
            }

            // Set bio
            if (teacher.getBio() != null && !teacher.getBio().isEmpty()) {
                bioEditText.setText(teacher.getBio());
            }

            // Set price
            int price = teacher.getPrice();
            if (price > 0) {
                int progress = (price / 10) - 10; // Convert price to progress (reverse of calculation)
                priceSeekBar.setProgress(progress);
                priceValueTextView.setText("₪" + price);
                selectedPrice = price;
            }

            // Set experience
            int experience = teacher.getExperience();
            if (experience > 0) {
                experienceSeekBar.setProgress(experience);
                experienceValueTextView.setText(experience + " years");
                selectedExperience = experience;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading teacher data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Set spinner selection based on value
     */
    private void setSpinnerSelection(Spinner spinner, String value) {
        try {
            for (int i = 0; i < spinner.getCount(); i++) {
                if (spinner.getItemAtPosition(i).toString().equals(value)) {
                    spinner.setSelection(i);
                    break;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting spinner selection: " + e.getMessage());
        }
    }
}
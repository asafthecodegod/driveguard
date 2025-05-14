package com.example.asaf_avisar.activitys;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.asaf_avisar.Check;
import com.example.asaf_avisar.FireBaseManager;
import com.example.asaf_avisar.R;
import com.example.asaf_avisar.StudentUser;

import java.util.Calendar;
import java.util.Date;

/**
 * Activity that handles user registration functionality.
 */
public class RegisterPageActivity extends AppCompatActivity implements View.OnClickListener, DatePicker.OnDateChangedListener {

    //==========================================================================================
    // DISPLAY LAYER - UI Components and Display Methods
    //==========================================================================================

    // UI components
    private EditText etEmail, etPassword, etUsername;
    private DatePicker dpBirthday;
    private Button registerButton;
    private CheckBox isTeacher;
    private TextView registerLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resgister_page);

        // Initialize UI components
        initializeUIComponents();

        // Set up event listeners
        setupEventListeners();

        // Initialize logic components
        initializeLogicComponents();

        // Configure UI restrictions
        configureDatePickerLimits();
    }

    /**
     * Initialize all UI components
     */
    private void initializeUIComponents() {
        registerLink = findViewById(R.id.ToLoginText);
        etEmail = findViewById(R.id.userEmail);
        etUsername = findViewById(R.id.register_username);
        dpBirthday = findViewById(R.id.birthday_picker);
        etPassword = findViewById(R.id.register_password);
        registerButton = findViewById(R.id.RegisterButton);
        isTeacher = findViewById(R.id.userType);
    }

    /**
     * Set up all event listeners
     */
    private void setupEventListeners() {
        registerLink.setOnClickListener(this);
        registerButton.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dpBirthday.setOnDateChangedListener(this);
        }

        isTeacher.setOnCheckedChangeListener((buttonView, isChecked) -> {
            userRole = isChecked ? "Teacher" : "Student";
        });
    }

    /**
     * Configure date picker restrictions
     */
    private void configureDatePickerLimits() {
        // Restrict the DatePicker to not select future dates
        Calendar today = Calendar.getInstance();
        dpBirthday.setMaxDate(today.getTimeInMillis());
    }

    /**
     * Display validation errors for inputs
     */
    private void showValidationErrors(boolean isEmailValid, boolean isNameValid, boolean isPasswordValid) {
        if (!isEmailValid) {
            etEmail.setError("Invalid email.");
        }

        if (!isNameValid) {
            etUsername.setError("Invalid name.");
        }

        if (!isPasswordValid) {
            etPassword.setError("Invalid password.");
        }
    }

    /**
     * Display toast message to user
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Navigate to login screen
     */
    private void navigateToLogin() {
        startActivity(new Intent(this, LoginPageActivity.class));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        if (view == registerLink) {
            navigateToLogin();
        } else if (view == registerButton) {
            handleRegistration();
        }
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        // Not needed for functionality but required for implementation
    }

    //==========================================================================================
    // LOGIC LAYER - Business Logic and Data Management
    //==========================================================================================

    private Check check;
    private FireBaseManager fireBaseManager;
    private Date birthDate;
    private String userRole = "Student"; // Default role

    /**
     * Initialize logic components
     */
    private void initializeLogicComponents() {
        check = new Check();
        fireBaseManager = new FireBaseManager(this);
    }

    /**
     * Handle the registration process
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void handleRegistration() {
        // Get user inputs
        String email = etEmail.getText().toString();
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        // Validate inputs
        boolean isEmailValid = check.checkEmail(email);
        boolean isNameValid = check.checkName(username);
        boolean isPasswordValid = check.checkPass(password);

        // Show validation errors if any
        showValidationErrors(isEmailValid, isNameValid, isPasswordValid);

        // If all inputs are valid, check age
        if (isEmailValid && isNameValid && isPasswordValid) {
            validateAgeAndRegister();
        }
    }

    /**
     * Validate user age and proceed with registration if valid
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void validateAgeAndRegister() {
        int age = calculateAge(dpBirthday.getYear(), dpBirthday.getMonth(), dpBirthday.getDayOfMonth());

        if (age < 16) {
            showToast("You must be at least 16 years old");
        } else {
            // Create user data and register
            createUserAndRegister();
            showToast("Age verified. Proceeding...");
        }
    }

    /**
     * Create user object and register with Firebase
     */
    private void createUserAndRegister() {
        String email = etEmail.getText().toString();
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        StudentUser newUser = new StudentUser(username, email, password, birthDate);
        fireBaseManager.createUser(newUser, userRole);
    }

    /**
     * Calculate user age based on birth date
     */
    public int calculateAge(int birthYear, int birthMonth, int birthDay) {
        // Get the current date using Calendar
        Calendar currentDate = Calendar.getInstance();
        int currentYear = currentDate.get(Calendar.YEAR);
        int currentMonth = currentDate.get(Calendar.MONTH); // 0-based
        int currentDay = currentDate.get(Calendar.DAY_OF_MONTH);

        // Store the birth date for user creation
        birthDate = new Date(birthYear, birthMonth, birthDay);

        // Calculate the age
        int age = currentYear - birthYear;

        // Adjust age if birthday hasn't occurred this year yet
        if (currentMonth < birthMonth || (currentMonth == birthMonth && currentDay < birthDay)) {
            age--;
        }

        return age;
    }
}
package com.example.asaf_avisar.activitys;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.asaf_avisar.objects.Check;
import com.example.asaf_avisar.FireBaseManager;
import com.example.asaf_avisar.callbacks.FirebaseCallback;
import com.example.asaf_avisar.R;
import com.example.asaf_avisar.objects.StudentUser;
import com.example.asaf_avisar.objects.TeacherUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Activity that handles user registration functionality.
 */
public class RegisterPageActivity extends AppCompatActivity implements View.OnClickListener, DatePicker.OnDateChangedListener, FirebaseCallback {

    //==========================================================================================
    // DISPLAY LAYER - UI Components and Display Methods
    //==========================================================================================

    private EditText etEmail, etPassword, etUsername;
    private DatePicker dpBirthday;
    private Button registerButton;
    private CheckBox isTeacherCheckBox;
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

    private void initializeUIComponents() {
        registerLink = findViewById(R.id.ToLoginText);
        etEmail = findViewById(R.id.userEmail);
        etUsername = findViewById(R.id.register_username);
        dpBirthday = findViewById(R.id.birthday_picker);
        etPassword = findViewById(R.id.register_password);
        registerButton = findViewById(R.id.RegisterButton);
        isTeacherCheckBox = findViewById(R.id.userType);
    }

    private void setupEventListeners() {
        registerLink.setOnClickListener(this);
        registerButton.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dpBirthday.setOnDateChangedListener(this);
        }
    }

    private void configureDatePickerLimits() {
        Calendar today = Calendar.getInstance();
        dpBirthday.setMaxDate(today.getTimeInMillis());
    }

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

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

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
        // Not used, but required
    }

    //==========================================================================================
    // LOGIC LAYER - Business Logic and Data Management
    //==========================================================================================

    private Check check;
    private FireBaseManager fireBaseManager;
    private Date birthDate;
    private boolean registrationSuccessful = false;
    private boolean registeredAsTeacher = false;

    private void initializeLogicComponents() {
        check = new Check();
        fireBaseManager = new FireBaseManager(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void handleRegistration() {
        String email = etEmail.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        boolean isEmailValid = check.checkEmail(email);
        boolean isNameValid = check.checkName(username);
        boolean isPasswordValid = check.checkPass(password);

        showValidationErrors(isEmailValid, isNameValid, isPasswordValid);

        if (isEmailValid && isNameValid && isPasswordValid) {
            validateAgeAndRegister();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void validateAgeAndRegister() {
        int age = calculateAge(dpBirthday.getYear(), dpBirthday.getMonth(), dpBirthday.getDayOfMonth());

        if (age < 16) {
            showToast("You must be at least 16 years old");
        } else {
            showToast("Age verified. Creating your account...");
            createUserAndRegister();
        }
    }

    private void createUserAndRegister() {
        String email = etEmail.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Save the checkbox state for later use
        registeredAsTeacher = isTeacherCheckBox.isChecked();

        if (registeredAsTeacher) {
            TeacherUser newTeacher = new TeacherUser(username, email, password, birthDate, 0);
            // Set required fields
            newTeacher.setTeacher(true);
            newTeacher.setName(username);
            newTeacher.setEmail(email);
            newTeacher.setPassword(password);
            newTeacher.setBirthday(birthDate);
            newTeacher.setFollowerCount(0);
            newTeacher.setFollowingCount(0);
            newTeacher.setDrivingInvestment(0);
            newTeacher.setProfilePhotoBase64("");
            fireBaseManager.createUser(newTeacher, "Teacher");
        } else {
            StudentUser newStudent = new StudentUser(username, email, password, birthDate);
            // Set required fields
            newStudent.setTeacher(false);
            newStudent.setName(username);
            newStudent.setEmail(email);
            newStudent.setPassword(password);
            newStudent.setBirthday(birthDate);
            newStudent.setFollowerCount(0);
            newStudent.setFollowingCount(0);
            newStudent.setDrivingInvestment(0);
            newStudent.setProfilePhotoBase64("");
            fireBaseManager.createUser(newStudent, "Student");
        }

        // Registration is complete, now check for successful authentication
        registrationSuccessful = true;
    }

    public int calculateAge(int birthYear, int birthMonth, int birthDay) {
        Calendar currentDate = Calendar.getInstance();
        int currentYear = currentDate.get(Calendar.YEAR);
        int currentMonth = currentDate.get(Calendar.MONTH); // 0-based
        int currentDay = currentDate.get(Calendar.DAY_OF_MONTH);

        // Save birth date
        birthDate = new Date(birthYear - 1900, birthMonth, birthDay); // Adjust year for java.util.Date

        int age = currentYear - birthYear;
        if (currentMonth < birthMonth || (currentMonth == birthMonth && currentDay < birthDay)) {
            age--;
        }

        return age;
    }

    //==========================================================================================
    // FIREBASE CALLBACKS - Handle Firebase responses
    //==========================================================================================

    // Assuming these methods are called after user creation is successful
    // We need to implement the FirebaseCallback methods

    @Override
    public void oncallbackStudent(StudentUser student) {
        if (registrationSuccessful) {
            // User registration was successful as a student
            navigateToMenuActivity(false);
        }
    }

    @Override
    public void oncallbackArryStudent(ArrayList<StudentUser> students) {
        // Not used for registration
    }

    @Override
    public void onCallbackTeacher(ArrayList<TeacherUser> teachers) {
        // Not used for registration
    }

    @Override
    public void onCallbackSingleTeacher(TeacherUser teacher) {
        if (registrationSuccessful) {
            // User registration was successful as a teacher
            navigateToMenuActivity(true);
        }
    }

    /**
     * Navigate to the menu activity with user type information
     */
    private void navigateToMenuActivity(boolean isTeacher) {
        Intent menuIntent = new Intent(this, menu.class);
        menuIntent.putExtra("isTeacher", isTeacher);
        startActivity(menuIntent);
        finish(); // Close the registration activity
    }
}
package com.example.asaf_avisar.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.asaf_avisar.objects.Check;
import com.example.asaf_avisar.FireBaseManager;
import com.example.asaf_avisar.R;

/**
 * Activity that handles user login functionality.
 */
public class LoginPageActivity extends AppCompatActivity implements View.OnClickListener {

    //==========================================================================================
    // DISPLAY LAYER - UI Components and Display Methods
    //==========================================================================================

    // UI components
    private EditText etEmail, etPassword;
    private Button loginButton;
    private TextView registerLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);

        // Initialize UI components
        initializeUIComponents();

        // Set up event listeners
        setupEventListeners();

        // Initialize logic components
        initializeLogicComponents();
    }

    /**
     * Initialize all UI components
     */
    private void initializeUIComponents() {
        etEmail = findViewById(R.id.loginemail);
        etPassword = findViewById(R.id.loginpassword);
        loginButton = findViewById(R.id.loginButton);
        registerLink = findViewById(R.id.signuplink);
    }

    /**
     * Set up all event listeners
     */
    private void setupEventListeners() {
        loginButton.setOnClickListener(this);
        registerLink.setOnClickListener(this);
    }

    /**
     * Display email validation error
     */
    private void showEmailError() {
        etEmail.setError("Invalid email.");
    }

    /**
     * Display password validation error
     */
    private void showPasswordError() {
        etPassword.setError("Invalid password.");
    }

    /**
     * Clear any previous error messages
     */
    private void clearErrorMessages() {
        etEmail.setError(null);
        etPassword.setError(null);
    }

    @Override
    public void onClick(View view) {
        if (view == loginButton) {
            handleLoginButtonClick();
        }
        if (view == registerLink) {
            navigateToRegistration();
        }
    }

    //==========================================================================================
    // LOGIC LAYER - Business Logic and Data Management
    //==========================================================================================

    private FireBaseManager fireBaseManager;
    private Check check;

    /**
     * Initialize logic components
     */
    private void initializeLogicComponents() {
        fireBaseManager = new FireBaseManager(this);
        check = new Check();
    }

    /**
     * Handle login button click - validate inputs and attempt login
     */
    private void handleLoginButtonClick() {
        // Clear any previous errors
        clearErrorMessages();

        // Get user inputs
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        // Validate inputs
        if (!validateCredentials(email, password)) {
            return; // Validation failed, errors already shown
        }

        // Attempt login
        attemptLogin(email, password);
    }

    /**
     * Validate user credentials
     * @return true if validation passes, false otherwise
     */
    private boolean validateCredentials(String email, String password) {
        if (!check.checkEmail(email)) {
            showEmailError();
            return false;
        }

        if (!check.checkPass(password)) {
            showPasswordError();
            return false;
        }

        return true;
    }

    /**
     * Attempt to log in with provided credentials
     */
    private void attemptLogin(String email, String password) {
        fireBaseManager.loginUser(email, password);
        // Note: Firebase will handle success/failure and navigation
    }

    /**
     * Navigate to the registration screen
     */
    private void navigateToRegistration() {
        startActivity(new Intent(this, RegisterPageActivity.class));
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
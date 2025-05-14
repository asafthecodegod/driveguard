package com.example.asaf_avisar.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.asaf_avisar.R;

/**
 * Initial screen allowing users to either log in or create a new account.
 */
public class LoginOrRegistretionActivity extends AppCompatActivity {

    //==========================================================================================
    // DISPLAY LAYER - UI Components and Display Methods
    //==========================================================================================

    /**
     * Button for navigating to login screen
     */
    Button loginButton;

    /**
     * Button for navigating to account creation screen
     */
    Button createAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_or_registretion);

        // Initialize UI components
        initializeUIComponents();

        // Set up event listeners
        setupEventListeners();
    }

    /**
     * Initialize all UI components
     */
    private void initializeUIComponents() {
        loginButton = findViewById(R.id.loginButton);
        createAccountButton = findViewById(R.id.createAccountButton);
    }

    /**
     * Set up all event listeners
     */
    private void setupEventListeners() {
        // Set up login button click listener
        loginButton.setOnClickListener(v -> navigateToLogin());

        // Set up create account button click listener
        createAccountButton.setOnClickListener(v -> navigateToRegistration());
    }

    //==========================================================================================
    // LOGIC LAYER - Navigation Logic
    //==========================================================================================

    /**
     * Navigate to the login screen
     */
    private void navigateToLogin() {
        Intent intent = new Intent(LoginOrRegistretionActivity.this, LoginPageActivity.class);
        startActivity(intent);
    }

    /**
     * Navigate to the registration screen
     */
    private void navigateToRegistration() {
        Intent intent = new Intent(LoginOrRegistretionActivity.this, RegisterPageActivity.class);
        startActivity(intent);
    }
}
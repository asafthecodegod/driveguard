package com.example.asaf_avisar.activitys;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.asaf_avisar.FireBaseManager;
import com.example.asaf_avisar.R;

/**
 * Initial startup activity that checks authentication state
 * and either navigates to the main menu or displays the start button.
 */
public class StartActivity extends AppCompatActivity {

    //==========================================================================================
    // DISPLAY LAYER - UI Components and Display Methods
    //==========================================================================================

    // UI components
    private Button button;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start);

        // Initialize UI components
        initializeUIComponents();

        // Initialize logic components
        initializeLogicComponents();

        // Check authentication state
        checkAuthenticationAndNavigate();

        // Set up event listeners
        setupEventListeners();
    }

    /**
     * Initialize all UI components
     */
    private void initializeUIComponents() {
        button = findViewById(R.id.startButton);
    }

    /**
     * Set up all event listeners
     */
    private void setupEventListeners() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToLoginRegistration();
            }
        });
    }

    //==========================================================================================
    // LOGIC LAYER - Authentication and Navigation Logic
    //==========================================================================================

    private FireBaseManager fireBaseManager;

    /**
     * Initialize logic components
     */
    private void initializeLogicComponents() {
        fireBaseManager = new FireBaseManager(this);
    }

    /**
     * Check if user is already authenticated and navigate accordingly
     */
    private void checkAuthenticationAndNavigate() {
        if (fireBaseManager.isConnected()) {
            navigateToMenu();
        }
    }

    /**
     * Navigate to the main menu activity
     */
    private void navigateToMenu() {
        startActivity(new Intent(this, menu.class));
        finish(); // Close this activity to prevent going back to it
    }

    /**
     * Navigate to the login/registration activity
     */
    private void navigateToLoginRegistration() {
        Intent intent = new Intent(StartActivity.this, LoginOrRegistretionActivity.class);
        startActivity(intent);
    }
}
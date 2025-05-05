package com.example.asaf_avisar.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.asaf_avisar.R;

/**
 * The type Login or registretion activity.
 */
public class LoginOrRegistretionActivity extends AppCompatActivity {
    /**
     * The Login button.
     */
    Button loginButton;
    /**
     * The Create account button.
     */
    Button createAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_or_registretion); // Corrected the layout name

        loginButton = findViewById(R.id.loginButton);
        createAccountButton = findViewById(R.id.createAccountButton); // Use separate variable

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginOrRegistretionActivity.this, LoginPageActivity.class);
                startActivity(intent);
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() { // Added OnClickListener for createAccountButton
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginOrRegistretionActivity.this, RegisterPageActivity.class); // Assuming you have a RegistrationPageActivity
                startActivity(intent);
            }
        });
    }
}

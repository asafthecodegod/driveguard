package com.example.asaf_avisar.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.asaf_avisar.Check;
import com.example.asaf_avisar.FireBaseManager;
import com.example.asaf_avisar.R;

public class LoginPageActivity extends AppCompatActivity implements View.OnClickListener {
    private FireBaseManager fireBaseManager;
    private EditText etEmail, etPassword;
    private Button loginButton;
    private Check check;
    private TextView registerLink;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_page);
        etEmail = findViewById(R.id.loginemail);
        etPassword = findViewById(R.id.loginpassword);
        fireBaseManager = new FireBaseManager(this);
        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(this);
        check = new Check();
        registerLink =findViewById(R.id.registerLink);
        registerLink.setOnClickListener(this);
    }


    public void onClick(View view) {
        if (view == loginButton) {
            if ((!check.checkEmail(etEmail.getText().toString())))
                etEmail.setError("Invild emil.");
            else if ((!check.checkPass(etPassword.getText().toString())))
                etPassword.setError("Invild password.");
            else
            fireBaseManager.loginUser(etEmail.getText().toString(), etPassword.getText().toString());
        }
        if( view == registerLink)
        {
            startActivity(new Intent(this, RegisterPageActivity.class));

        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
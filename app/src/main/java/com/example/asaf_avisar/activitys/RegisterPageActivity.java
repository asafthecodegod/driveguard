package com.example.asaf_avisar.activitys;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.asaf_avisar.Check;
import com.example.asaf_avisar.FireBaseManager;
import com.example.asaf_avisar.R;
import com.example.asaf_avisar.StudentUser;

import java.util.Calendar;
import java.util.Date;

public class RegisterPageActivity extends AppCompatActivity implements View.OnClickListener, DatePicker.OnDateChangedListener {
    private EditText etEmail,etPassword,etUsername;
    private DatePicker dpBirthday;
    private Button registerButton;
    private Check check;
    private FireBaseManager fireBaseManager;
    private Date YearOld;
    private CheckBox isTeacher;
    private String userRole = "Student";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resgister_page);
        etEmail = findViewById(R.id.email);
        etUsername = findViewById(R.id.loginemail);
        dpBirthday = findViewById(R.id.birthday);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            dpBirthday.setOnDateChangedListener(this);
        }
        etPassword = findViewById(R.id.loginpassword);
        registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(this);
        check = new Check();
        fireBaseManager = new FireBaseManager(this);
        isTeacher = findViewById(R.id.userType);

        isTeacher.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                userRole = "Teacher";  // Set userRole to Teacher when checked
            } else {
                userRole = "Student";  // Default role if unchecked
            }
        });
    }

    public  int calculateAge(int birthYear, int birthMonth, int birthDay) {
        // Get the current date using Calendar
        Calendar currentDate = Calendar.getInstance();
        int currentYear = currentDate.get(Calendar.YEAR);
        int currentMonth = currentDate.get(Calendar.MONTH); // 0-based
        int currentDay = currentDate.get(Calendar.DAY_OF_MONTH);

        // Calculate the age
        int age = currentYear - birthYear;
        YearOld = new Date(currentYear,currentMonth,currentDay);

        // Adjust the age if the birthday hasn't occurred yet this year
        if (currentMonth < birthMonth || (currentMonth == birthMonth && currentDay < birthDay)) {
            age--; // Subtract 1 if the birthday hasn't occurred yet
        }

        return age;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view)
    {
        if(view == registerButton){
            if ((!check.checkEmail(etEmail.getText().toString())))
                etEmail.setError("Invild email.");
            if ((!check.checkName(etUsername.getText().toString())))
                etUsername.setError("Invild name.");
            if ((!check.checkPass(etPassword.getText().toString())))
                etPassword.setError("Invild password.");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                int age = calculateAge(dpBirthday.getYear(),dpBirthday.getMonth(),dpBirthday.getDayOfMonth());
                //Toast.makeText(this, "Your calculated age: " + dpBirthday, Toast.LENGTH_SHORT).show();

                if ((age)< 16)
                {
                    Toast.makeText(this, "You must be at least 16 years old", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(this, "Age verified. Proceeding...", Toast.LENGTH_SHORT).show();
                    fireBaseManager.createUser(new StudentUser(etUsername.getText().toString(),etEmail.getText().toString(),etPassword.getText().toString(),YearOld),userRole);
                }
            }

            }


        }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

    }
}


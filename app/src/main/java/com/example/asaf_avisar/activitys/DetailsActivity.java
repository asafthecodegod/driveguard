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
import com.example.asaf_avisar.FirebaseCallback;
import com.example.asaf_avisar.MyReceiver;
import com.example.asaf_avisar.R;
import com.example.asaf_avisar.StudentUser;
import com.example.asaf_avisar.TeacherUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DetailsActivity extends AppCompatActivity implements FirebaseCallback {

    // Views
    private TextView licenseDateLabel, hello;
    private RadioGroup radioGroupDriveType, radioGroupTheory, radioGroupGreenFile, radioGroupLicense;
    private RadioButton radioManual, radioAutomatic, radioYesTheory,radioYesGreenFile;
    private Spinner citySpinner;
    private DatePicker licenseDatePicker;
    private Button submitButton;

    private String selectedCity = "Not selected"; // Default value 1
    private FireBaseManager fireBaseManager;
    private String userName;
    private StudentUser studentUser;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        hello = findViewById(R.id.userName);
        if (userName != null) {
            hello.setText("Hi, " + userName);
        }

        fireBaseManager = new FireBaseManager(this);
        fireBaseManager.readData(this, "Student", fireBaseManager.getUserid());

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

        // Restrict future dates in DatePicker
        licenseDatePicker.setMaxDate(System.currentTimeMillis());

        // Initially hide the license date picker and label
        licenseDateLabel.setVisibility(View.GONE);
        licenseDatePicker.setVisibility(View.GONE);

        // Set up the city spinner
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

        // Show or hide license date picker based on selection
        radioGroupLicense.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioYesLicense) {
                licenseDateLabel.setVisibility(View.VISIBLE);
                licenseDatePicker.setVisibility(View.VISIBLE);
            } else {
                licenseDateLabel.setVisibility(View.GONE);
                licenseDatePicker.setVisibility(View.GONE);
            }
        });

        // Handle submit button click
        submitButton.setOnClickListener(v -> {
            int driveType = getDriveType();
            boolean theory = getTheoryStatus();
            boolean greenFile = getGreenFileStatus();
            boolean license = getLicenseStatus();
            Date licenseDate = getLicenseDate();

            // Check if the license is provided and the licenseDate is not null
            if (license && licenseDate == null) {
                Toast.makeText(DetailsActivity.this, "Please provide your license date", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update StudentUser object
            studentUser.setHasGreenForm(greenFile);
            studentUser.setHasLicense(license);
            studentUser.setLicenseDate(licenseDate);
            studentUser.setPassedTheory(theory);
            if(driveType == 1)
            studentUser.setDriverType(true);
            else
                studentUser.setDriverType(false);
            studentUser.setCity(selectedCity);

            // Update Firebase
            fireBaseManager.updateUser(studentUser);

            // Navigate to menu activity
            Intent intent = new Intent(DetailsActivity.this, menu.class);
            if (license) {
                intent.putExtra("LICENSE_DATE", licenseDate);
            }
            startActivity(intent);
        });
    }

    private int getDriveType() {
        int selectedId = radioGroupDriveType.getCheckedRadioButtonId();
        if (selectedId == radioManual.getId()) return 0;
        if (selectedId == radioAutomatic.getId()) return 1;
        return -1;
    }

    private boolean getTheoryStatus() {
        return radioGroupTheory.getCheckedRadioButtonId() == radioYesTheory.getId();
    }

    private boolean getGreenFileStatus() {
        return radioGroupGreenFile.getCheckedRadioButtonId() == radioYesGreenFile.getId();
    }

    private boolean getLicenseStatus() {
        return radioGroupLicense.getCheckedRadioButtonId() == R.id.radioYesLicense;
    }

    private Date getLicenseDate() {
        if (licenseDatePicker.getVisibility() == View.VISIBLE) {
            int day = licenseDatePicker.getDayOfMonth();
            int month = licenseDatePicker.getMonth();
            int year = licenseDatePicker.getYear();

            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, day);

            // Prevent future dates
            Calendar today = Calendar.getInstance();
            if (selectedDate.after(today)) {
                Toast.makeText(this, "Future dates are not allowed!", Toast.LENGTH_SHORT).show();
                return null;
            }

            // Schedule a notification 3 months later
            scheduleNotification(selectedDate);

            return selectedDate.getTime();
        }
        return null;
    }

    private void scheduleNotification(Calendar licenseDate) {
        Calendar notificationDate = (Calendar) licenseDate.clone();
        notificationDate.add(Calendar.MONTH, 3);
        //notificationDate.add(Calendar.SECOND, 3);


        Intent intent = new Intent(this, MyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, notificationDate.getTimeInMillis(), pendingIntent);
            // תאריך נוכחי פלוס כמה שניות
        }
    }

    @Override
    public void oncallbackArryStudent(ArrayList<StudentUser> students) {}

    @Override
    public void oncallbackStudent(StudentUser user) {
        userName = user.getName();
        hello.setText("Hi, " + userName);
        Toast.makeText(this, "Hi " + user.getName(), Toast.LENGTH_SHORT).show();
        studentUser = user;
    }

    @Override
    public void onCallbackTeacher(ArrayList<TeacherUser> teachers) {}

}

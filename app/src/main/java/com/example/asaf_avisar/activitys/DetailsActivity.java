package com.example.asaf_avisar.activitys;

import android.annotation.SuppressLint;
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
import com.example.asaf_avisar.MainActivity;
import com.example.asaf_avisar.R;
import com.example.asaf_avisar.StudentUser;
import com.example.asaf_avisar.TeacherUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DetailsActivity extends AppCompatActivity implements FirebaseCallback {

    // Views
    private TextView licenseDateLabel;
    private RadioGroup radioGroupDriveType, radioGroupTheory, radioGroupGreenFile, radioGroupLicense;
    private RadioButton radioManual, radioAutomatic, radioYesTheory, radioNoTheory, radioYesGreenFile, radioNoGreenFile, radioYesLicense, radioNoLicense;
    private Spinner citySpinner;
    private DatePicker licenseDatePicker;
    private Button submitButton;
    private String selectedCity = "Not selected"; // Default value
    FireBaseManager fireBaseManager;
    TextView hello;
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
        fireBaseManager.readData(this,"Student",fireBaseManager.getUserid());

        licenseDateLabel = findViewById(R.id.licenseDateLabel);
        citySpinner = findViewById(R.id.citySpinner);
        radioGroupDriveType = findViewById(R.id.radioGroupDriveType);
        radioGroupTheory = findViewById(R.id.radioGroupTheory);
        radioGroupGreenFile = findViewById(R.id.radioGroupGreenFile);
        radioGroupLicense = findViewById(R.id.radioGroupLicense);
        radioManual = findViewById(R.id.radioManual);
        radioAutomatic = findViewById(R.id.radioAutomatic);
        radioYesTheory = findViewById(R.id.radioYesTheory);
        radioNoTheory = findViewById(R.id.radioNoTheory);
        radioYesGreenFile = findViewById(R.id.radioYesGreenFile);
        radioNoGreenFile = findViewById(R.id.radioNoGreenFile);
        radioYesLicense = findViewById(R.id.radioYesLicense);
        radioNoLicense = findViewById(R.id.radioNoLicense);
        licenseDatePicker = findViewById(R.id.licenseDatePicker);
        submitButton = findViewById(R.id.submitButton);

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
        radioGroupLicense.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioYesLicense) {
                    licenseDateLabel.setVisibility(View.VISIBLE);
                    licenseDatePicker.setVisibility(View.VISIBLE);
                } else {
                    licenseDateLabel.setVisibility(View.GONE);
                    licenseDatePicker.setVisibility(View.GONE);
                }
            }
        });

        // Handle submit button click
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the inputs from the user
                int driveType = getDriveType();
                boolean theory = getTheoryStatus();
                boolean greenFile = getGreenFileStatus();
                boolean license = getLicenseStatus();
                Date licenseDate = getLicenseDate();


                // Check if the license is provided and the licenseDate is not null
                if (license && licenseDate == null) {
                    // Show a toast if license date is not provided
                    Toast.makeText(DetailsActivity.this, "Please provide your license date", Toast.LENGTH_SHORT).show();
                    return; // Exit method early
                }

                // Create a new StudentUser object with the provided data
              //  StudentUser studentUser = new StudentUser(licenseDate, theory, driveType, greenFile, license);
                studentUser.setGreenform(greenFile);
                studentUser.setLicense(license);
                studentUser.setLicenseDate(licenseDate);
                studentUser.setTheory(theory);
                studentUser.setType(driveType);
                studentUser.setCity(selectedCity);

                // Pass the StudentUser object to Firebase
                fireBaseManager.UpdateUser(studentUser);


                // Pass the licenseDate to the next activity as an extra (if needed)
                if (getLicenseStatus() == false) {
                    Intent intent = new Intent(DetailsActivity.this, menu.class);
                    startActivity(intent);  // Make sure to start the activity
                } else {
                    Intent intent = new Intent(DetailsActivity.this, menu.class);
                    intent.putExtra("LICENSE_DATE", licenseDate); // Passing the Date object directly
                    startActivity(intent);
                }
            }
        });
    }

    // Method to get the selected drive type (manual or automatic)
    private int getDriveType() {
        int selectedId = radioGroupDriveType.getCheckedRadioButtonId();
        if (selectedId == radioManual.getId()) {
            return 0; // 0 for manual
        } else if (selectedId == radioAutomatic.getId()) {
            return 1; // 1 for automatic
        }
        return -1; // Default value when nothing is selected
    }

    // Method to get the theory status (Yes or No)
    private boolean getTheoryStatus() {
        int selectedId = radioGroupTheory.getCheckedRadioButtonId();
        return selectedId == radioYesTheory.getId(); // Returns true if 'Yes', false if 'No'
    }

    // Method to get the green file status (Yes or No)
    private boolean getGreenFileStatus() {
        int selectedId = radioGroupGreenFile.getCheckedRadioButtonId();
        return selectedId == radioYesGreenFile.getId(); // Returns true if 'Yes', false if 'No'
    }

    // Method to get the license status (Yes or No)
    private boolean getLicenseStatus() {
        int selectedId = radioGroupLicense.getCheckedRadioButtonId();  // Get the selected radio button ID
        if (selectedId == R.id.radioYesLicense) {
            return true; // If "Yes" is selected, return true
        } else if (selectedId == R.id.radioNoLicense) {
            return false; // If "No" is selected, return false
        }
        return false; // If no radio button is selected, return false (or handle as needed)
    }


    // Method to get the license date (only if the user has a license)
    private Date getLicenseDate() {
        if (licenseDatePicker.getVisibility() == View.VISIBLE) {
            int day = licenseDatePicker.getDayOfMonth();
            int month = licenseDatePicker.getMonth(); // Month is 0-based
            int year = licenseDatePicker.getYear();

            // Use Calendar to create a proper Date object
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            return calendar.getTime(); // Get the Date object from Calendar
        }
        return null; // If no date is selected, return null
    }

    @Override
    public void oncallbackArryStudent(ArrayList<StudentUser> students) {

    }

    @Override
    public void oncallbackStudent(StudentUser user) {
        userName = user.getName();
        hello.setText("Hi, " + userName); // Update the TextView with the user's name

        // Optionally show a toast
        Toast.makeText(this, "Hi " + user.getName(), Toast.LENGTH_SHORT).show();
        studentUser = user;
    }

    @Override
    public void onCallbackTeacher(ArrayList<TeacherUser> teachers) {

    }

}

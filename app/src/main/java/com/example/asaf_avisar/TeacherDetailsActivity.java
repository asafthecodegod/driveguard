package com.example.asaf_avisar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.asaf_avisar.activitys.menu;
import com.example.asaf_avisar.callbacks.FirebaseCallback;
import com.example.asaf_avisar.objects.Check;
import com.example.asaf_avisar.objects.TeacherUser;
import com.example.asaf_avisar.objects.StudentUser;

import java.util.ArrayList;

public class TeacherDetailsActivity extends AppCompatActivity implements FirebaseCallback {
    private static final String TAG = "TeacherDetailsActivity";

    // UI
    private TextView hello;
    private EditText bioEditText, phoneEditText;
    private TextView priceValueTextView, experienceValueTextView;
    private RadioGroup radioGroupDriveType, radioGroupAvailability;
    private RadioButton radioManual, radioAutomatic, radioAvailableYes;
    private SeekBar priceSeekBar, experienceSeekBar;
    private Spinner citySpinner;
    private Button submitButton;

    // Helpers
    private Check validator;
    private FireBaseManager fireBaseManager;

    // State
    private TeacherUser teacherUser;
    private String selectedCity = "Not selected";
    private int selectedPrice = 150;
    private int selectedExperience = 3;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_details);

        initializeViews();
        initializeHelpers();
        loadTeacherData();
        setupUIComponents();
    }

    private void initializeViews() {
        hello = findViewById(R.id.userName);
        bioEditText = findViewById(R.id.bioEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        priceValueTextView = findViewById(R.id.priceValueTextView);
        experienceValueTextView = findViewById(R.id.experienceValueTextView);
        radioGroupDriveType = findViewById(R.id.radioGroupDriveType);
        radioManual = findViewById(R.id.radioManual);
        radioAutomatic = findViewById(R.id.radioAutomatic);
        radioGroupAvailability = findViewById(R.id.radioGroupAvailability);
        radioAvailableYes = findViewById(R.id.radioAvailableYes);
        priceSeekBar = findViewById(R.id.priceSeekBar);
        experienceSeekBar = findViewById(R.id.experienceSeekBar);
        citySpinner = findViewById(R.id.citySpinner);
        submitButton = findViewById(R.id.submitButton);
    }

    private void initializeHelpers() {
        validator = new Check();
        fireBaseManager = new FireBaseManager(this);
    }

    private void loadTeacherData() {
        // First try to load from Teacher collection
        fireBaseManager.readTeacherData(this, fireBaseManager.getUserid());
    }

    private void setupUIComponents() {
        setupCitySpinner();
        setupPriceSeekBar();
        setupExperienceSeekBar();
        setupSubmitButton();
    }

    private void setupSubmitButton() {
        submitButton.setOnClickListener(v -> {
            if (validateInputs()) {
                handleSubmit();
            }
        });
    }

    private void setupCitySpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.israel_cities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(adapter);
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> p, android.view.View v, int pos, long id) {
                selectedCity = p.getItemAtPosition(pos).toString();
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });
    }

    private void setupPriceSeekBar() {
        priceSeekBar.setProgress((selectedPrice/10)-10);
        priceValueTextView.setText("₪" + selectedPrice);
        priceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar s, int prog, boolean u) {
                selectedPrice = (prog + 10) * 10;
                priceValueTextView.setText("₪" + selectedPrice);
            }
            @Override public void onStartTrackingTouch(SeekBar s) {}
            @Override public void onStopTrackingTouch(SeekBar s) {}
        });
    }

    private void setupExperienceSeekBar() {
        experienceSeekBar.setProgress(selectedExperience);
        experienceValueTextView.setText(selectedExperience + " years");
        experienceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar s, int prog, boolean u) {
                selectedExperience = prog;
                experienceValueTextView.setText(prog + " years");
            }
            @Override public void onStartTrackingTouch(SeekBar s) {}
            @Override public void onStopTrackingTouch(SeekBar s) {}
        });
    }

    private boolean validateInputs() {
        String phone = phoneEditText.getText().toString().trim();
        if (phone.isEmpty()) {
            phoneEditText.setError("Please enter your phone number");
            return false;
        }
        if (!validator.checkPhone(phone)) {
            phoneEditText.setError("Enter a valid 9-digit Israeli number");
            return false;
        }

        String bio = bioEditText.getText().toString().trim();
        if (bio.length() > 200) {
            bioEditText.setError("Bio too long (max 200 chars)");
            return false;
        }

        return true;
    }

    private void handleSubmit() {
        int driveType = (radioGroupDriveType.getCheckedRadioButtonId() == radioAutomatic.getId()) ? 1 : 0;
        boolean available = (radioGroupAvailability.getCheckedRadioButtonId() == radioAvailableYes.getId());

        updateTeacherUser(
                driveType,
                bioEditText.getText().toString().trim(),
                phoneEditText.getText().toString().trim(),
                available
        );

        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, menu.class).putExtra("isTeacher", true));
        finish();
    }

    private void updateTeacherUser(int driveType, String bio, String phone, boolean available) {
        if (teacherUser == null) {
            teacherUser = new TeacherUser();
            // Copy data from StudentUser if available
            fireBaseManager.readData(this, "Student", fireBaseManager.getUserid());
            return;
        }

        // Update teacher-specific fields
        teacherUser.setDriverType(driveType == 1);
        teacherUser.setLocation(selectedCity);
        teacherUser.setBio(bio);
        teacherUser.setPhone(phone);
        teacherUser.setAvailable(available);
        teacherUser.setPrice(selectedPrice);
        teacherUser.setExperience(selectedExperience);
        teacherUser.setTeacher(true);

        // Ensure we have the user ID
        teacherUser.setId(fireBaseManager.getUserid());

        // Save to Teacher collection
        fireBaseManager.updateTeacher(teacherUser);
        Log.d(TAG, "TeacherUser updated successfully");
    }

    @Override
    public void onCallbackSingleTeacher(TeacherUser teacher) {
        if (teacher != null) {
            teacherUser = teacher;
            populateUIWithTeacherData(teacher);
        } else {
            // If not found in Teacher collection, try Student collection
            fireBaseManager.readData(this, "Student", fireBaseManager.getUserid());
        }
    }

    @Override
    public void oncallbackStudent(StudentUser student) {
        if (student != null) {
            // Create new TeacherUser from StudentUser data
            teacherUser = new TeacherUser();
            teacherUser.setId(student.getId());
            teacherUser.setName(student.getName());
            teacherUser.setEmail(student.getEmail());
            teacherUser.setProfilePhotoBase64(student.getProfilePhotoBase64());
            teacherUser.setBirthday(student.getBirthday());
            teacherUser.setTeacher(true);
            
            // Set default teacher values
            teacherUser.setDriverType(student.isDriverType());
            teacherUser.setLocation(student.getCity());
            teacherUser.setPrice(selectedPrice);
            teacherUser.setExperience(selectedExperience);
            teacherUser.setAvailable(true);
            
            // Update UI
            hello.setText("Welcome, " + student.getName());
        }
    }

    private void populateUIWithTeacherData(TeacherUser teacher) {
        hello.setText("Welcome, " + teacher.getName());

        if (teacher.getPhone() != null) phoneEditText.setText(teacher.getPhone());
        if (teacher.getBio() != null) bioEditText.setText(teacher.getBio());
        if (teacher.getLocation() != null) setSpinnerSelection(citySpinner, teacher.getLocation());

        radioGroupDriveType.check(teacher.isDriverType() ? radioAutomatic.getId() : radioManual.getId());
        radioGroupAvailability.check(teacher.isAvailable() ? radioAvailableYes.getId() : R.id.radioAvailableNo);

        selectedPrice = teacher.getPrice();
        selectedExperience = teacher.getExperience();
        priceSeekBar.setProgress((teacher.getPrice()/10)-10);
        experienceSeekBar.setProgress(teacher.getExperience());
    }

    @Override public void oncallbackArryStudent(ArrayList<StudentUser> students) {}
    @Override public void onCallbackTeacher(ArrayList<TeacherUser> teachers) {}

    private void setSpinnerSelection(Spinner spinner, String value) {
        if (value == null) return;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(value)) {
                spinner.setSelection(i);
                return;
            }
        }
    }
}

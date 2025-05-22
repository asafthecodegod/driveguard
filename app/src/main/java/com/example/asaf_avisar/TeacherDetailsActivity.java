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
    private String    selectedCity       = "Not selected";
    private int       selectedPrice      = 150;
    private int       selectedExperience = 3;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_details);

        // 1) Wire up views
        hello                  = findViewById(R.id.userName);
        bioEditText            = findViewById(R.id.bioEditText);
        phoneEditText          = findViewById(R.id.phoneEditText);
        priceValueTextView     = findViewById(R.id.priceValueTextView);
        experienceValueTextView= findViewById(R.id.experienceValueTextView);
        radioGroupDriveType    = findViewById(R.id.radioGroupDriveType);
        radioManual            = findViewById(R.id.radioManual);
        radioAutomatic         = findViewById(R.id.radioAutomatic);
        radioGroupAvailability = findViewById(R.id.radioGroupAvailability);
        radioAvailableYes      = findViewById(R.id.radioAvailableYes);
        priceSeekBar           = findViewById(R.id.priceSeekBar);
        experienceSeekBar      = findViewById(R.id.experienceSeekBar);
        citySpinner            = findViewById(R.id.citySpinner);
        submitButton           = findViewById(R.id.submitButton);

        // 2) Helpers & initial model
        validator       = new Check();
        fireBaseManager = new FireBaseManager(this);
        teacherUser     = new TeacherUser();
        teacherUser.setTeacher(true);

        // 3) Load current teacher data
        fireBaseManager.readData(this, "Teacher", fireBaseManager.getUserid());

        // 4) Set up controls
        setupCitySpinner();
        setupPriceSeekBar();
        setupExperienceSeekBar();

        // 5) Submit handler
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
        // progress  (150/10)-10 = 5
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

    /** Build the model and persist via updateTeacher(...) **/
    private void updateTeacherUser(int driveType, String bio, String phone, boolean available) {
        teacherUser.setDriverType(driveType == 1);
        teacherUser.setLocation(selectedCity);
        teacherUser.setBio(bio);
        teacherUser.setPhone(phone);
        teacherUser.setAvailable(available);
        teacherUser.setPrice(selectedPrice);
        teacherUser.setExperience(selectedExperience);

        // **Critical**: write under /Teacher/uid
        fireBaseManager.updateTeacher(teacherUser);
        Log.d(TAG, "TeacherUser updated via updateTeacher()");
    }

    //====================================================================================
    // FirebaseCallback implementations
    //====================================================================================

    @Override
    public void onCallbackSingleTeacher(TeacherUser t) {
        if (t == null) return;
        teacherUser = t;
        hello.setText("Welcome, " + t.getName());

        // preload existing values
        if (t.getPhone()   != null) phoneEditText.setText(t.getPhone());
        if (t.getBio()     != null) bioEditText.setText(t.getBio());
        if (t.getLocation()!= null) setSpinnerSelection(citySpinner, t.getLocation());

        radioGroupDriveType.check(t.isDriverType() ? radioAutomatic.getId() : radioManual.getId());
        radioGroupAvailability.check(t.isAvailable() ? radioAvailableYes.getId() : R.id.radioAvailableNo);

        priceSeekBar.setProgress((t.getPrice()/10)-10);
        experienceSeekBar.setProgress(t.getExperience());
    }

    @Override public void oncallbackStudent(com.example.asaf_avisar.objects.StudentUser s) {}
    @Override public void oncallbackArryStudent(ArrayList<com.example.asaf_avisar.objects.StudentUser> u) {}
    @Override public void onCallbackTeacher(ArrayList<TeacherUser> u) {}

    private void setSpinnerSelection(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(value)) {
                spinner.setSelection(i);
                return;
            }
        }
    }
}

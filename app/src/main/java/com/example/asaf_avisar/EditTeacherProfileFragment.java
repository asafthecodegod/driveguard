package com.example.asaf_avisar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

/**
 * Fragment for editing teacher profile information.
 */
public class EditTeacherProfileFragment extends Fragment implements FirebaseCallback {

    //==========================================================================================
    // DISPLAY LAYER - UI Components
    //==========================================================================================

    private EditText nameEditText, bioEditText, phoneEditText, emailEditText;
    private SeekBar priceSeekBar, experienceSeekBar;
    private TextView priceTextView, experienceTextView;
    private Spinner locationSpinner;
    private RatingBar rankRatingBar;
    private Button saveButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_teacher_profile, container, false);

        // Initialize UI components
        initializeUIComponents(view);

        // Initialize logic
        initializeLogicComponents();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up listeners
        setupEventListeners();

        // Load teacher data
        loadTeacherData();
    }

    /**
     * Initialize all UI components
     */
    private void initializeUIComponents(View view) {
        // Basic info fields
        nameEditText = view.findViewById(R.id.teacher_name_edit);
        bioEditText = view.findViewById(R.id.teacher_bio_edit);
        phoneEditText = view.findViewById(R.id.teacher_phone_edit);
        emailEditText = view.findViewById(R.id.teacher_email_edit);

        // SeekBars and their TextViews
        priceSeekBar = view.findViewById(R.id.price_seekbar);
        priceTextView = view.findViewById(R.id.price_text);
        experienceSeekBar = view.findViewById(R.id.experience_seekbar);
        experienceTextView = view.findViewById(R.id.experience_text);

        // Spinners
        locationSpinner = view.findViewById(R.id.location_spinner);

        // Rating (read-only display of current rating)
        rankRatingBar = view.findViewById(R.id.rank_rating);

        // Button
        saveButton = view.findViewById(R.id.save_button);
    }

    /**
     * Set up event listeners
     */
    private void setupEventListeners() {
        // Price SeekBar listener
        priceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Calculate price: min 100, max 300, step 10
                int price = (progress + 10) * 10;
                priceTextView.setText("₪" + price);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Not needed
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Not needed
            }
        });

        // Experience SeekBar listener
        experienceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                experienceTextView.setText(progress + " years");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Not needed
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Not needed
            }
        });

        // Save button listener
        saveButton.setOnClickListener(v -> saveTeacherProfile());
    }

    //==========================================================================================
    // LOGIC LAYER - Business Logic
    //==========================================================================================

    private FireBaseManager fireBaseManager;
    private TeacherUser teacherUser;

    /**
     * Initialize logic components
     */
    private void initializeLogicComponents() {
        fireBaseManager = new FireBaseManager(getContext());
    }

    /**
     * Load teacher data from Firebase
     */
    private void loadTeacherData() {
        // Read teacher data from Firebase
        fireBaseManager.readData(this, "Teacher", fireBaseManager.getUserid());
    }

    /**
     * Save teacher profile to Firebase
     */
    private void saveTeacherProfile() {
        // Validate inputs
        if (!validateInputs()) {
            return;
        }

        // Update teacher object with form values
        updateTeacherWithFormValues();

        // Save to Firebase
        fireBaseManager.updateUser(teacherUser);

        // Show success message
        Toast.makeText(getContext(), "Teacher profile updated successfully", Toast.LENGTH_SHORT).show();
    }

    /**
     * Validate user inputs
     */
    private boolean validateInputs() {
        // Check if name is not empty
        if (nameEditText.getText().toString().trim().isEmpty()) {
            nameEditText.setError("Name is required");
            return false;
        }

        // Check if phone is not empty
        if (phoneEditText.getText().toString().trim().isEmpty()) {
            phoneEditText.setError("Phone number is required");
            return false;
        }

        return true;
    }

    /**
     * Update teacher user object with form values
     */
    private void updateTeacherWithFormValues() {
        // Create a new teacher user if not loaded
        if (teacherUser == null) {
            teacherUser = new TeacherUser();
        }

        // Set basic info
        teacherUser.setName(nameEditText.getText().toString().trim());
        teacherUser.setBio(bioEditText.getText().toString().trim());
        teacherUser.setPhone(phoneEditText.getText().toString().trim());

        // Keep email unchanged (or update if made editable)
        if (!emailEditText.isEnabled()) {
            // Skip setting email - can't be changed
        } else {
            teacherUser.setEmail(emailEditText.getText().toString().trim());
        }

        // Set location
        teacherUser.setLocation(locationSpinner.getSelectedItem().toString());

        // Calculate price from SeekBar
        int priceProgress = priceSeekBar.getProgress();
        int price = (priceProgress + 10) * 10; // min 100, max 300, step 10
        teacherUser.setPrice(price);

        // Set experience
        teacherUser.setExperience(experienceSeekBar.getProgress());

        // Set availability (could add checkbox for this)
        teacherUser.setAvailable(true);


    }

    /**
     * Populate UI with teacher data
     */
    private void populateUIWithTeacherData(TeacherUser teacher) {
        if (teacher == null) {
            return;
        }

        // Set basic info
        nameEditText.setText(teacher.getName());
        bioEditText.setText(teacher.getBio());
        phoneEditText.setText(teacher.getPhone());
        emailEditText.setText(teacher.getEmail());
        emailEditText.setEnabled(false); // Email cannot be changed

        // Set location spinner selection
        if (teacher.getLocation() != null && !teacher.getLocation().isEmpty()) {
            setSpinnerSelection(locationSpinner, teacher.getLocation());
        }


        // Set price SeekBar
        int price = teacher.getPrice();
        if (price > 0) {
            int progress = (price / 10) - 10; // Convert price to progress (100=0, 110=1, etc.)
            priceSeekBar.setProgress(progress);
            priceTextView.setText("₪" + price);
        }

        // Set experience SeekBar
        int experience = teacher.getExperience();
        if (experience > 0) {
            experienceSeekBar.setProgress(experience);
            experienceTextView.setText(experience + " years");
        }

        // Set rating display (not editable)
        rankRatingBar.setRating(teacher.getRank());
    }

    /**
     * Helper to set spinner selection based on value
     */
    private void setSpinnerSelection(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    //==========================================================================================
    // CALLBACK IMPLEMENTATIONS - Firebase Data Handling
    //==========================================================================================

    @Override
    public void oncallbackStudent(StudentUser student) {
        // Not used in this fragment
    }

    @Override
    public void oncallbackArryStudent(ArrayList<StudentUser> students) {
        // Not used in this fragment
    }

    @Override
    public void onCallbackTeacher(ArrayList<TeacherUser> teachers) {
        if (teachers != null && !teachers.isEmpty()) {
            // Get the teacher user
            teacherUser = teachers.get(0);

            // Populate UI with teacher data
            populateUIWithTeacherData(teacherUser);
        }
    }

    @Override
    public void onCallbackSingleTeacher(TeacherUser teacher) {
        if (teacher != null) {
            // Get the teacher user
            teacherUser = teacher;

            // Populate UI with teacher data
            populateUIWithTeacherData(teacher);
        }
    }
}
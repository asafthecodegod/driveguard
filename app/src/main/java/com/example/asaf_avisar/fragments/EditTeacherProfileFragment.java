package com.example.asaf_avisar.fragments;

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

import com.example.asaf_avisar.FireBaseManager;
import com.example.asaf_avisar.R;
import com.example.asaf_avisar.callbacks.FirebaseCallback;
import com.example.asaf_avisar.objects.Check;
import com.example.asaf_avisar.objects.StudentUser;
import com.example.asaf_avisar.objects.TeacherUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    private Check check;

    //==========================================================================================
    // LOGIC LAYER - Business Logic
    //==========================================================================================

    private FireBaseManager fireBaseManager;
    private TeacherUser teacherUser;
    private String currentUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_teacher_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        check = new Check();
        initializeFirebaseManager();
        initializeUIComponents(view);
        setupEventListeners();
        loadCurrentTeacherData();
    }

    private void initializeFirebaseManager() {
        fireBaseManager = new FireBaseManager(getContext());
        currentUserId = fireBaseManager.getUserid();

        if (currentUserId == null) {
            Toast.makeText(getContext(), "Error: User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeUIComponents(View view) {
        nameEditText = view.findViewById(R.id.teacher_name_edit);
        bioEditText = view.findViewById(R.id.teacher_bio_edit);
        phoneEditText = view.findViewById(R.id.teacher_phone_edit);
        emailEditText = view.findViewById(R.id.teacher_email_edit);

        priceSeekBar = view.findViewById(R.id.price_seekbar);
        priceTextView = view.findViewById(R.id.price_text);
        experienceSeekBar = view.findViewById(R.id.experience_seekbar);
        experienceTextView = view.findViewById(R.id.experience_text);

        locationSpinner = view.findViewById(R.id.location_spinner);
        rankRatingBar = view.findViewById(R.id.rank_rating);
        rankRatingBar.setIsIndicator(true);

        saveButton = view.findViewById(R.id.save_button);
        setDefaultSeekBarValues();
    }

    private void setDefaultSeekBarValues() {
        priceSeekBar.setProgress(5);
        priceTextView.setText("₪150");

        experienceSeekBar.setProgress(1);
        experienceTextView.setText("1 years");
    }

    private void setupEventListeners() {
        priceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int price = (progress + 10) * 10;
                priceTextView.setText("₪" + price);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        experienceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                experienceTextView.setText(progress + " years");
            }
            public void onStartTrackingTouch(SeekBar seekBar) {}
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        saveButton.setOnClickListener(v -> saveTeacherProfile());
    }

    private void loadCurrentTeacherData() {
        if (currentUserId == null) {
            Toast.makeText(getContext(), "Error: User ID not available", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(getContext(), "Loading teacher profile...", Toast.LENGTH_SHORT).show();
        fireBaseManager.readData(this, "Teacher", currentUserId);
    }

    private void saveTeacherProfile() {
        if (!validateInputs()) return;

        if (teacherUser == null) {
            teacherUser = new TeacherUser();
        }

        updateTeacherWithFormValues();

        Toast.makeText(getContext(), "Saving teacher profile...", Toast.LENGTH_SHORT).show();
        fireBaseManager.updateUser(teacherUser);

        Toast.makeText(getContext(), "Teacher profile updated successfully", Toast.LENGTH_SHORT).show();
    }

    private boolean validateInputs() {
        String name = nameEditText.getText().toString().trim();
        if (name.isEmpty()) {
            nameEditText.setError("Name is required");
            nameEditText.requestFocus();
            return false;
        }

        String phone = phoneEditText.getText().toString().trim();
        if (phone.isEmpty()) {
            phoneEditText.setError("Please enter your phone number");
            phoneEditText.requestFocus();
            return false;
        }

        if (!check.checkPhone(phone)) {
            phoneEditText.setError("Enter a valid phone number");
            phoneEditText.requestFocus();
            return false;
        }

        return true;
    }

    private void updateTeacherWithFormValues() {
        teacherUser.setName(nameEditText.getText().toString().trim());
        teacherUser.setBio(bioEditText.getText().toString().trim());
        teacherUser.setPhone(phoneEditText.getText().toString().trim());

        if (emailEditText.isEnabled()) {
            teacherUser.setEmail(emailEditText.getText().toString().trim());
        }

        teacherUser.setLocation(locationSpinner.getSelectedItem().toString());

        int price = (priceSeekBar.getProgress() + 10) * 10;
        teacherUser.setPrice(price);

        teacherUser.setExperience(experienceSeekBar.getProgress());
        teacherUser.setAvailable(true);
    }

    private void populateUIWithTeacherData(TeacherUser teacher) {
        if (teacher == null) {
            Toast.makeText(getContext(), "No existing teacher profile found. Please fill in your details.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (teacher.getName() != null) nameEditText.setText(teacher.getName());
        if (teacher.getBio() != null) bioEditText.setText(teacher.getBio());
        if (teacher.getPhone() != null) phoneEditText.setText(teacher.getPhone());
        if (teacher.getEmail() != null) {
            emailEditText.setText(teacher.getEmail());
            emailEditText.setEnabled(false);
        }

        if (teacher.getLocation() != null) {
            setSpinnerSelection(locationSpinner, teacher.getLocation());
        }

        if (teacher.getPrice() > 0) {
            int progress = Math.max(0, Math.min(20, (teacher.getPrice() / 10) - 10));
            priceSeekBar.setProgress(progress);
            priceTextView.setText("₪" + teacher.getPrice());
        }

        experienceSeekBar.setProgress(Math.min(20, teacher.getExperience()));
        experienceTextView.setText(teacher.getExperience() + " years");

        rankRatingBar.setRating((float) teacher.getRank());

        Toast.makeText(getContext(), "Teacher profile loaded successfully", Toast.LENGTH_SHORT).show();
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        if (spinner.getAdapter() == null) return;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    //==========================================================================================
    // CALLBACK IMPLEMENTATIONS
    //==========================================================================================

    @Override
    public void oncallbackStudent(StudentUser student) {
        if (student == null) {
            Toast.makeText(getContext(), "No teacher profile found. Please create one.", Toast.LENGTH_SHORT).show();
        } else {
            teacherUser = new TeacherUser();
            teacherUser.setName(student.getName());
            teacherUser.setEmail(student.getEmail());
            teacherUser.setPhone(student.getPhone());
            teacherUser.setBio(student.getBio());
            teacherUser.setPrice(student.getPrice());
            populateUIWithTeacherData(teacherUser);
        }
    }

    public void getStudentById(String userId) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");

        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    StudentUser student = snapshot.getValue(StudentUser.class);
                    oncallbackStudent(student);
                } else {
                    oncallbackStudent(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                oncallbackStudent(null);
            }
        });
    }

    @Override
    public void oncallbackArryStudent(ArrayList<StudentUser> students) {
        // Not used
    }

    @Override
    public void onCallbackTeacher(ArrayList<TeacherUser> teachers) {
        if (teachers != null && !teachers.isEmpty()) {
            teacherUser = teachers.get(0);
            populateUIWithTeacherData(teacherUser);
        } else {
            Toast.makeText(getContext(), "No teacher profile found. Please create one.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCallbackSingleTeacher(TeacherUser teacher) {
        if (teacher != null) {
            teacherUser = teacher;
            populateUIWithTeacherData(teacherUser);
        } else {
            Toast.makeText(getContext(), "Teacher data could not be loaded.", Toast.LENGTH_SHORT).show();
        }
    }
}

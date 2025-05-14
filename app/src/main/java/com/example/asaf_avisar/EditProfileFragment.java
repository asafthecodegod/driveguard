package com.example.asaf_avisar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * The type Edit profile fragment.
 */
public class EditProfileFragment extends Fragment implements FirebaseCallback, View.OnClickListener {

    //==============================================================================================
    // UI COMPONENTS (DISPLAY LAYER)
    //==============================================================================================

    private TextInputEditText etName, etBio, etCity, etInvestment;
    private TextView tvLicenseDate;
    private RadioGroup rgDriverType;
    private RadioButton rbManual, rbAutomatic;
    private CheckBox cbGreenForm, cbTheory;
    private Button btnSave, btnChangePhoto;
    private ImageView profileImage;

    private static final int IMAGE_REQUEST_CODE = 1001;

    /**
     * Instantiates a new Edit profile fragment.
     */
    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase manager
        initializeFirebaseManager();

        // Initialize UI components
        initializeUIComponents(view);
    }

    /**
     * Initialize UI components and set up click listeners
     */
    private void initializeUIComponents(View view) {
        // Text input fields
        etName = view.findViewById(R.id.et_name);
        etBio = view.findViewById(R.id.et_bio);
        etCity = view.findViewById(R.id.et_city);
        etInvestment = view.findViewById(R.id.et_investment);

        // License date TextView
        tvLicenseDate = view.findViewById(R.id.tv_license_date);
        tvLicenseDate.setOnClickListener(this);

        // Radio buttons for driver type
        rgDriverType = view.findViewById(R.id.rg_driver_type);
        rbManual = view.findViewById(R.id.rb_manual);
        rbAutomatic = view.findViewById(R.id.rb_automatic);

        // Checkboxes
        cbGreenForm = view.findViewById(R.id.cb_green_form);
        cbTheory = view.findViewById(R.id.cb_theory);

        // Buttons
        btnSave = view.findViewById(R.id.btn_save);
        btnSave.setOnClickListener(this);

        btnChangePhoto = view.findViewById(R.id.btn_change_photo);
        btnChangePhoto.setOnClickListener(this);

        // Profile image
        profileImage = view.findViewById(R.id.et_profile_image);
    }

    @Override
    public void onClick(View v) {
        if (v == tvLicenseDate) {
            showDatePicker();
        } else if (v == btnSave) {
            saveChanges();
        } else if (v == btnChangePhoto) {
            changeProfilePicture();
        }
    }

    /**
     * Shows the date picker dialog for license date selection
     */
    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // If we already have a date selected, use it
        if (selectedLicenseDate != null) {
            year = selectedLicenseDate.get(Calendar.YEAR);
            month = selectedLicenseDate.get(Calendar.MONTH);
            day = selectedLicenseDate.get(Calendar.DAY_OF_MONTH);
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
                    selectedLicenseDate = Calendar.getInstance();
                    selectedLicenseDate.set(selectedYear, selectedMonth, selectedDay);

                    // Format the date for display
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    tvLicenseDate.setText(dateFormat.format(selectedLicenseDate.getTime()));
                },
                year, month, day);

        // Restrict future dates
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    /**
     * Open gallery to select profile image
     */
    private void changeProfilePicture() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap selectedImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                profileImage.setImageBitmap(selectedImage);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error loading image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //==============================================================================================
    // BUSINESS LOGIC LAYER
    //==============================================================================================

    private FireBaseManager fireBaseManager;
    private StudentUser studentUser;
    private Calendar selectedLicenseDate;

    /**
     * Initialize Firebase manager and load user data
     */
    private void initializeFirebaseManager() {
        fireBaseManager = new FireBaseManager(getContext());
        // Load data from Firebase
        fireBaseManager.readData(this, "Student", fireBaseManager.getUserid());
    }

    /**
     * Save user data to Firebase
     */
    private void saveChanges() {
        if (studentUser != null) {
            // Validate inputs
            if (etName.getText().toString().trim().isEmpty()) {
                etName.setError("Name is required");
                etName.requestFocus();
                return;
            }

            if (etInvestment.getText().toString().trim().isEmpty()) {
                etInvestment.setText("0"); // Default to 0 if empty
            }

            // Update user data
            studentUser.setName(etName.getText().toString());
            studentUser.setBio(etBio.getText().toString());
            studentUser.setCity(etCity.getText().toString());

            try {
                studentUser.setDrivingInvestment(Integer.parseInt(etInvestment.getText().toString()));
            } catch (NumberFormatException e) {
                etInvestment.setError("Please enter a valid number");
                etInvestment.requestFocus();
                return;
            }

            studentUser.setHasGreenForm(cbGreenForm.isChecked());
            studentUser.setPassedTheory(cbTheory.isChecked());

            // Handle Driver Type selection from RadioGroup
            studentUser.setDriverType(rbAutomatic.isChecked());

            if (selectedLicenseDate != null) {
                Date licenseDate = selectedLicenseDate.getTime();
                studentUser.setLicenseDate(licenseDate);
                studentUser.setHasLicense(true);
            } else {
                studentUser.setHasLicense(false);
            }

            // Process profile image
            if (profileImage.getDrawable() != null) {
                try {
                    Bitmap bitmap = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();
                    String profilePhotoBase64 = ImageUtils.convertTo64Base(bitmap);
                    studentUser.setProfilePhotoBase64(profilePhotoBase64);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error processing image", Toast.LENGTH_SHORT).show();
                }
            }

            // Show progress indicator
            Toast.makeText(getContext(), "Updating profile...", Toast.LENGTH_SHORT).show();

            // Update user in Firebase
            fireBaseManager.updateUser(studentUser);
            Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Error: Unable to save user data", Toast.LENGTH_SHORT).show();
        }
    }

    //==============================================================================================
    // FIREBASE CALLBACKS
    //==============================================================================================

    @Override
    public void oncallbackStudent(StudentUser user) {
        this.studentUser = user;

        if (user == null) {
            Toast.makeText(getContext(), "Error: Could not load user data", Toast.LENGTH_SHORT).show();
            return;
        }

        // Populate UI with user data
        etName.setText(user.getName());
        etBio.setText(user.getBio());
        etCity.setText(user.getCity());
        etInvestment.setText(String.valueOf(user.getDrivingInvestment()));
        cbGreenForm.setChecked(user.isHasGreenForm());
        cbTheory.setChecked(user.isPassedTheory());

        // Set Driver Type RadioButton selection
        if (user.isDriverType()) {
            rgDriverType.check(R.id.rb_automatic);
        } else {
            rgDriverType.check(R.id.rb_manual);
        }

        // Set license date
        if (user.getLicenseDate() != null) {
            selectedLicenseDate = Calendar.getInstance();
            selectedLicenseDate.setTime(user.getLicenseDate());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            tvLicenseDate.setText(dateFormat.format(user.getLicenseDate()));
        }

        // Set profile image
        if (user.getProfilePhotoBase64() != null && !user.getProfilePhotoBase64().isEmpty()) {
            try {
                Bitmap decodedProfilePic = ImageUtils.convert64base(user.getProfilePhotoBase64());
                profileImage.setImageBitmap(decodedProfilePic);
            } catch (Exception e) {
                e.printStackTrace();
                // If there's an error, we'll just use the default image
            }
        }
    }

    @Override
    public void oncallbackArryStudent(ArrayList<StudentUser> students) {
        // Handle array of students if needed
    }

    @Override
    public void onCallbackTeacher(ArrayList<TeacherUser> teachers) {
        // Handle teacher data if needed
    }

    @Override
    public void onCallbackSingleTeacher(TeacherUser teacher) {

    }
}
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.asaf_avisar.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * The type Edit profile fragment.
 */
public class EditProfileFragment extends Fragment implements FirebaseCallback {

    private EditText etName, etBio, etCity, etInvestment;
    private TextView tvLicenseDate;
    private RadioGroup rgDriverType;
    private CheckBox cbGreenForm, cbTheory;
    private Button btnSave, btnChangePhoto;
    private ImageView profileImage;
    private StudentUser studentUser;
    private FireBaseManager fireBaseManager;
    private Calendar selectedLicenseDate;

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

        // Initialize FireBaseManager
        fireBaseManager = new FireBaseManager(getContext());

        // Initialize views
        etName = view.findViewById(R.id.et_name);
        etBio = view.findViewById(R.id.et_bio);
        etCity = view.findViewById(R.id.et_city);
        etInvestment = view.findViewById(R.id.et_investment);
        tvLicenseDate = view.findViewById(R.id.tv_license_date);
        rgDriverType = view.findViewById(R.id.rg_driver_type);
        cbGreenForm = view.findViewById(R.id.cb_green_form);
        cbTheory = view.findViewById(R.id.cb_theory);
        btnSave = view.findViewById(R.id.btn_save);
        btnChangePhoto = view.findViewById(R.id.btn_change_photo);
        profileImage = view.findViewById(R.id.et_profile_image);

        // License date picker
        tvLicenseDate.setOnClickListener(v -> showDatePicker());

        // Save button action
        btnSave.setOnClickListener(v -> saveChanges());

        // Change photo button action
        btnChangePhoto.setOnClickListener(v -> changeProfilePicture());

        // Load data from Firebase
        fireBaseManager.readData(this, "Student", fireBaseManager.getUserid());
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            selectedLicenseDate = Calendar.getInstance();
            selectedLicenseDate.set(year, month, dayOfMonth);
            tvLicenseDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void saveChanges() {
        if (studentUser != null) {
            studentUser.setName(etName.getText().toString());
            studentUser.setBio(etBio.getText().toString());
            studentUser.setCity(etCity.getText().toString());
            studentUser.setDrivingInvestment(Integer.parseInt(etInvestment.getText().toString()));
            studentUser.setHasGreenForm(cbGreenForm.isChecked());
            studentUser.setPassedTheory(cbTheory.isChecked());

            // Handle Driver Type selection from RadioGroup
            int selectedDriverTypeId = rgDriverType.getCheckedRadioButtonId();
            if (selectedDriverTypeId != -1) {
                RadioButton selectedRadioButton = getView().findViewById(selectedDriverTypeId);
                studentUser.setDriverType(selectedRadioButton.getText().toString().equals("Automatic"));
            }

            if (selectedLicenseDate != null) {
                Date licenseDate = selectedLicenseDate.getTime();
                studentUser.setLicenseDate(licenseDate);
                studentUser.setHasLicense(true);
            } else {
                studentUser.setHasLicense(false);
            }

            if (profileImage.getDrawable() != null) {
                Bitmap bitmap = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();
                String profilePhotoBase64 = ImageUtils.convertTo64Base(bitmap);
                studentUser.setProfilePhotoBase64(profilePhotoBase64);
            }

            // Update user in Firebase
            fireBaseManager.updateUser(studentUser);
            Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_SHORT).show();
        }
    }

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

                // Optionally, upload this image to Firebase or save its path to update in the user profile later.
                // You can save the image path or upload it directly to Firebase Storage.
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error loading image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void oncallbackStudent(StudentUser user) {
        this.studentUser = user;
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

        if (user.getLicenseDate() != null) {
            selectedLicenseDate = Calendar.getInstance();
            selectedLicenseDate.setTime(user.getLicenseDate());
            tvLicenseDate.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(user.getLicenseDate()));
        }
        if (user.getProfilePhotoBase64() != null && !user.getProfilePhotoBase64().isEmpty()) {
            Bitmap decodedProfilePic = ImageUtils.convert64base(user.getProfilePhotoBase64());
            profileImage.setImageBitmap(decodedProfilePic);
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
}

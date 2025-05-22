package com.example.asaf_avisar.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.asaf_avisar.FireBaseManager;
import com.example.asaf_avisar.callbacks.FirebaseCallback;
import com.example.asaf_avisar.R;
import com.example.asaf_avisar.objects.TeacherUser;
import com.example.asaf_avisar.objects.ImageUtils;
import com.example.asaf_avisar.objects.StudentUser;

import java.util.ArrayList;

/**
 * Fragment to display a teacher's profile details
 */
public class TeacherProfileFragment extends Fragment implements FirebaseCallback {

    // UI Components
    private ImageView profileImageView;
    private TextView nameTextView, emailTextView, phoneTextView, bioTextView;
    private TextView experienceTextView, priceTextView, locationTextView;
    private RatingBar ratingBar;
    private CardView profileCard, statsCard, contactCard;
    private LinearLayout buttonContainer;

    // Data
    private TeacherUser teacher;
    private FireBaseManager fireBaseManager;
    private String teacherId;
    private boolean isOwnProfile = false;

    // Teacher data from callbacks
    private String teacherName, teacherEmail, teacherPhone;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_teacher_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase
        fireBaseManager = new FireBaseManager(requireActivity());

        // Initialize UI components
        initializeUIComponents(view);

        // Determine which teacher profile to load
        determineProfileToLoad();

        // Load teacher data
        loadTeacherData();
    }

    /**
     * Determine which teacher profile to load
     */
    private void determineProfileToLoad() {
        Bundle args = getArguments();
        String currentUserId = fireBaseManager.getUserid();

        if (args != null) {
            teacherId = args.getString("TEACHER_ID");
            String teacherName = args.getString("TEACHER_NAME");

            if (teacherName != null && !teacherName.isEmpty() && nameTextView != null) {
                nameTextView.setText(teacherName);
            }

            isOwnProfile = teacherId != null && teacherId.equals(currentUserId);
        } else {
            // No arguments, this is the logged-in teacher viewing their own profile
            teacherId = currentUserId;
            isOwnProfile = true;
        }
    }

    /**
     * Initialize UI components
     */
    private void initializeUIComponents(View view) {
        try {
            // Cards
            profileCard = view.findViewById(R.id.profile_card);
            statsCard = view.findViewById(R.id.stats_card);
            contactCard = view.findViewById(R.id.contact_card);
            buttonContainer = view.findViewById(R.id.button_container);

            // Profile section
            profileImageView = view.findViewById(R.id.profile_image);
            nameTextView = view.findViewById(R.id.teacher_name);
            ratingBar = view.findViewById(R.id.teacher_rating);
            bioTextView = view.findViewById(R.id.teacher_bio);

            // Stats section
            experienceTextView = view.findViewById(R.id.teacher_experience);
            priceTextView = view.findViewById(R.id.teacher_price);
            locationTextView = view.findViewById(R.id.teacher_location);

            // Contact section
            emailTextView = view.findViewById(R.id.teacher_email);
            phoneTextView = view.findViewById(R.id.teacher_phone);

            // Hide the button container
            if (buttonContainer != null) {
                buttonContainer.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            Toast.makeText(getContext(), "Error loading profile view", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Load teacher data from Firebase
     */
    private void loadTeacherData() {
        if (teacherId == null) {
            Toast.makeText(getContext(), "Teacher ID not available", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Load from Student collection (where teachers are actually stored)
            fireBaseManager.readData(this, "Student", teacherId);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error loading teacher data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Update UI with teacher data
     */
    private void updateUI(TeacherUser teacher) {
        if (teacher == null) {
            Toast.makeText(getContext(), "Teacher profile not found", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            this.teacher = teacher;

            // Basic info - use data from onCallbackSingleTeacher if available
            if (nameTextView != null) {
                String name = teacherName != null ? teacherName :
                        (teacher.getName() != null ? teacher.getName() : "Unknown Teacher");
                nameTextView.setText(name);
            }

            if (ratingBar != null) {
                ratingBar.setRating(teacher.getRank());
            }

            // Bio
            if (bioTextView != null) {
                String bio = (teacher.getBio() != null && !teacher.getBio().isEmpty())
                        ? teacher.getBio() : "No bio available";
                bioTextView.setText(bio);
            }

            // Contact info - use data from onCallbackSingleTeacher if available
            if (emailTextView != null) {
                String email = teacherEmail != null ? teacherEmail :
                        (teacher.getEmail() != null ? teacher.getEmail() : "Email not available");
                emailTextView.setText(email);
            }

            if (phoneTextView != null) {
                String phone = teacherPhone != null ? teacherPhone :
                        (teacher.getPhone() != null ? teacher.getPhone() : "Phone not available");
                phoneTextView.setText(phone);
            }

            // Teacher stats
            if (experienceTextView != null) {
                experienceTextView.setText(teacher.getExperience() + " years");
            }

            if (priceTextView != null) {
                priceTextView.setText("₪" + teacher.getPrice() + "/hour");
            }

            if (locationTextView != null) {
                String location = teacher.getLocation() != null ? teacher.getLocation() : "Location not specified";
                locationTextView.setText(location);
            }

            // Profile image
            displayProfileImage(teacher.getProfilePhotoBase64());

        } catch (Exception e) {
            Toast.makeText(getContext(), "Error updating profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Display profile image from base64 string
     */
    private void displayProfileImage(String base64Image) {
        if (profileImageView != null) {
            if (base64Image != null && !base64Image.isEmpty()) {
                try {
                    Bitmap bitmap = ImageUtils.convert64base(base64Image);
                    if (bitmap != null) {
                        profileImageView.setImageBitmap(bitmap);
                    } else {
                        profileImageView.setImageResource(R.drawable.ic_default_profile);
                    }
                } catch (Exception e) {
                    profileImageView.setImageResource(R.drawable.ic_default_profile);
                }
            } else {
                profileImageView.setImageResource(R.drawable.ic_default_profile);
            }
        }
    }

    //==========================================================================================
    // CALLBACK IMPLEMENTATIONS
    //==========================================================================================

    @Override
    public void oncallbackStudent(StudentUser student) {
        if (student != null) {
            if (student.isTeacher()) {
                try {
                    TeacherUser teacher = (TeacherUser) student;
                    updateUI(teacher);
                } catch (ClassCastException e) {
                    TeacherUser teacher = createTeacherFromStudent(student);
                    updateUI(teacher);
                }
            } else {
                TeacherUser teacher = createTeacherFromStudent(student);
                updateUI(teacher);
            }
        } else {
            Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Create TeacherUser from StudentUser data
     */
    private TeacherUser createTeacherFromStudent(StudentUser student) {
        TeacherUser teacher = new TeacherUser();

        // Copy basic StudentUser fields
        teacher.setName(teacherName != null ? teacherName : "Medi");
        teacher.setEmail(teacherEmail != null ? teacherEmail : "contact@gmail.com.com");
        teacher.setId(student.getId() != null ? student.getId() : teacherId);
        teacher.setProfilePhotoBase64(student.getProfilePhotoBase64());
        teacher.setBio(student.getBio() != null ? student.getBio() : "Experienced driving instructor");
        teacher.setTeacher(true);

        // Set default teacher-specific values
        teacher.setRank(4);
        teacher.setExperience(8);
        teacher.setPrice(150);
        teacher.setLocation("Tel Aviv");
        teacher.setAvailable(true);
        teacher.setTeacherId(student.getId() != null ? student.getId() : teacherId);
        teacher.setPhone(teacherPhone != null ? teacherPhone : "050-68308736");

        return teacher;
    }

    @Override
    public void onCallbackSingleTeacher(TeacherUser teacherUser) {
        if (teacherUser != null) {
            // Store the real teacher data
            teacherPhone = teacherUser.getPhone();
            teacherEmail = teacherUser.getEmail();
            teacherName = teacherUser.getName();

            // Update UI with the complete teacher data
            updateUI(teacherUser);
        }
    }

    @Override
    public void oncallbackArryStudent(ArrayList<StudentUser> students) {
        // Not used for single teacher profile
    }

    @Override
    public void onCallbackTeacher(ArrayList<TeacherUser> teachers) {
        // Not used when reading from Student collection
    }

    /**
     * Static factory method to create a new instance of the fragment with teacher ID
     */
    public static TeacherProfileFragment newInstance(String teacherId, String teacherName) {
        TeacherProfileFragment fragment = new TeacherProfileFragment();
        Bundle args = new Bundle();
        args.putString("TEACHER_ID", teacherId);
        args.putString("TEACHER_NAME", teacherName);
        fragment.setArguments(args);
        return fragment;
    }
}
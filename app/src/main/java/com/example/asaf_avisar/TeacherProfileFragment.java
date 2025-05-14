package com.example.asaf_avisar;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

/**
 * Fragment to display a teacher's profile details
 */
public class TeacherProfileFragment extends Fragment implements FirebaseCallback {

    private static final String TAG = "TeacherProfileFragment";

    // UI Components
    private ImageView profileImageView;
    private TextView nameTextView, emailTextView, phoneTextView, bioTextView;
    private TextView experienceTextView, priceTextView, locationTextView;
    private RatingBar ratingBar;
    private Button contactButton, bookLessonButton;
    private CardView profileCard, statsCard, contactCard;
    private LinearLayout buttonContainer;

    // Data
    private TeacherUser teacher;
    private FireBaseManager fireBaseManager;
    private String teacherId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Inflating fragment_teacher_profile layout");
        return inflater.inflate(R.layout.fragment_teacher_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "onViewCreated: Setting up the fragment");

        // Initialize Firebase
        fireBaseManager = new FireBaseManager(requireActivity());

        // Initialize UI components
        initializeUIComponents(view);

        // Get teacher ID from arguments if any
        Bundle args = getArguments();
        if (args != null) {
            teacherId = args.getString("TEACHER_ID");
            Log.d(TAG, "Teacher ID from arguments: " + teacherId);

            // Set name from args for immediate display while loading full profile
            String teacherName = args.getString("TEACHER_NAME");
            if (teacherName != null && !teacherName.isEmpty()) {
                nameTextView.setText(teacherName);
            }
        } else {
            // No arguments, this is the logged-in teacher viewing their own profile
            teacherId = fireBaseManager.getUserid();
            Log.d(TAG, "No arguments, using current user ID: " + teacherId);
        }

        // Load teacher data
        loadTeacherData();
    }

    /**
     * Initialize UI components
     */
    private void initializeUIComponents(View view) {
        try {
            // Cards - make sure all cards are visible
            profileCard = view.findViewById(R.id.profile_card);
            statsCard = view.findViewById(R.id.stats_card);
            contactCard = view.findViewById(R.id.contact_card);
            buttonContainer = view.findViewById(R.id.button_container);

            if (profileCard != null) profileCard.setVisibility(View.VISIBLE);
            if (statsCard != null) statsCard.setVisibility(View.VISIBLE);
            if (contactCard != null) contactCard.setVisibility(View.VISIBLE);

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

            // Buttons
            contactButton = view.findViewById(R.id.contact_button);
            bookLessonButton = view.findViewById(R.id.book_lesson_button);

            // Set up button click listeners
            if (contactButton != null) {
                contactButton.setOnClickListener(v -> contactTeacher());
            }

            if (bookLessonButton != null) {
                bookLessonButton.setOnClickListener(v -> bookLesson());
            }

            // Hide contact/book buttons if this is the user's own profile
            if (teacherId == null || teacherId.equals(fireBaseManager.getUserid())) {
                if (buttonContainer != null) buttonContainer.setVisibility(View.GONE);
            } else {
                // Viewing someone else's profile
                if (buttonContainer != null) buttonContainer.setVisibility(View.VISIBLE);
            }

            Log.d(TAG, "UI components initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing UI components: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Error loading profile view", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Load teacher data from Firebase
     */
    private void loadTeacherData() {
        // If it's a sample teacher (from development data)
        if (teacherId != null && teacherId.startsWith("sample_")) {
            // Get sample teacher index
            int index = Integer.parseInt(teacherId.substring(7));
            createSampleTeacher(index);
            return;
        }

        // Load teacher from Firebase - either logged-in user or specified teacher
        String idToLoad = (teacherId != null) ? teacherId : fireBaseManager.getUserid();
        Log.d(TAG, "Loading teacher data for ID: " + idToLoad);
        fireBaseManager.readData(this, "Teacher", idToLoad);
    }

    /**
     * Create a sample teacher for development purposes
     */
    private void createSampleTeacher(int index) {
        String[] names = {"Alice", "Bob", "Charlie", "David", "Emma", "Frank", "Grace", "Hannah", "Ian", "Julia"};
        int[] ratings = {5, 4, 3, 4, 5, 2, 4, 3, 5, 4};
        String[] bios = {
                "Experienced driving instructor with 8+ years helping students pass their tests.",
                "Patient instructor specializing in nervous drivers. Great track record.",
                "Certified instructor with expertise in defensive driving techniques.",
                "Former race car driver turned instructor. I make driving fun!",
                "Specializing in parallel parking and city driving techniques.",
                "Calm and patient instructor with 5+ years of experience.",
                "Bilingual instructor offering lessons in English and Hebrew.",
                "Experienced instructor specialized in helping student drivers build confidence.",
                "Retired driving examiner with inside knowledge of the test requirements.",
                "Patient instructor specializing in automatic transmission vehicles."
        };

        String[] locations = {"Tel Aviv", "Jerusalem", "Haifa", "Beersheba", "Netanya",
                "Tel Aviv", "Haifa", "Jerusalem", "Tel Aviv", "Haifa"};

        int[] experiences = {8, 5, 12, 7, 4, 5, 6, 9, 15, 3};
        int[] prices = {150, 140, 160, 150, 145, 130, 155, 150, 170, 135};

        // Use index to create a consistent sample teacher
        int idx = Math.min(index, names.length - 1);
        teacher = new TeacherUser(
                names[idx],
                names[idx].toLowerCase() + "@example.com",
                "05" + (50000000 + idx * 1111111),
                null,
                ratings[idx]
        );
        teacher.setTeacherId("sample_" + idx);
        teacher.setBio(bios[idx]);
        teacher.setLocation(locations[idx]);
        teacher.setExperience(experiences[idx]);
        teacher.setPrice(prices[idx]);

        // Update UI
        updateUI(teacher);
    }

    /**
     * Update UI with teacher data
     */
    private void updateUI(TeacherUser teacher) {
        if (teacher == null) {
            Log.e(TAG, "Teacher is null in updateUI");
            return;
        }

        try {
            // Basic info
            if (nameTextView != null) {
                nameTextView.setText(teacher.getName());
            }
            if (ratingBar != null) {
                ratingBar.setRating(teacher.getRank());
            }

            // Contact info
            if (emailTextView != null) {
                emailTextView.setText(teacher.getEmail());
            }

            // Phone
            if (phoneTextView != null) {
                String phone = null;
                try {
                    phone = teacher.getPhone();
                } catch (Exception e) {
                    // Try alternate method name
                    try {
                        phone = teacher.getPhone();
                    } catch (Exception e2) {
                        Log.e(TAG, "Neither getPhoneNumber() nor getPhone() method found", e2);
                    }
                }

                if (phone != null) {
                    phoneTextView.setText(phone);
                }
            }

            // Additional info
            if (bioTextView != null) {
                if (teacher.getBio() != null && !teacher.getBio().isEmpty()) {
                    bioTextView.setText(teacher.getBio());
                } else {
                    bioTextView.setText("No bio available");
                }
            }

            // Set experience
            if (experienceTextView != null) {
                String experience = teacher.getExperience() + " years";
                experienceTextView.setText(experience);
            }

            // Set price
            if (priceTextView != null) {
                String price = "₪" + teacher.getPrice() + "/hour";
                priceTextView.setText(price);
            }

            // Set location
            if (locationTextView != null) {
                locationTextView.setText(teacher.getLocation());
            }

            // Profile image
            if (profileImageView != null && teacher.getProfilePhotoBase64() != null && !teacher.getProfilePhotoBase64().isEmpty()) {
                // Load profile image from Base64 string
                Bitmap bitmap = ImageUtils.convert64base(teacher.getProfilePhotoBase64());
                if (bitmap != null) {
                    profileImageView.setImageBitmap(bitmap);
                }
            }

            Log.d(TAG, "UI updated with teacher data");
        } catch (Exception e) {
            Log.e(TAG, "Error updating UI with teacher data: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Error updating profile", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Contact the teacher (e.g., send a message)
     */
    private void contactTeacher() {
        if (teacher == null) return;

        // Implement contact functionality
        Toast.makeText(getContext(), "Contacting " + teacher.getName(), Toast.LENGTH_SHORT).show();
        // You might launch a messaging activity or dialog here
    }

    /**
     * Book a lesson with the teacher
     */
    private void bookLesson() {
        if (teacher == null) return;

        // Implement lesson booking
        Toast.makeText(getContext(), "Booking lesson with " + teacher.getName(), Toast.LENGTH_SHORT).show();
        // You might launch a booking activity or dialog here
    }

    // FirebaseCallback implementations

    @Override
    public void oncallbackArryStudent(ArrayList<StudentUser> students) {
        // Not used in this fragment
    }

    @Override
    public void oncallbackStudent(StudentUser user) {
        // Not used in this fragment
    }

    @Override
    public void onCallbackTeacher(ArrayList<TeacherUser> teachers) {
        if (teachers != null && !teachers.isEmpty()) {
            teacher = teachers.get(0);
            updateUI(teacher);
        } else {
            Toast.makeText(getContext(), "Teacher not found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCallbackSingleTeacher(TeacherUser teacherUser) {
        if (teacherUser != null) {
            this.teacher = teacherUser;
            updateUI(teacherUser);
        } else {
            Toast.makeText(getContext(), "Teacher not found", Toast.LENGTH_SHORT).show();
        }
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
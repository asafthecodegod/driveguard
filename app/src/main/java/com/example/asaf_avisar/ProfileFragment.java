package com.example.asaf_avisar;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asaf_avisar.activitys.Post;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * The type Profile fragment - Displays a user's profile information.
 */
public class ProfileFragment extends Fragment implements FirebaseCallback, FirebaseCallbackPosts {

    //==========================================================================================
    // DISPLAY LAYER - UI Components and Display Methods
    //==========================================================================================

    // Profile UI components
    private ImageView profileImage;
    private TextView username, tvBio, tvDriverType, textFollowersCount, textFollowingCount;
    private TextView tvGreenForm, tvTheory, tvLicenseDateDetail;
    private Button btnUnfollow, btnNudge;

    // Progress UI components
    private ProgressBar nightProgressBar, dayProgressBar;
    private TextView nightCounter, dayCounter;

    // Posts UI component
    private RecyclerView rvPosts;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_proflie, container, false);

        // Initialize data components
        initializeDataComponents();

        // Initialize UI components
        initializeUIComponents(view);
        setupEventListeners();

        // Load data
        loadUserData();

        return view;
    }

    /**
     * Initialize all UI components by binding views from the layout
     */
    private void initializeUIComponents(View view) {
        // Bind profile views
        profileImage = view.findViewById(R.id.profile_image);
        username = view.findViewById(R.id.username);
        tvBio = view.findViewById(R.id.textView2);
        tvDriverType = view.findViewById(R.id.user_info_1);
        textFollowersCount = view.findViewById(R.id.text_followers_count);
        textFollowingCount = view.findViewById(R.id.text_following_count);
        tvGreenForm = view.findViewById(R.id.detail_green_form);
        tvTheory = view.findViewById(R.id.detail_theory);
        tvLicenseDateDetail = view.findViewById(R.id.detail_license_date);

        // Bind action buttons
        btnUnfollow = view.findViewById(R.id.btn_unfollow);
        btnNudge = view.findViewById(R.id.btn_nudge);

        // Bind progress views
        nightProgressBar = view.findViewById(R.id.night_progress_bar);
        dayProgressBar = view.findViewById(R.id.day_progress_bar);
        nightCounter = view.findViewById(R.id.night_progress_text);
        dayCounter = view.findViewById(R.id.day_progress_text);

        // Bind and setup RecyclerView
        rvPosts = view.findViewById(R.id.recyclerViewPosts);
        rvPosts.setLayoutManager(new GridLayoutManager(getContext(), 3));
        adapter = new PostsAdapter(posts);
        rvPosts.setAdapter(adapter);
    }

    /**
     * Set up event listeners for interactive UI elements
     */
    private void setupEventListeners() {
        btnUnfollow.setOnClickListener(v -> handleFollowButtonClick());
        btnNudge.setOnClickListener(v -> handleNudgeButtonClick());
    }

    /**
     * Handle unfollow/follow button click
     */
    private void handleFollowButtonClick() {
        Toast.makeText(getContext(), "Follow/Unfollow clicked", Toast.LENGTH_SHORT).show();
        // Logic for follow/unfollow would go here
    }

    /**
     * Handle nudge button click
     */
    private void handleNudgeButtonClick() {
        Toast.makeText(getContext(), "Nudge clicked", Toast.LENGTH_SHORT).show();
        // Logic for nudge would go here
    }

    /**
     * Update UI with user profile data
     */
    private void updateProfileUI(StudentUser user) {
        if (user == null) return;

        // Display profile image
        displayProfileImage(user.getProfilePhotoBase64());

        // Set basic profile information
        username.setText(user.getName());
        tvBio.setText(user.getBio() == null ? "" : user.getBio());
        tvDriverType.setText("Driver Type: " + (user.isDriverType() ? "manual" : "automatic"));

        // Set statistics counters
        textFollowersCount.setText(String.valueOf(user.getFollowerCount()));
        textFollowingCount.setText(String.valueOf(user.getFollowingCount()));

        // Set license details
        tvGreenForm.setText("Green Form: " + (user.isHasGreenForm() ? "Submitted" : "Not Submitted"));
        tvTheory.setText("Theory: " + (user.isPassedTheory() ? "Completed" : "Not Completed"));

        // Format and set license date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String licenseDate = user.getLicenseDate() != null
                ? dateFormat.format(user.getLicenseDate())
                : "On their way!";
        tvLicenseDateDetail.setText("License Release Date: " + licenseDate);

        // Update progress indicators
        updateProgressDisplay(user.getLicenseDate());
    }

    /**
     * Display profile image from base64 string
     */
    private void displayProfileImage(String base64Image) {
        if (base64Image != null) {
            Bitmap bitmap = ImageUtils.convert64base(base64Image);
            if (bitmap != null) {
                profileImage.setImageBitmap(bitmap);
            }
        }
    }

    /**
     * Update progress UI elements
     */
    private void updateProgressDisplay(Date licenseDate) {
        // Calculate days left using business logic
        int[] daysLeft = calculateDaysLeft(licenseDate);
        if (daysLeft == null) return;

        int dayLeft = daysLeft[0];
        int nightLeft = daysLeft[1];

        // Update UI with calculated values
        dayCounter.setText(String.valueOf(dayLeft));
        nightCounter.setText(String.valueOf(nightLeft));

        // Calculate and set progress bar values
        dayProgressBar.setProgress((dayLeft * 100) / 90);
        nightProgressBar.setProgress((nightLeft * 100) / 180);
    }

    /**
     * Update posts display with new data
     */
    private void updatePostsDisplay(ArrayList<Post> newPosts) {
        posts.clear();
        posts.addAll(newPosts);
        adapter.notifyDataSetChanged();
    }

    //==========================================================================================
    // LOGIC LAYER - Business Logic and Data Management
    //==========================================================================================

    private String userId;
    private List<Post> posts = new ArrayList<>();
    private PostsAdapter adapter;
    private FireBaseManager fbm;
    private long diffInDays;
    private int progress;

    /**
     * Initialize data-related components
     */
    private void initializeDataComponents() {
        fbm = new FireBaseManager(requireContext());

        // Get userId from arguments or fallback to current user
        userId = determineUserId();
    }

    /**
     * Determine which user ID to display
     */
    private String determineUserId() {
        String id = null;
        if (getArguments() != null) {
            id = getArguments().getString("userId");
        }
        if (id == null) {
            // Fallback to current user
            id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        return id;
    }

    /**
     * Load user data and posts from Firebase
     */
    private void loadUserData() {
        fbm.readData(this, "Student", userId);
        fbm.readPostsForUser(this, userId);
    }

    /**
     * Calculate days left for day and night escort periods
     * @return int array with [dayLeft, nightLeft] or null if license date is null
     */
    private int[] calculateDaysLeft(Date licenseDate) {
        if (licenseDate == null) return null;

        // Calculate days since license was issued
        long diff = Calendar.getInstance().getTimeInMillis() - licenseDate.getTime();
        diffInDays = TimeUnit.MILLISECONDS.toDays(diff);
        progress = (int) diffInDays;

        // Calculate days left for each period
        int dayLeft = (int) Math.max(0, 90 - diffInDays);
        int nightLeft = (int) Math.max(0, 180 - diffInDays);

        return new int[] {dayLeft, nightLeft};
    }

    //==========================================================================================
    // CALLBACK IMPLEMENTATIONS - Firebase Data Handling
    //==========================================================================================

    @Override
    public void oncallbackStudent(StudentUser user) {
        updateProfileUI(user);
    }

    @Override
    public void oncallbackArryStudent(ArrayList<StudentUser> students) {
        // Not used in this fragment
    }

    @Override
    public void onCallbackTeacher(ArrayList<TeacherUser> teachers) {
        // Not used in this fragment
    }

    @Override
    public void onCallbackPosts(ArrayList<Post> postsList) {
        updatePostsDisplay(postsList);
    }

    //==========================================================================================
    // INNER ADAPTER CLASS - For displaying posts grid
    //==========================================================================================

    private static class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.VH> {
        private final List<Post> items;

        /**
         * Instantiates a new Posts adapter.
         *
         * @param items the items
         */
        PostsAdapter(List<Post> items) {
            this.items = items;
        }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Create an ImageView for each grid item
            ImageView iv = new ImageView(parent.getContext());
            int w = parent.getResources().getDisplayMetrics().widthPixels / 3;
            iv.setLayoutParams(new RecyclerView.LayoutParams(w, w));
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return new VH(iv);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            // Convert base64 content to bitmap and display
            String base64 = items.get(position).getContent();
            if (base64 != null) {
                Bitmap bitmap = ImageUtils.convert64base(base64);
                if (bitmap != null) holder.iv.setImageBitmap(bitmap);
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        /**
         * The ViewHolder for post items
         */
        static class VH extends RecyclerView.ViewHolder {
            /**
             * The ImageView for post content
             */
            ImageView iv;

            /**
             * Instantiates a new ViewHolder
             *
             * @param iv the image view
             */
            VH(ImageView iv) {
                super(iv);
                this.iv = iv;
            }
        }
    }
}
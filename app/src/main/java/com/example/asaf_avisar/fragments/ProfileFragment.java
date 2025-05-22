package com.example.asaf_avisar.fragments;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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

import com.example.asaf_avisar.FireBaseManager;
import com.example.asaf_avisar.callbacks.FirebaseCallback;
import com.example.asaf_avisar.callbacks.FirebaseCallbackPosts;
import com.example.asaf_avisar.R;
import com.example.asaf_avisar.objects.StudentUser;
import com.example.asaf_avisar.objects.TeacherUser;
import com.example.asaf_avisar.objects.ImageUtils;
import com.example.asaf_avisar.objects.Post;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
    private Button btnFollow, btnNudge; // Changed btnUnfollow to btnFollow

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
        btnFollow = view.findViewById(R.id.btn_unfollow); // Using same ID but renamed variable
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
        btnFollow.setOnClickListener(v -> handleFollowButtonClick());
        btnNudge.setOnClickListener(v -> handleNudgeButtonClick());
    }

    /**
     * Handle follow/unfollow button click
     */
    private void handleFollowButtonClick() {
        // Add comprehensive null checks
        if (profileUser == null) {
            Toast.makeText(getContext(), "Profile not loaded yet, please wait...", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (currentUserId == null) {
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        if (profileUser.getId() == null) {
            Toast.makeText(getContext(), "Profile ID not available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Initialize following/followers maps if they're null
        if (currentUser != null && currentUser.getFollowing() == null) {
            currentUser.setFollowing(new HashMap<>());
        }
        if (profileUser.getFollowers() == null) {
            profileUser.setFollowers(new HashMap<>());
        }

        // Check if currently following
        boolean isCurrentlyFollowing = profileUser.isFollowedBy(currentUserId);

        if (isCurrentlyFollowing) {
            // Unfollow logic
            handleUnfollow(currentUserId);
        } else {
            // Follow logic
            handleFollow(currentUserId);
        }
    }

    /**
     * Handle follow action
     */
    private void handleFollow(String currentUserId) {
        try {
            // Update local data immediately for instant UI feedback
            if (profileUser != null) {
                profileUser.addFollower(currentUserId);
            }
            
            if (currentUser != null) {
                currentUser.addFollowing(profileUser.getId());
            }

            // Update UI immediately
            updateFollowButton(true);
            updateFollowerCount(profileUser != null ? profileUser.getFollowerCount() : 0);

            // Update Firebase
            fbm.updateUserFollowing(currentUserId, profileUser.getId(), true);
            fbm.updateUserFollowers(profileUser.getId(), currentUserId, true);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error while following user", Toast.LENGTH_SHORT).show();
            Log.e("ProfileFragment", "Error in handleFollow: " + e.getMessage());
        }
    }

    /**
     * Handle unfollow action
     */
    private void handleUnfollow(String currentUserId) {
        try {
            // Update local data immediately for instant UI feedback
            if (profileUser != null) {
                profileUser.removeFollower(currentUserId);
            }
            
            if (currentUser != null) {
                currentUser.removeFollowing(profileUser.getId());
            }

            // Update UI immediately
            updateFollowButton(false);
            updateFollowerCount(profileUser != null ? profileUser.getFollowerCount() : 0);

            // Update Firebase
            fbm.updateUserFollowing(currentUserId, profileUser.getId(), false);
            fbm.updateUserFollowers(profileUser.getId(), currentUserId, false);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error while unfollowing user", Toast.LENGTH_SHORT).show();
            Log.e("ProfileFragment", "Error in handleUnfollow: " + e.getMessage());
        }
    }

    /**
     * Update follow button appearance based on follow status
     */
    private void updateFollowButton(boolean isFollowing) {
        if (btnFollow != null) {
            btnFollow.setText(isFollowing ? "Unfollow" : "Follow");
            btnFollow.setBackgroundColor(isFollowing ? Color.GRAY : Color.BLUE);
            btnFollow.setTextColor(Color.WHITE);
        }
    }

    /**
     * Update follower count display
     */
    private void updateFollowerCount(int count) {
        if (textFollowersCount != null) {
            textFollowersCount.setText(String.valueOf(count));
        }
    }

    /**
     * Handle nudge button click
     */
    private void handleNudgeButtonClick() {
        Toast.makeText(getContext(), "Nudge clicked",Toast.LENGTH_SHORT).show();
        // Logic for nudge would go here
    }

    /**
     * Update UI with user profile data
     */
    private void updateProfileUI(StudentUser user) {
        if (user == null) return;

        profileUser = user; // Store reference

        // Make sure the user has an ID (set it if missing)
        if (profileUser.getId() == null) {
            profileUser.setId(userId);
        }

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

        // Setup follow button if viewing another user's profile
        setupFollowButton();
    }

    /**
     * Setup follow button based on current follow status
     */
    private void setupFollowButton() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Hide follow button if viewing own profile
        if (userId.equals(currentUserId)) {
            btnFollow.setVisibility(View.GONE);
            return;
        }

        btnFollow.setVisibility(View.VISIBLE);

        // Check if currently following this user
        boolean isFollowing = profileUser != null && profileUser.isFollowedBy(currentUserId);
        updateFollowButton(isFollowing);
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
        for (Post post : newPosts) {
            if (post.getType() == 1) { // Only photo posts
                posts.add(post);
            }
        }
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

    // NEW: Store references to current user and profile user
    private StudentUser currentUser;
    private StudentUser profileUser;

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
        // Load the profile user data
        fbm.readData(this, "Student", userId);
        fbm.readPostsForUser(this, userId);

        // Also load current user data if viewing another user's profile
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (!userId.equals(currentUserId)) {
            fbm.readData(new FirebaseCallback() {
                @Override
                public void oncallbackStudent(StudentUser user) {
                    currentUser = user;
                    // Refresh follow button setup after getting current user data
                    setupFollowButton();
                }

                @Override
                public void oncallbackArryStudent(ArrayList<StudentUser> students) {}

                @Override
                public void onCallbackTeacher(ArrayList<TeacherUser> teachers) {}

                @Override
                public void onCallbackSingleTeacher(TeacherUser teacher) {}
            }, "Student", currentUserId);
        }
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
    public void onCallbackSingleTeacher(TeacherUser teacher) {

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
        private static final int GRID_SPAN_COUNT = 3;
        private static final int IMAGE_PADDING = 2; // dp

        PostsAdapter(List<Post> items) {
            this.items = items;
        }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Create a container view for proper padding
            View container = new View(parent.getContext());
            
            // Calculate image size based on screen width and padding
            int screenWidth = parent.getResources().getDisplayMetrics().widthPixels;
            int paddingPx = (int) (IMAGE_PADDING * parent.getResources().getDisplayMetrics().density);
            int imageSize = (screenWidth - (paddingPx * (GRID_SPAN_COUNT + 1))) / GRID_SPAN_COUNT;
            
            // Create ImageView with proper layout params
            ImageView iv = new ImageView(parent.getContext());
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(imageSize, imageSize);
            params.setMargins(paddingPx, paddingPx, paddingPx, paddingPx);
            iv.setLayoutParams(params);
            
            // Configure ImageView
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv.setAdjustViewBounds(true);
            
            return new VH(iv);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            // Show loading state
            holder.iv.setImageResource(R.drawable.loading_post);
            
            // Load image in background
            new Thread(() -> {
                String base64 = items.get(position).getContent();
                if (base64 != null) {
                    Bitmap bitmap = ImageUtils.convert64base(base64);
                    if (bitmap != null) {
                        // Post back to main thread for UI update
                        holder.iv.post(() -> {
                            holder.iv.setImageBitmap(bitmap);
                            // Optional: Add fade-in animation
                            holder.iv.setAlpha(0f);
                            holder.iv.animate().alpha(1f).setDuration(200).start();
                        });
                    }
                }
            }).start();
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class VH extends RecyclerView.ViewHolder {
            ImageView iv;

            VH(ImageView iv) {
                super(iv);
                this.iv = iv;
            }
        }
    }
}
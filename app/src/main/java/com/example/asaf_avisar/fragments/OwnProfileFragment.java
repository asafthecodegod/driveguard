package com.example.asaf_avisar.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import java.util.List;
import java.util.Locale;

/**
 * The type Own profile fragment - Shows the current user's profile information and posts.
 */
public class OwnProfileFragment extends Fragment implements FirebaseCallback, FirebaseCallbackPosts {

    //==========================================================================================
    // DISPLAY LAYER - UI Components and Display Methods
    //==========================================================================================

    // header views
    private ImageView profileImage_1;
    private TextView username_1, textView2_1, user_info_1_1;
    private TextView followers_count_1, following_count_1, lessons_count_1;

    // additional detail views
    private TextView detail_green_form_1, detail_theory_1, detail_license_date_1;

    // posts RecyclerView
    private RecyclerView recyclerViewPosts_1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_own_proflie, container, false);

        // Initialize UI components and data
        initializeUIComponents(view);
        initializeData();

        return view;
    }

    /**
     * Initialize all UI components by binding views from the layout
     */
    private void initializeUIComponents(View view) {
        // Bind header views
        profileImage_1 = view.findViewById(R.id.profile_image_1);
        username_1 = view.findViewById(R.id.username_1);
        textView2_1 = view.findViewById(R.id.textView2_1);
        user_info_1_1 = view.findViewById(R.id.user_info_1_1);
        followers_count_1 = view.findViewById(R.id.followers_count_1);
        following_count_1 = view.findViewById(R.id.following_count_1);
        lessons_count_1 = view.findViewById(R.id.lessons_count_1);

        // Bind detail views
        detail_green_form_1 = view.findViewById(R.id.detail_green_form_1);
        detail_theory_1 = view.findViewById(R.id.detail_theory_1);
        detail_license_date_1 = view.findViewById(R.id.detail_license_date_1);

        // Setup RecyclerView
        recyclerViewPosts_1 = view.findViewById(R.id.recyclerViewPosts_1);
        recyclerViewPosts_1.setLayoutManager(new GridLayoutManager(getContext(), 3));
        adapter = new PostsAdapter(posts);
        recyclerViewPosts_1.setAdapter(adapter);
    }

    /**
     * Update UI with user profile data
     */
    private void updateUIWithUserData(StudentUser user) {
        if (user == null) return;

        // Display profile image
        displayProfileImage(user.getProfilePhotoBase64());

        // Set basic profile info
        username_1.setText(user.getName());
        textView2_1.setText(user.getBio() != null ? user.getBio() : "");
        user_info_1_1.setText("Driver Type: " + (user.isDriverType() ? "manual" : "automatic"));

        // Set count statistics
        followers_count_1.setText(String.valueOf(user.getFollowerCount()));
        following_count_1.setText(String.valueOf(user.getFollowingCount()));
        lessons_count_1.setText(String.valueOf(user.getLessonCount()));

        // Set additional details
        detail_green_form_1.setText("Green Form: " + (user.isHasGreenForm() ? "Submitted" : "Not Submitted"));
        detail_theory_1.setText("Theory: " + (user.isPassedTheory() ? "Completed" : "Not Completed"));

        // Set license date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String licenseDate = user.getLicenseDate() != null
                ? dateFormat.format(user.getLicenseDate())
                : "On their way!";
        detail_license_date_1.setText("License Release Date: " + licenseDate);
    }

    /**
     * Display profile image from base64 string
     */
    private void displayProfileImage(String base64Image) {
        if (base64Image != null) {
            Bitmap bitmap = ImageUtils.convert64base(base64Image);
            if (bitmap != null) {
                profileImage_1.setImageBitmap(bitmap);
            }
        }
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

    /**
     * Initialize data sources and load data
     */
    private void initializeData() {
        // Initialize Firebase manager and user ID
        fbm = new FireBaseManager(requireContext());
        userId = getCurrentUserId();

        // Load user profile data and posts
        loadUserData();
        loadUserPosts();
    }

    /**
     * Get current user ID from Firebase
     */
    private String getCurrentUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    /**
     * Load user profile data from Firebase
     */
    private void loadUserData() {
        fbm.readData(this, "Student", userId);
    }

    /**
     * Load user posts from Firebase
     */
    private void loadUserPosts() {
        fbm.readPostsForUser(this, userId);
    }

    //==========================================================================================
    // CALLBACK IMPLEMENTATIONS - Firebase Data Handling
    //==========================================================================================

    // FirebaseCallback for StudentUser
    @Override
    public void oncallbackStudent(StudentUser user) {
        updateUIWithUserData(user);
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

    // FirebaseCallbackPosts
    @Override
    public void onCallbackPosts(ArrayList<Post> postsList) {
        updatePostsDisplay(postsList);
    }

    //==========================================================================================
    // INNER ADAPTER CLASS - For displaying posts grid
    //==========================================================================================

    /**
     * Adapter for displaying user posts in a grid layout
     */
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
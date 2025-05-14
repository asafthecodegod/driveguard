package com.example.asaf_avisar;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.asaf_avisar.activitys.Post;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Bottom sheet dialog for displaying and adding comments to posts.
 */
public class CommentsBottomSheet extends BottomSheetDialogFragment implements FirebaseCallback {

    //==========================================================================================
    // DISPLAY LAYER - UI Components and Display Methods
    //==========================================================================================

    // UI components
    private RecyclerView commentsRecyclerView;
    private CommentsAdapter commentsAdapter;
    private EditText commentInput;
    private ImageButton sendButton;

    // Constants for arguments
    private static final String ARG_POST = "arg_post";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate and initialize the UI
        View view = inflater.inflate(R.layout.comments_bottom_sheet, container, false);

        // Initialize UI components
        initializeUIComponents(view);

        // Set up the RecyclerView
        setupCommentsRecyclerView();

        // Set up event listeners
        setupEventListeners();

        return view;
    }

    /**
     * Initialize all UI components
     */
    private void initializeUIComponents(View view) {
        commentsRecyclerView = view.findViewById(R.id.comments_recyclerview);
        commentInput = view.findViewById(R.id.comment_input);
        sendButton = view.findViewById(R.id.send_comment_button);
    }

    /**
     * Set up the comments RecyclerView with adapter
     */
    private void setupCommentsRecyclerView() {
        commentsAdapter = new CommentsAdapter(commentList, getContext());
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        commentsRecyclerView.setAdapter(commentsAdapter);
    }

    /**
     * Set up event listeners for UI interactions
     */
    private void setupEventListeners() {
        // Handle send button click to add new comment
        sendButton.setOnClickListener(v -> handleCommentSubmission());
    }

    /**
     * Clear the comment input field
     */
    private void clearCommentInput() {
        commentInput.setText("");
    }

    /**
     * Notify the adapter about a new comment
     */
    private void notifyCommentAdded() {
        commentsAdapter.notifyItemInserted(commentList.size() - 1);
    }

    //==========================================================================================
    // LOGIC LAYER - Business Logic and Data Management
    //==========================================================================================

    // Data objects
    private Post post;
    private List<Comment> commentList;
    private FireBaseManager fireBaseManager;
    private String username;
    private String profilePictureUrl;

    /**
     * Create a new instance of the comments bottom sheet for a specific post
     */
    public static CommentsBottomSheet newInstance(Post post) {
        CommentsBottomSheet fragment = new CommentsBottomSheet();
        Bundle args = new Bundle();
        args.putSerializable(ARG_POST, post);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize data from arguments
        loadPostFromArguments();

        // Initialize comment list
        initializeCommentList();

        // Initialize Firebase manager and load user data
        initializeFirebaseAndLoadUserData();
    }

    /**
     * Load post data from fragment arguments
     */
    private void loadPostFromArguments() {
        if (getArguments() != null) {
            post = (Post) getArguments().getSerializable(ARG_POST);
        }
    }

    /**
     * Initialize the comment list from post data
     */
    private void initializeCommentList() {
        commentList = post != null && post.getComments() != null ?
                post.getComments() : new ArrayList<>();
    }

    /**
     * Initialize Firebase manager and load user data
     */
    private void initializeFirebaseAndLoadUserData() {
        fireBaseManager = new FireBaseManager(getContext());
        fireBaseManager.readData(this, "Student", fireBaseManager.getUserid());
    }

    /**
     * Handle the submission of a new comment
     */
    private void handleCommentSubmission() {
        String commentText = commentInput.getText().toString().trim();

        if (validateCommentText(commentText)) {
            // Create and add the new comment
            Comment newComment = createNewComment(commentText);
            addCommentToList(newComment);

            // Update UI
            clearCommentInput();
            notifyCommentAdded();

            // Update in Firebase
            updateCommentsInFirebase();
        }
    }

    /**
     * Validate the comment text is not empty
     */
    private boolean validateCommentText(String text) {
        return !TextUtils.isEmpty(text);
    }

    /**
     * Create a new comment object with current user data
     */
    private Comment createNewComment(String text) {
        String currentUserId = FireBaseManager.getmAuth().getCurrentUser().getUid();
        return new Comment(
                post.getKey(),
                currentUserId,
                username,
                text,
                new Date(),
                profilePictureUrl
        );
    }

    /**
     * Add a comment to the comments list
     */
    private void addCommentToList(Comment comment) {
        commentList.add(comment);
    }

    /**
     * Update the comments in Firebase
     */
    private void updateCommentsInFirebase() {
        FireBaseManager fbManager = new FireBaseManager(getContext());
        fbManager.updatePostComments(post, commentList);
    }

    //==========================================================================================
    // CALLBACK IMPLEMENTATIONS - Firebase Data Handling
    //==========================================================================================

    @Override
    public void oncallbackStudent(StudentUser student) {
        if (student != null) {
            // Store user data for creating comments
            username = student.getName();
            profilePictureUrl = student.getProfilePhotoBase64();
        }
    }

    @Override
    public void oncallbackArryStudent(ArrayList<StudentUser> students) {
        // Not used in this fragment
    }

    @Override
    public void onCallbackTeacher(ArrayList<TeacherUser> teachers) {
        // Not used in this fragment
    }
}
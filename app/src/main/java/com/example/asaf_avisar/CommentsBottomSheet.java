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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The type Comments bottom sheet.
 */
public class CommentsBottomSheet extends BottomSheetDialogFragment implements FirebaseCallback {

    private static final String ARG_POST = "arg_post";
    private Post post;
    private RecyclerView commentsRecyclerView;
    private CommentsAdapter commentsAdapter;
    private List<Comment> commentList;
    private EditText commentInput;
    private ImageButton sendButton;
    private FireBaseManager fireBaseManager;
    private String username;
    private String profilePictureUrl;

    /**
     * New instance comments bottom sheet.
     *
     * @param post the post
     * @return the comments bottom sheet
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
        if (getArguments() != null) {
            post = (Post) getArguments().getSerializable(ARG_POST);
        }
        // Initialize comment list; you might want to load these from Firebase.
        commentList = post.getComments() != null ? post.getComments() : new ArrayList<>();

        // Initialize FireBaseManager before using it
        fireBaseManager = new FireBaseManager(getContext()); // Make sure it's initialized here

        // Now you can call methods on fireBaseManager
        fireBaseManager.readData(this,"Student", fireBaseManager.getUserid());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.comments_bottom_sheet, container, false);
        commentsRecyclerView = view.findViewById(R.id.comments_recyclerview);
        commentInput = view.findViewById(R.id.comment_input);
        sendButton = view.findViewById(R.id.send_comment_button);

        commentsAdapter = new CommentsAdapter(commentList, getContext());
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        commentsRecyclerView.setAdapter(commentsAdapter);

        // Handle send button click to add new comment
        sendButton.setOnClickListener(v -> {
            String text = commentInput.getText().toString().trim();
            if (!TextUtils.isEmpty(text)) {
                // Create a new comment; use FirebaseAuth to get current user info.
                String currentUserId = FireBaseManager.getmAuth().getCurrentUser().getUid();
                // Use profilePictureUrl for new comment
                Comment newComment = new Comment(post.getKey(), currentUserId, username, text, new Date(), profilePictureUrl);
                commentList.add(newComment);
                commentsAdapter.notifyItemInserted(commentList.size() - 1);
                commentInput.setText("");

                // Update the post's comments in Firebase (implement updatePostComments in FireBaseManager)
                FireBaseManager fbManager = new FireBaseManager(getContext());
                fbManager.updatePostComments(post, commentList);
            }
        });

        return view;
    }

    @Override
    public void oncallbackArryStudent(ArrayList<StudentUser> students) {
        // You can retrieve the profile picture URL here if needed
    }

    @Override
    public void oncallbackStudent(StudentUser student) {
        username = student.getName();
        profilePictureUrl = student.getProfilePhotoBase64(); // Assuming StudentUser has a getProfilePictureUrl method
    }

    @Override
    public void onCallbackTeacher(ArrayList<TeacherUser> teachers) {
        // Not used here
    }
}

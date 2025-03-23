package com.example.asaf_avisar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {

    private List<Comment> commentList;
    private Context context;

    public CommentsAdapter(List<Comment> commentList, Context context) {
        this.commentList = commentList;
        this.context = context;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.userName.setText(comment.getUserName());
        holder.date.setText(new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(comment.getDate()));
        holder.content.setText(comment.getContent());
        holder.likeCount.setText(String.valueOf(comment.getLikesCount()));

        // Decode and set profile picture (Base64 string to Bitmap)
        Bitmap userPfpBitmap = decodeBase64(comment.getProfilePictureUrl());
        if (userPfpBitmap != null) {
            holder.profilePicture.setImageBitmap(userPfpBitmap);
        }

        // Like Button Click
        holder.likeButton.setOnClickListener(v -> {
            String currentUserId = FireBaseManager.getmAuth().getCurrentUser().getUid();

            // ✅ Prevent duplicate likes
            if (comment.getLikedByUsers().contains(currentUserId)) {
                comment.setLikesCount(comment.getLikesCount() - 1);
                comment.getLikedByUsers().remove(currentUserId);
                holder.likeCount.setText(String.valueOf(comment.getLikesCount()));
                return; // Stop execution if the user already liked the comment
            }

            // Increment like count
            comment.setLikesCount(comment.getLikesCount() + 1);
            comment.getLikedByUsers().add(currentUserId); // Add user to liked list

            // Update UI
            holder.likeCount.setText(String.valueOf(comment.getLikesCount()));
            animateHeart(holder.likeButton);

            // Save changes to Firebase
            FireBaseManager fbManager = new FireBaseManager(v.getContext());
            fbManager.updateCommentLikes(comment, comment.getKey());
        });

        // Reply Button Click
        holder.replyButton.setOnClickListener(v -> {
            if (holder.repliesRecyclerView.getVisibility() == View.GONE) {
                holder.repliesRecyclerView.setVisibility(View.VISIBLE); // Show replies
                holder.repliesRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                CommentsAdapter repliesAdapter = new CommentsAdapter(comment.getReplies(), context);
                holder.repliesRecyclerView.setAdapter(repliesAdapter);
            } else {
                holder.repliesRecyclerView.setVisibility(View.GONE); // Hide replies
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView userName, date, content, likeCount, replyButton;
        ImageView likeButton, profilePicture;  // Added ImageView for profile picture
        RecyclerView repliesRecyclerView;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.comment_user_name);
            date = itemView.findViewById(R.id.comment_date);
            content = itemView.findViewById(R.id.comment_content);
            likeCount = itemView.findViewById(R.id.comment_like_count);
            replyButton = itemView.findViewById(R.id.comment_reply_button);
            likeButton = itemView.findViewById(R.id.comment_like_button);
            profilePicture = itemView.findViewById(R.id.user_pfp); // ImageView for profile picture
            repliesRecyclerView = itemView.findViewById(R.id.replies_recyclerview);
        }
    }

    private void animateHeart(View view) {
        view.animate()
                .scaleX(1.5f)
                .scaleY(1.5f)
                .setDuration(150)
                .withEndAction(() -> view.animate().scaleX(1f).scaleY(1f).setDuration(150).start())
                .start();
    }

    // Helper method: decode a Base64-encoded string into a Bitmap.
    private Bitmap decodeBase64(String base64Image) {
        try {
            if (base64Image == null || base64Image.isEmpty()) {
                return null;
            }
            byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

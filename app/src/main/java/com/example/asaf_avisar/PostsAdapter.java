package com.example.asaf_avisar;

import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.example.asaf_avisar.activitys.Post;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Post> posts;
    private static final int TYPE_NOTE = 0;
    private static final int TYPE_PHOTO = 1;
    private static final int TYPE_STATUS = 3;

    public PostsAdapter(List<Post> posts) {
        this.posts = (posts != null) ? posts : new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        Post post = posts.get(position);
        int type = post.getType();
        if (type == TYPE_PHOTO) {
            return TYPE_PHOTO;
        } else if (type == TYPE_STATUS) {
            return TYPE_STATUS;
        } else {
            return TYPE_NOTE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_PHOTO) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_post, parent, false);
            return new PhotoViewHolder(view);
        } else if (viewType == TYPE_NOTE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_post, parent, false);
            return new NoteViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.status_post, parent, false);
            return new StatusViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Post post = posts.get(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        String formattedDate = post.getDate() != null ? dateFormat.format(post.getDate()) : "Unknown Date";

        Bitmap userPfpBitmap = ImageUtils.convert64base(post.getUserPfp());

        if (holder instanceof PhotoViewHolder) {
            PhotoViewHolder photoHolder = (PhotoViewHolder) holder;
            if (userPfpBitmap != null) {
                photoHolder.userPfp.setImageBitmap(userPfpBitmap);
            }
            photoHolder.userName.setText(post.getUserName());
            photoHolder.photoDescription.setText(post.getDescription());


            // Decode the post content (Base64 string) into Bitmap and display it
            String base64Content = post.getContent();  // Ensure this is the Base64 string for the image
            if (base64Content != null && !base64Content.isEmpty()) {
                Bitmap postImageBitmap = ImageUtils.convert64base(base64Content); // Decode to Bitmap
                if (postImageBitmap != null) {
                    photoHolder.postContent.setImageBitmap(postImageBitmap); // Set the decoded Bitmap to the ImageView
                } else {
                    Log.e("Image Error", "Failed to decode the image from Base64.");
                }
            } else {
                Log.e("Image Error", "Base64 content is null or empty.");
            }


            photoHolder.likeCount.setText(String.valueOf(post.getLikesCount()));

            photoHolder.likeButton.setOnClickListener(v -> handleLikeButtonClick(post, photoHolder.likeCount, photoHolder.likeButton));
            photoHolder.commentButton.setOnClickListener(v -> openCommentsBottomSheet(v, post));

        } else if (holder instanceof NoteViewHolder) {
            NoteViewHolder noteHolder = (NoteViewHolder) holder;

            if (userPfpBitmap != null) {
                noteHolder.userPfp.setImageBitmap(userPfpBitmap);
            }
            noteHolder.userName.setText(post.getUserName());
            noteHolder.postTitle.setText(post.getDescription());
            noteHolder.NoteContent.setText(post.getContent());
            noteHolder.postDate.setText(formattedDate);
            noteHolder.likeCount.setText(String.valueOf(post.getLikesCount()));

            noteHolder.likeButton.setOnClickListener(v -> handleLikeButtonClick(post, noteHolder.likeCount, noteHolder.likeButton));
            noteHolder.commentButton.setOnClickListener(v -> openCommentsBottomSheet(v, post));
        } else if (holder instanceof StatusViewHolder) {
            StatusViewHolder statusHolder = (StatusViewHolder) holder;
            if (userPfpBitmap != null) {
                statusHolder.userPfp.setImageBitmap(userPfpBitmap);
            }
            statusHolder.userName.setText(post.getUserName());
            statusHolder.postTitle.setText(post.getDescription());
            statusHolder.postDate.setText(formattedDate);
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    private void handleLikeButtonClick(Post post, TextView likeCountView, View likeButton) {
        String currentUserId = FireBaseManager.getmAuth().getCurrentUser().getUid();
        if (post.getLikedByUsers().contains(currentUserId)) {
            post.setLikesCount(post.getLikesCount() - 1);
            post.getLikedByUsers().remove(currentUserId);
        } else {
            post.setLikesCount(post.getLikesCount() + 1);
            post.getLikedByUsers().add(currentUserId);
            animateHeart(likeButton);
        }
        likeCountView.setText(String.valueOf(post.getLikesCount()));
        new FireBaseManager(likeButton.getContext()).updatePostLikes(post, post.getKey());
    }

    private void openCommentsBottomSheet(View v, Post post) {
        CommentsBottomSheet bottomSheet = CommentsBottomSheet.newInstance(post);
        bottomSheet.show(((AppCompatActivity) v.getContext()).getSupportFragmentManager(), "commentsBottomSheet");
    }

    private void animateHeart(View view) {
        view.animate().scaleX(1.5f).scaleY(1.5f).setDuration(150)
                .withEndAction(() -> view.animate().scaleX(1f).scaleY(1f).setDuration(150).start()).start();
    }

    // ViewHolder for photo posts.
    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView postContent, userPfp, likeButton, commentButton;
        TextView userName, photoDescription, postDate, likeCount;

        PhotoViewHolder(View itemView) {
            super(itemView);
            userPfp = itemView.findViewById(R.id.user_pfp);
            userName = itemView.findViewById(R.id.user_name);
            postContent = itemView.findViewById(R.id.photo_content);
            photoDescription = itemView.findViewById(R.id.photo_description);
            postDate = itemView.findViewById(R.id.post_date);
            likeButton = itemView.findViewById(R.id.like_button);
            commentButton = itemView.findViewById(R.id.comment_button);
            likeCount = itemView.findViewById(R.id.like_count);
        }
    }

    // ViewHolder for note posts.
    static class NoteViewHolder extends RecyclerView.ViewHolder {
        ImageView userPfp;
        TextView userName, postTitle, NoteContent, postDate, likeCount;
        ImageView likeButton, commentButton;  // Changed from ImageButton to ImageView

        NoteViewHolder(View itemView) {
            super(itemView);
            userPfp = itemView.findViewById(R.id.user_pfp);
            userName = itemView.findViewById(R.id.user_name);
            postTitle = itemView.findViewById(R.id.note_title);
            NoteContent = itemView.findViewById(R.id.note_content);
            postDate = itemView.findViewById(R.id.post_date);
            likeButton = itemView.findViewById(R.id.like_button);
            commentButton = itemView.findViewById(R.id.comment_button);
            likeCount = itemView.findViewById(R.id.like_count);
        }
    }

    // ViewHolder for status posts.
    static class StatusViewHolder extends RecyclerView.ViewHolder {
        ImageView userPfp;
        TextView userName, postTitle, postDate;

        StatusViewHolder(View itemView) {
            super(itemView);
            userPfp = itemView.findViewById(R.id.user_pfp);
            userName = itemView.findViewById(R.id.user_name);
            postDate = itemView.findViewById(R.id.post_date);
        }
    }
}

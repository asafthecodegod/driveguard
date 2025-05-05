package com.example.asaf_avisar;

import android.graphics.Bitmap;
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

/**
 * The type Posts adapter.
 */
public class PostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Post> posts;
    private static final int TYPE_NOTE = 0;
    private static final int TYPE_PHOTO = 1;
    private static final int TYPE_STATUS = 3;

    /**
     * Instantiates a new Posts adapter.
     *
     * @param posts the posts
     */
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
            photoHolder.postContent.setImageResource(R.drawable.loading_post);
            photoHolder.photoDescription.setText(post.getDescription());
            // Decode the post content (Base64 string) into Bitmap and display it
            String base64Content = post.getContent();  // Ensure this is the Base64 string for the image
            Bitmap postImageBitmap = ImageUtils.convert64base(base64Content);
            photoHolder.postContent.setImageBitmap(postImageBitmap); // Set the decoded Bitmap to the ImageView
            //photoHolder.postContent.setImageResource();


            photoHolder.likeCount.setText(String.valueOf(post.getLikesCount()));

            photoHolder.likeButton.setOnClickListener(v -> handleLikeButtonClick(post, photoHolder.likeCount, photoHolder.likeButton));
            photoHolder.commentButton.setOnClickListener(v -> openCommentsBottomSheet(v, post));

        } else if (holder instanceof NoteViewHolder) {
            NoteViewHolder noteHolder = (NoteViewHolder) holder;

            userPfpBitmap = ImageUtils.convert64base(post.getImageUrl());
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

    /**
     * The type Photo view holder.
     */
// ViewHolder for photo posts.
    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        /**
         * The Post content.
         */
        ImageView postContent, /**
         * The User pfp.
         */
        userPfp, /**
         * The Like button.
         */
        likeButton, /**
         * The Comment button.
         */
        commentButton;
        /**
         * The User name.
         */
        TextView userName, /**
         * The Photo description.
         */
        photoDescription, /**
         * The Post date.
         */
        postDate, /**
         * The Like count.
         */
        likeCount;

        /**
         * Instantiates a new Photo view holder.
         *
         * @param itemView the item view
         */
        PhotoViewHolder(View itemView) {
            super(itemView);
            userPfp = itemView.findViewById(R.id.user_pfp);
            userName = itemView.findViewById(R.id.user_name);
            postContent = itemView.findViewById(R.id.image_content);
            photoDescription = itemView.findViewById(R.id.photo_description);
            postDate = itemView.findViewById(R.id.post_date);
            likeButton = itemView.findViewById(R.id.like_button);
            commentButton = itemView.findViewById(R.id.comment_button);
            likeCount = itemView.findViewById(R.id.like_count);
        }
    }

    /**
     * The type Note view holder.
     */
// ViewHolder for note posts.
    static class NoteViewHolder extends RecyclerView.ViewHolder {
        /**
         * The User pfp.
         */
        ImageView userPfp;
        /**
         * The User name.
         */
        TextView userName, /**
         * The Post title.
         */
        postTitle, /**
         * The Note content.
         */
        NoteContent, /**
         * The Post date.
         */
        postDate, /**
         * The Like count.
         */
        likeCount;
        /**
         * The Like button.
         */
        ImageView likeButton, /**
         * The Comment button.
         */
        commentButton;  // Changed from ImageButton to ImageView

        /**
         * Instantiates a new Note view holder.
         *
         * @param itemView the item view
         */
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

    /**
     * The type Status view holder.
     */
// ViewHolder for status posts.
    static class StatusViewHolder extends RecyclerView.ViewHolder {
        /**
         * The User pfp.
         */
        ImageView userPfp;
        /**
         * The User name.
         */
        TextView userName, /**
         * The Post title.
         */
        postTitle, /**
         * The Post date.
         */
        postDate;

        /**
         * Instantiates a new Status view holder.
         *
         * @param itemView the item view
         */
        StatusViewHolder(View itemView) {
            super(itemView);
            userPfp = itemView.findViewById(R.id.user_pfp);
            userName = itemView.findViewById(R.id.user_name);
            postDate = itemView.findViewById(R.id.post_date);
        }
    }
}

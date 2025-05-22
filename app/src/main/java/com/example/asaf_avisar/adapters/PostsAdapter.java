package com.example.asaf_avisar.adapters;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asaf_avisar.CommentsBottomSheet;
import com.example.asaf_avisar.FireBaseManager;
import com.example.asaf_avisar.R;
import com.example.asaf_avisar.objects.ImageUtils;
import com.example.asaf_avisar.objects.Post;

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
    private static final int TYPE_GUARDIAN_TIME = 2;
    private static final int TYPE_STATUS = 3;

    /**
     * Instantiates a new Posts adapter.
     *
     * @param posts the posts
     */
    public PostsAdapter(List<Post> posts) {
        this.posts = (posts != null) ? posts : new ArrayList<>();
    }

    /**
     * Update the adapter's data with new posts
     *
     * @param newPosts the new posts to display
     */
    public void updatePosts(List<Post> newPosts) {
        this.posts.clear();
        if (newPosts != null) {
            this.posts.addAll(newPosts);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        Post post = posts.get(position);
        int type = post.getType();
        if (type == TYPE_PHOTO) {
            return TYPE_PHOTO;
        } else if (type == TYPE_STATUS) {
            return TYPE_STATUS;
        } else if (type == TYPE_GUARDIAN_TIME) {
            return TYPE_GUARDIAN_TIME;
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
        } else if (viewType == TYPE_GUARDIAN_TIME) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.status_post, parent, false);
            return new GuardianTimeViewHolder(view);
        } else if (viewType == TYPE_STATUS) {
            // If you have a separate layout for status posts
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.status_post, parent, false);
            return new StatusViewHolder(view);
        } else {
            // Default case
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_post, parent, false);
            return new NoteViewHolder(view);
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
            } else {
                photoHolder.userPfp.setImageResource(R.drawable.ic_default_profile);
            }
            photoHolder.userName.setText(post.getUserName());
            photoHolder.postContent.setImageResource(R.drawable.loading_post);
            photoHolder.photoDescription.setText(post.getDescription());
            photoHolder.postDate.setText(formattedDate);

            // Decode the post content (Base64 string) into Bitmap and display it
            String base64Content = post.getContent();  // Ensure this is the Base64 string for the image
            Bitmap postImageBitmap = ImageUtils.convert64base(base64Content);
            if (postImageBitmap != null) {
                photoHolder.postContent.setImageBitmap(postImageBitmap); // Set the decoded Bitmap to the ImageView
            }

            photoHolder.likeCount.setText(String.valueOf(post.getLikesCount()));
            photoHolder.likeButton.setOnClickListener(v -> handleLikeButtonClick(post, photoHolder.likeCount, photoHolder.likeButton));
            photoHolder.commentButton.setOnClickListener(v -> openCommentsBottomSheet(v, post));

        } else if (holder instanceof NoteViewHolder) {
            NoteViewHolder noteHolder = (NoteViewHolder) holder;

            if (userPfpBitmap != null) {
                noteHolder.userPfp.setImageBitmap(userPfpBitmap);
            } else {
                noteHolder.userPfp.setImageResource(R.drawable.ic_default_profile);
            }
            noteHolder.userName.setText(post.getUserName());
            noteHolder.postTitle.setText(post.getDescription());
            noteHolder.NoteContent.setText(post.getContent());
            noteHolder.postDate.setText(formattedDate);
            noteHolder.likeCount.setText(String.valueOf(post.getLikesCount()));

            noteHolder.likeButton.setOnClickListener(v -> handleLikeButtonClick(post, noteHolder.likeCount, noteHolder.likeButton));
            noteHolder.commentButton.setOnClickListener(v -> openCommentsBottomSheet(v, post));

        } else if (holder instanceof GuardianTimeViewHolder) {
            GuardianTimeViewHolder guardianHolder = (GuardianTimeViewHolder) holder;

            // Set user info
            if (userPfpBitmap != null) {
                guardianHolder.userPfp.setImageBitmap(userPfpBitmap);
            } else {
                guardianHolder.userPfp.setImageResource(R.drawable.ic_default_profile);
            }
            guardianHolder.userName.setText(post.getUserName());
            guardianHolder.postDate.setText(formattedDate);

            // Set guardian time data
            guardianHolder.dayCount.setText(String.valueOf(post.getDaysRemaining()));
            guardianHolder.nightCount.setText(String.valueOf(post.getNightsRemaining()));

            // Set progress bars
            guardianHolder.dayProgress.setProgress(post.getDayProgressPercentage());
            guardianHolder.nightProgress.setProgress(post.getNightProgressPercentage());

            // Set motivational message
            guardianHolder.motivationMessage.setText(post.getMotivationalMessage());

            // Set like count and interactions
            guardianHolder.likeCount.setText(String.valueOf(post.getLikesCount()));

            // Setup like button with proper color based on whether user has liked
            boolean hasLiked = post.getLikedByUsers() != null &&
                    post.getLikedByUsers().contains(FireBaseManager.getmAuth().getCurrentUser().getUid());

            if (hasLiked) {
                guardianHolder.likeButton.setColorFilter(Color.BLUE);
            } else {
                guardianHolder.likeButton.setColorFilter(Color.GRAY);
            }

            guardianHolder.likeButton.setOnClickListener(v ->
                    handleLikeButtonClick(post, guardianHolder.likeCount, guardianHolder.likeButton));
            guardianHolder.commentButton.setOnClickListener(v ->
                    openCommentsBottomSheet(v, post));

        } else if (holder instanceof StatusViewHolder) {
            StatusViewHolder statusHolder = (StatusViewHolder) holder;
            if (userPfpBitmap != null) {
                statusHolder.userPfp.setImageBitmap(userPfpBitmap);
            } else {
                statusHolder.userPfp.setImageResource(R.drawable.ic_default_profile);
            }
            statusHolder.userName.setText(post.getUserName());
            statusHolder.statusContent.setText(post.getContent());
            statusHolder.postDate.setText(formattedDate);
            statusHolder.likeCount.setText(String.valueOf(post.getLikesCount()));

            statusHolder.likeButton.setOnClickListener(v -> handleLikeButtonClick(post, statusHolder.likeCount, statusHolder.likeButton));
            statusHolder.commentButton.setOnClickListener(v -> openCommentsBottomSheet(v, post));
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    /**
     * Handle like button click for all post types
     */
    private void handleLikeButtonClick(Post post, TextView likeCountView, View likeButton) {
        String currentUserId = FireBaseManager.getmAuth().getCurrentUser().getUid();

        if (post.getLikedByUsers() == null) {
            post.setLikedByUsers(new ArrayList<>());
        }

        if (post.getLikedByUsers().contains(currentUserId)) {
            post.setLikesCount(post.getLikesCount() - 1);
            post.getLikedByUsers().remove(currentUserId);
            if (likeButton instanceof ImageView) {
                ((ImageView) likeButton).setColorFilter(Color.GRAY);
            }
        } else {
            post.setLikesCount(post.getLikesCount() + 1);
            post.getLikedByUsers().add(currentUserId);
            if (likeButton instanceof ImageView) {
                ((ImageView) likeButton).setColorFilter(Color.BLUE);
            }
            animateHeart(likeButton);
        }

        likeCountView.setText(String.valueOf(post.getLikesCount()));
        new FireBaseManager(likeButton.getContext()).updatePostLikes(post, post.getKey());
    }

    /**
     * Open comments bottom sheet for post
     */
    private void openCommentsBottomSheet(View v, Post post) {
        CommentsBottomSheet bottomSheet = CommentsBottomSheet.newInstance(post);
        bottomSheet.show(((AppCompatActivity) v.getContext()).getSupportFragmentManager(), "commentsBottomSheet");
    }

    /**
     * Animate heart button when liked
     */
    private void animateHeart(View view) {
        view.animate().scaleX(1.5f).scaleY(1.5f).setDuration(150)
                .withEndAction(() -> view.animate().scaleX(1f).scaleY(1f).setDuration(150).start()).start();
    }

    /**
     * The type Photo view holder.
     */
    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView postContent, userPfp, likeButton, commentButton;
        TextView userName, photoDescription, postDate, likeCount;

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
    static class NoteViewHolder extends RecyclerView.ViewHolder {
        ImageView userPfp, likeButton, commentButton;
        TextView userName, postTitle, NoteContent, postDate, likeCount;

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
     * ViewHolder for guardian time posts.
     */
    static class GuardianTimeViewHolder extends RecyclerView.ViewHolder {
        ImageView userPfp, likeButton, commentButton;
        TextView userName, postDate, dayCount, nightCount, motivationMessage, likeCount;
        ProgressBar dayProgress, nightProgress;

        GuardianTimeViewHolder(View itemView) {
            super(itemView);
            userPfp = itemView.findViewById(R.id.user_pfp);
            userName = itemView.findViewById(R.id.user_name);
            postDate = itemView.findViewById(R.id.post_date);
            dayCount = itemView.findViewById(R.id.day_count);
            nightCount = itemView.findViewById(R.id.night_count);
            dayProgress = itemView.findViewById(R.id.day_progress);
            nightProgress = itemView.findViewById(R.id.night_progress);
            motivationMessage = itemView.findViewById(R.id.motivation_message);
            likeButton = itemView.findViewById(R.id.like_button);
            commentButton = itemView.findViewById(R.id.comment_button);
            likeCount = itemView.findViewById(R.id.like_count);
        }
    }

    /**
     * The type Status view holder.
     */
    static class StatusViewHolder extends RecyclerView.ViewHolder {
        ImageView userPfp, likeButton, commentButton;
        TextView userName, statusContent, postDate, likeCount;

        StatusViewHolder(View itemView) {
            super(itemView);
            userPfp = itemView.findViewById(R.id.user_pfp);
            userName = itemView.findViewById(R.id.user_name);
            //statusContent = itemView.findViewById(R.id.status_content);
            postDate = itemView.findViewById(R.id.post_date);
            likeButton = itemView.findViewById(R.id.like_button);
            commentButton = itemView.findViewById(R.id.comment_button);
            likeCount = itemView.findViewById(R.id.like_count);
        }
    }
}
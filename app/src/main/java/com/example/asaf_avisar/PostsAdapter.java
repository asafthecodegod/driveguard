package com.example.asaf_avisar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asaf_avisar.activitys.Post;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Post> posts;
    private static final int TYPE_PHOTO = 1;
    private static final int TYPE_NOTE = 0;
    private static final int TYPE_STATUS = 3;

    public PostsAdapter(List<Post> posts) {
        this.posts = (posts != null) ? posts : new ArrayList<>(); // Ensure the list is never null
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

        if (holder instanceof PhotoViewHolder) {
            PhotoViewHolder photoHolder = (PhotoViewHolder) holder;
            photoHolder.userName.setText(post.getUserName());
            photoHolder.postTitle.setText(post.getDescription());
            photoHolder.description.setText(post.getDescription());
            photoHolder.postDate.setText(post.getDate() != null ? dateFormat.format(post.getDate()) : "Unknown Date");
        } else if (holder instanceof NoteViewHolder) {
            NoteViewHolder noteHolder = (NoteViewHolder) holder;
            noteHolder.userName.setText(post.getUserName());
            noteHolder.postTitle.setText(post.getDescription());
            noteHolder.noteText.setText(post.getDescription());
            noteHolder.postDate.setText(post.getDate() != null ? dateFormat.format(post.getDate()) : "Unknown Date");
        } else if (holder instanceof StatusViewHolder) {
            StatusViewHolder statusHolder = (StatusViewHolder) holder;
            statusHolder.userName.setText(post.getUserName());
            statusHolder.postTitle.setText(post.getDescription());
            statusHolder.postDate.setText(post.getDate() != null ? dateFormat.format(post.getDate()) : "Unknown Date");
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView postImage, userPfp;
        TextView userName, postTitle, description, postDate;
        ImageButton likeButton, commentButton;

        PhotoViewHolder(View itemView) {
            super(itemView);
            userPfp = itemView.findViewById(R.id.user_pfp);
            userName = itemView.findViewById(R.id.user_name);
            postTitle = itemView.findViewById(R.id.post_title);
            postDate = itemView.findViewById(R.id.post_date);
            postImage = itemView.findViewById(R.id.post_image);
            description = itemView.findViewById(R.id.photo_description);
            likeButton = itemView.findViewById(R.id.like_button);
            commentButton = itemView.findViewById(R.id.comment_button);
        }
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView userName, postTitle, noteText, postDate;
        ImageButton likeButton, commentButton;

        NoteViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name);
            postTitle = itemView.findViewById(R.id.post_title);
            noteText = itemView.findViewById(R.id.note_text);
            postDate = itemView.findViewById(R.id.post_date);
            likeButton = itemView.findViewById(R.id.like_button);
            commentButton = itemView.findViewById(R.id.comment_button);
        }
    }

    static class StatusViewHolder extends RecyclerView.ViewHolder {
        TextView userName, postTitle, postDate;
        ImageButton likeButton, commentButton;

        StatusViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name);
            postTitle = itemView.findViewById(R.id.post_title);
            postDate = itemView.findViewById(R.id.post_date);
            likeButton = itemView.findViewById(R.id.like_button);
            commentButton = itemView.findViewById(R.id.comment_button);
        }
    }
}

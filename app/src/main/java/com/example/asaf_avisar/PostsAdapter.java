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

import java.util.ArrayList;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Post> posts;
    private static final int TYPE_PHOTO = 1;
    private static final int TYPE_NOTE = 2;
    private static final int TYPE_STATUS = 3;

    public PostsAdapter(List<Post> posts) {
        this.posts = (posts != null) ? posts : new ArrayList<>(); // Ensure the list is never null
    }


    @Override
    public int getItemViewType(int position) {
        return posts.get(position).getType();
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
        if (holder instanceof PhotoViewHolder) {
            PhotoViewHolder photoHolder = (PhotoViewHolder) holder;
            photoHolder.userName.setText(post.getUserName());
            photoHolder.description.setText(post.getDescription());
            // Set other photo post data like the image
            // photoHolder.postImage.setImageResource(post.getImageResourceId());
        } else if (holder instanceof NoteViewHolder) {
            NoteViewHolder noteHolder = (NoteViewHolder) holder;
            noteHolder.userName.setText(post.getUserName());
            noteHolder.noteText.setText(post.getDescription());
        } else if (holder instanceof StatusViewHolder) {
            StatusViewHolder statusHolder = (StatusViewHolder) holder;
            statusHolder.userName.setText(post.getUserName());
            statusHolder.statusDescription.setText(post.getDescription());
            // Assuming post.getDaysLeft1() and post.getDaysLeft2() return the number of days left
            //statusHolder.daysLeft1.setText(post.getDaysLeft1());
            //statusHolder.daysLeft2.setText(post.getDaysLeft2());
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView postImage, userPfp;
        TextView userName, description;
        ImageButton likeButton, commentButton;

        PhotoViewHolder(View itemView) {
            super(itemView);
            userPfp = itemView.findViewById(R.id.user_pfp);
            userName = itemView.findViewById(R.id.user_name);
            postImage = itemView.findViewById(R.id.post_image);
            description = itemView.findViewById(R.id.photo_description);
            likeButton = itemView.findViewById(R.id.like_button);
            commentButton = itemView.findViewById(R.id.comment_button);
        }
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView userName, noteText;
        ImageButton likeButton, commentButton;

        NoteViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name);
            noteText = itemView.findViewById(R.id.note_text);
            likeButton = itemView.findViewById(R.id.like_button);
            commentButton = itemView.findViewById(R.id.comment_button);
        }
    }

    static class StatusViewHolder extends RecyclerView.ViewHolder {
        TextView userName, statusDescription, daysLeft1, daysLeft2;
        ImageButton likeButton, commentButton;

        StatusViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name);
            //statusDescription = itemView.findViewById(R.id.status_description);
            //daysLeft1 = itemView.findViewById(R.id.days_left_circle1);
            //daysLeft2 = itemView.findViewById(R.id.days_left_circle2);
            likeButton = itemView.findViewById(R.id.like_button);
            commentButton = itemView.findViewById(R.id.comment_button);
        }
    }
}

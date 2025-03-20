package com.example.asaf_avisar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.asaf_avisar.activitys.Post;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Post> posts;
    // Post types: 0 = note, 1 = photo, 3 = status.
    private static final int TYPE_NOTE = 0;
    private static final int TYPE_PHOTO = 1;
    private static final int TYPE_STATUS = 3;

    public PostsAdapter(List<Post> posts) {
        // Ensure the list is never null.
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
            // Inflate the provided photo_post layout.
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_post, parent, false);
            return new PhotoViewHolder(view);
        } else if (viewType == TYPE_NOTE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_post, parent, false);
            return new NoteViewHolder(view);
        } else { // TYPE_STATUS
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.status_post, parent, false);
            return new StatusViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Post post = posts.get(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        String formattedDate = post.getDate() != null ? dateFormat.format(post.getDate()) : "Unknown Date";

        // Decode the user profile picture from Base64.
        Bitmap userPfpBitmap = decodeBase64(post.getUserPfp());

        if (holder instanceof PhotoViewHolder) {
            PhotoViewHolder photoHolder = (PhotoViewHolder) holder;
            if (userPfpBitmap != null) {
                photoHolder.userPfp.setImageBitmap(userPfpBitmap);
            }
            photoHolder.userName.setText(post.getUserName());
            photoHolder.photoDescription.setText(post.getDescription());
            photoHolder.postDate.setText(formattedDate);
            Bitmap postImageBitmap = ImageUtils.convertBase64ToBitmap(post.getContent());
            photoHolder.postContent.setImageBitmap(postImageBitmap);

        } else if (holder instanceof NoteViewHolder) {
            NoteViewHolder noteHolder = (NoteViewHolder) holder;
            if (userPfpBitmap != null) {
                noteHolder.userPfp.setImageBitmap(userPfpBitmap);
            }
            noteHolder.userName.setText(post.getUserName());
            noteHolder.postTitle.setText(post.getDescription());
            noteHolder.noteText.setText(post.getContent()); // This should be `note_content` in your XML
            noteHolder.postDate.setText(formattedDate);

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

    // ViewHolder for photo posts, using the provided layout.
    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView postContent, userPfp;
        TextView userName, photoDescription, postDate;

        PhotoViewHolder(View itemView) {
            super(itemView);
            userPfp = itemView.findViewById(R.id.user_pfp);
            userName = itemView.findViewById(R.id.user_name);
            postContent = itemView.findViewById(R.id.photo_content); // Changed to `post_content`
            photoDescription = itemView.findViewById(R.id.photo_description);
            postDate = itemView.findViewById(R.id.post_date);
        }
    }

    // ViewHolder for note posts.
    static class NoteViewHolder extends RecyclerView.ViewHolder {
        ImageView userPfp;
        TextView userName, postTitle, noteText, postDate;

        NoteViewHolder(View itemView) {
            super(itemView);
            userPfp = itemView.findViewById(R.id.user_pfp);
            userName = itemView.findViewById(R.id.user_name);
            postTitle = itemView.findViewById(R.id.note_title);
            noteText = itemView.findViewById(R.id.note_content); // Changed to `note_text`
            postDate = itemView.findViewById(R.id.post_date);
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
            //postTitle = itemView.findViewById(R.id.post_title);
            postDate = itemView.findViewById(R.id.post_date);
        }
    }
}

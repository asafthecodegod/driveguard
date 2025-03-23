package com.example.asaf_avisar;

import com.example.asaf_avisar.activitys.Post;
import java.util.Date;
import java.util.List;

public class PostImage extends Post {

    private String photo; // Stores Base64 image string

    // Constructor with only image
    public PostImage(String photo) {
        super();
        this.photo = photo;
    }

    // Main constructor with all post details
    public PostImage(String userId, String userName, String description, int likesCount,
                     String content, List<String> likedByUsers, List<Comment> comments,
                     String userPfp, Date date) {
        super(userId, userName, description, likesCount, content, likedByUsers, comments, userPfp, date);
        this.photo = content; // Ensure photo and content are aligned
    }

    // Constructor with post type handling
    public PostImage(String userId, String userName, String description, int likesCount,
                     String content, List<String> likedByUsers, String userPfp,
                     List<Comment> comments, int type, Date date) {
        super(userId, userName, description, likesCount, content, likedByUsers, comments, userPfp, date);
        this.photo = content; // Ensuring consistency
        this.setType(1); // Setting post type correctly
    }

    // Getter and Setter for Photo
    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}

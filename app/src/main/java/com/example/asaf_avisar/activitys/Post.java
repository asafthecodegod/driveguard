package com.example.asaf_avisar.activitys;

import com.example.asaf_avisar.Comment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Post implements Serializable {
    private String key;            // Unique post ID (Firebase key)
    private String userId;        // ID of the user who posted
    private String userName;      // Name of the user who posted
    private String userPfp;       // Profile picture URL of the user
    private String imageUrl;      // Image URL in the post
    private String description;   // Post description or caption
    private String content;       // Optional post content (text-only or mixed)
    private Date date;            // Post creation date
    private long timestamp;       // Unix timestamp (optional, can be used for sorting)
    private int likesCount;       // Number of likes
    private List<String> likedByUsers; // List of userIds who liked
    private List<Comment> comments;    // List of comments
    private int type;

    public Post() {
        likedByUsers = new ArrayList<>();
        comments = new ArrayList<>();
    }

    public Post(String userId, String userName, String userPfp, String imageUrl, String description, String content, Date date, int type) {
        this.userId = userId;
        this.userName = userName;
        this.userPfp = userPfp;
        this.imageUrl = imageUrl;
        this.description = description;
        this.content = content;
        this.date = date;
        this.timestamp = date.getTime();
        this.likesCount = 0;
        this.likedByUsers = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.type = 1;
    }


    public Post(
            String userName,
            String description,
            String content,
            String imageUrl,
            Date date

    ) {

        this.userName      = userName;
        this.description   = description;
        this.content       = content;
        this.imageUrl      = imageUrl;
        this.date          = date;
        this.timestamp     = date.getTime();
        this.type          = 0;
        this.likesCount    = 0;
        this.likedByUsers  = new ArrayList<>();
        this.comments      = new ArrayList<>();
    }

    // --- Getters and Setters ---


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserPfp() { return userPfp; }
    public void setUserPfp(String userPfp) { this.userPfp = userPfp; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Date getDate() { return date; }
    public void setDate(Date date) {
        this.date = date;
        this.timestamp = date.getTime();
    }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public int getLikesCount() { return likesCount; }
    public void setLikesCount(int likesCount) { this.likesCount = likesCount; }

    public List<String> getLikedByUsers() { return likedByUsers; }
    public void setLikedByUsers(List<String> likedByUsers) { this.likedByUsers = likedByUsers; }

    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }
}

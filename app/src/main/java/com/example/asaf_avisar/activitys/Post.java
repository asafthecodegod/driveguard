package com.example.asaf_avisar.activitys;

import com.example.asaf_avisar.Comment;
import java.util.List;

public class Post {
    private String userId; // User ID of the person who uploaded the post
    private String userName; // User name of the person who uploaded the post
    private String description;
    private int type; // 1 - Photo, 2 - Note, 3 - Status
    private int likesCount; // Like count
    private List<String> likedByUsers; // List of user IDs who liked the post
    private List<Comment> comments; // List of comments

    // Constructor
    public Post(String userId, String userName, String description, int type, int likesCount, List<String> likedByUsers, List<Comment> comments) {
        this.userId = userId;
        this.userName = userName;
        this.description = description;
        this.type = type;
        this.likesCount = likesCount;
        this.likedByUsers = likedByUsers;
        this.comments = comments;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public List<String> getLikedByUsers() {
        return likedByUsers;
    }

    public void setLikedByUsers(List<String> likedByUsers) {
        this.likedByUsers = likedByUsers;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}

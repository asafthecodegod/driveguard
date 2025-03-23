package com.example.asaf_avisar.activitys;

import com.example.asaf_avisar.Comment;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Post implements Serializable {
    private String key;         // Unique post ID (Firebase push key)
    private String userId;      // User ID of the person who uploaded the post
    private String userName;    // User name of the person who uploaded the post
    private String description; // Post description or title
    private String content;     // Content (for photo, note, etc.)
    private int likesCount;     // Number of likes
    private List<String> likedByUsers; // List of user IDs who liked the post
    private List<Comment> comments;    // List of comments
    private int type;           // Post type (0 = note, 1 = photo, 3 = status)
    private Date date;          // Date when the post was added
    private String userPfp;     // User profile picture (Base64 string or URL)

    // Default constructor (required for Firebase)
    public Post() {
    }

    // Constructor for minimal post (without userId, likedByUsers, and comments)
    public Post(String userName, String description, String content, String userPfp, Date date) {
        this.userName = userName;
        this.description = description;
        this.content = content;
        this.userPfp = userPfp;
        this.date = date;
        this.likesCount = 0;
        this.type = 0;
    }

    // Full constructor
    public Post(String userId, String userName, String description, int likesCount,
                String content, List<String> likedByUsers, List<Comment> comments,
                String userPfp, Date date) {
        this.userId = userId;
        this.userName = userName;
        this.description = description;
        this.likesCount = likesCount;
        this.content = content;
        this.likedByUsers = likedByUsers;
        this.comments = comments;
        this.userPfp = userPfp;
        this.date = date;
        this.type = 0;
    }

    // Getter and Setter for key
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }

    // Getters and setters for remaining fields
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

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public int getLikesCount() {
        return likesCount;
    }
    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public List<String> getLikedByUsers() {
        if (likedByUsers == null) likedByUsers = new ArrayList<>();
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

    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }

    public String getUserPfp() {
        return userPfp;
    }
    public void setUserPfp(String userPfp) {
        this.userPfp = userPfp;
    }
}

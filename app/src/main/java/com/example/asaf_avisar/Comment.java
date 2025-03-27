package com.example.asaf_avisar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Comment implements Serializable {
    private String key;         // Unique comment ID (Firebase push key)
    private String postId;      // ID of the post this comment belongs to
    private String userId;
    private String userName;
    private String content;
    private Date date;
    private int likesCount;
    //private List<Comment> replies; // ✅ Support nested replies
    private List<String> likedByUsers; // List of user IDs who liked the post
    private String profilePictureUrl; // Profile picture URL

//    public Comment() {
//        this.replies = new ArrayList<>(); // ✅ Prevent null pointer crashes
//    }

    public Comment(String postId, String userId, String userName, String content, Date date, String profilePictureUrl) {
        this.postId = postId;
        this.userId = userId;
        this.userName = userName;
        this.content = content;
        this.date = date;
        this.likesCount = 0;
        //this.replies = new ArrayList<>();
        this.profilePictureUrl = profilePictureUrl;
    }

    // Getters & Setters
    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public List<String> getLikedByUsers() {
        if (likedByUsers == null) likedByUsers = new ArrayList<>();
        return likedByUsers;
    }

    public void setLikedByUsers(List<String> likedByUsers) {
        this.likedByUsers = likedByUsers;
    }

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

//    public List<Comment> getReplies() {
//        if (replies == null) replies = new ArrayList<>();
//        return replies;
//    }
//
//    public void setReplies(List<Comment> replies) {
//        this.replies = replies;
//    }
}

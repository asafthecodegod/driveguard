package com.example.asaf_avisar.activitys;

import com.example.asaf_avisar.Comment;

import java.util.Date;
import java.util.List;

public class Post {
    private String userId; // User ID of the person who uploaded the post
    private String userName; // User name of the person who uploaded the post
    private String description;
    private String content;
    private int likesCount; // Like count
    private List<String> likedByUsers; // List of user IDs who liked the post
    private List<Comment> comments; // List of comments
    private int type;
    private Date date; // Added date field
    private String userPfp;
    public Post() {

    }

    public Post(String userName, String description, int likesCount, String content,int type,String userPfp,Date date) {
        this.userName = userName;
        this.description = description;
        this.content = content;
        this.likesCount = 0;
        this.type = 0;
        this.date = date;
        this.userPfp = userPfp;
    }

    // Constructor
    public Post(String userId, String userName, String description, int likesCount,String content, List<String> likedByUsers, List<Comment> comments,String userPfp,Date date) {
        this.userId = userId;
        this.userName = userName;
        this.description = description;
        this.likesCount = likesCount;
        this.likedByUsers = likedByUsers;
        this.comments = comments;
        this.content = content;
        this.type = 0;
        this.date = date;
        this.userPfp = userPfp;
    }

    public String getUserPfp() {
        return userPfp;
    }

    public void setUserPfp(String userPfp) {
        this.userPfp = userPfp;
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

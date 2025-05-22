package com.example.asaf_avisar.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The type Comment.
 */
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

    /**
     * Instantiates a new Comment.
     *
     * @param postId            the post id
     * @param userId            the user id
     * @param userName          the user name
     * @param content           the content
     * @param date              the date
     * @param profilePictureUrl the profile picture url
     */
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

    /**
     * Gets profile picture url.
     *
     * @return the profile picture url
     */
// Getters & Setters
    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    /**
     * Sets profile picture url.
     *
     * @param profilePictureUrl the profile picture url
     */
    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    /**
     * Gets key.
     *
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets key.
     *
     * @param key the key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Gets post id.
     *
     * @return the post id
     */
    public String getPostId() {
        return postId;
    }

    /**
     * Sets post id.
     *
     * @param postId the post id
     */
    public void setPostId(String postId) {
        this.postId = postId;
    }

    /**
     * Gets liked by users.
     *
     * @return the liked by users
     */
    public List<String> getLikedByUsers() {
        if (likedByUsers == null) likedByUsers = new ArrayList<>();
        return likedByUsers;
    }

    /**
     * Sets liked by users.
     *
     * @param likedByUsers the liked by users
     */
    public void setLikedByUsers(List<String> likedByUsers) {
        this.likedByUsers = likedByUsers;
    }

    /**
     * Gets user id.
     *
     * @return the user id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets user id.
     *
     * @param userId the user id
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets user name.
     *
     * @return the user name
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets user name.
     *
     * @param userName the user name
     */
    public void setUserName(String userName) {

        this.userName = userName;
    }

    /**
     * Gets content.
     *
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets content.
     *
     * @param content the content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Gets date.
     *
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Sets date.
     *
     * @param date the date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Gets likes count.
     *
     * @return the likes count
     */
    public int getLikesCount() {
        return likesCount;
    }

    /**
     * Sets likes count.
     *
     * @param likesCount the likes count
     */
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

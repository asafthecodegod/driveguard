package com.example.asaf_avisar.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The type Post - represents different types of posts in the application.
 */
public class Post implements Serializable {
    private String key;            // Unique post ID (Firebase key)
    private String userId;         // ID of the user who posted
    private String userName;       // Name of the user who posted
    private String userPfp;        // Profile picture URL of the user
    private String imageUrl;       // Image URL in the post
    private String description;    // Post description or caption
    private String content;        // Optional post content (text-only or mixed)
    private Date date;             // Post creation date
    private long timestamp;        // Unix timestamp (for sorting)
    private int likesCount;        // Number of likes
    private List<String> likedByUsers; // List of userIds who liked
    private List<Comment> comments;    // List of comments
    private int type;              // Post type: 0=Note, 1=Photo, 2=GuardianTime, 3=Status

    // Guardian time specific fields
    private int daysRemaining;
    private int nightsRemaining;

    // Fixed guardian time constants
    private static final int TOTAL_DAY_REQUIRED = 90;   // 90 days for day guardian
    private static final int TOTAL_NIGHT_REQUIRED = 180; // 180 days for night guardian

    /**
     * Empty constructor for Firebase.
     */
    public Post() {
        likedByUsers = new ArrayList<>();
        comments = new ArrayList<>();
    }

    /**
     * Constructor for photo posts.
     *
     * @param userId      the user id
     * @param userName    the user name
     * @param userPfp     the user profile photo
     * @param imageUrl    the image url
     * @param description the description
     * @param content     the content
     * @param date        the date
     * @param type        the type (typically 1 for photos)
     */
    public Post(String userId, String userName, String userPfp, String imageUrl,
                String description, String content, Date date, int type) {
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
        this.type = type;
    }

    /**
     * Constructor for note posts.
     *
     * @param userName    the user name
     * @param description the description/title
     * @param content     the content
     * @param userPfp     the user profile photo
     * @param date        the date
     */
    public Post(String userName, String description, String content, String userPfp, Date date) {
        this.userName = userName;
        this.description = description;
        this.content = content;
        this.userPfp = userPfp;
        this.date = date;
        this.timestamp = date.getTime();
        this.type = 0;  // Note type
        this.likesCount = 0;
        this.likedByUsers = new ArrayList<>();
        this.comments = new ArrayList<>();
    }


    /**
     * Constructor for guardian time posts.
     *
     * @param userId          the user id
     * @param userName        the user name
     * @param userPfp         the user profile photo
     * @param daysRemaining   days remaining in guardian time
     * @param nightsRemaining nights remaining in guardian time
     * @param date            the date
     */
    public Post(String userId, String userName, String userPfp, int daysRemaining,
                int nightsRemaining, Date date) {
        this.userId = userId;
        this.userName = userName;
        this.userPfp = userPfp;
        this.daysRemaining = daysRemaining;
        this.nightsRemaining = nightsRemaining;
        this.date = date;
        this.timestamp = date.getTime();
        this.type = 2; // Guardian time type
        this.description = "Guardian Time Remaining"; // Fixed title
        this.likesCount = 0;
        this.likedByUsers = new ArrayList<>();
        this.comments = new ArrayList<>();
    }

    // --- Getters and Setters ---

    /**
     * Gets type.
     *
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * Sets type.
     *
     * @param type the type
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Gets key.
     *
     * @return the key
     */
    public String getKey() { return key; }

    /**
     * Sets key.
     *
     * @param key the key
     */
    public void setKey(String key) { this.key = key; }

    /**
     * Gets user id.
     *
     * @return the user id
     */
    public String getUserId() { return userId; }

    /**
     * Sets user id.
     *
     * @param userId the user id
     */
    public void setUserId(String userId) { this.userId = userId; }

    /**
     * Gets user name.
     *
     * @return the user name
     */
    public String getUserName() { return userName; }

    /**
     * Sets user name.
     *
     * @param userName the user name
     */
    public void setUserName(String userName) { this.userName = userName; }

    /**
     * Gets user pfp.
     *
     * @return the user pfp
     */
    public String getUserPfp() { return userPfp; }

    /**
     * Sets user pfp.
     *
     * @param userPfp the user pfp
     */
    public void setUserPfp(String userPfp) { this.userPfp = userPfp; }

    /**
     * Gets image url.
     *
     * @return the image url
     */
    public String getImageUrl() { return imageUrl; }

    /**
     * Sets image url.
     *
     * @param imageUrl the image url
     */
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() { return description; }

    /**
     * Sets description.
     *
     * @param description the description
     */
    public void setDescription(String description) { this.description = description; }

    /**
     * Gets content.
     *
     * @return the content
     */
    public String getContent() { return content; }

    /**
     * Sets content.
     *
     * @param content the content
     */
    public void setContent(String content) { this.content = content; }

    /**
     * Gets date.
     *
     * @return the date
     */
    public Date getDate() { return date; }

    /**
     * Sets date.
     *
     * @param date the date
     */
    public void setDate(Date date) {
        this.date = date;
        this.timestamp = date.getTime();
    }

    /**
     * Gets timestamp.
     *
     * @return the timestamp
     */
    public long getTimestamp() { return timestamp; }

    /**
     * Sets timestamp.
     *
     * @param timestamp the timestamp
     */
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    /**
     * Gets likes count.
     *
     * @return the likes count
     */
    public int getLikesCount() { return likesCount; }

    /**
     * Sets likes count.
     *
     * @param likesCount the likes count
     */
    public void setLikesCount(int likesCount) { this.likesCount = likesCount; }

    /**
     * Gets liked by users.
     *
     * @return the liked by users
     */
    public List<String> getLikedByUsers() { return likedByUsers; }

    /**
     * Sets liked by users.
     *
     * @param likedByUsers the liked by users
     */
    public void setLikedByUsers(List<String> likedByUsers) { this.likedByUsers = likedByUsers; }

    /**
     * Gets comments.
     *
     * @return the comments
     */
    public List<Comment> getComments() { return comments; }

    /**
     * Sets comments.
     *
     * @param comments the comments
     */
    public void setComments(List<Comment> comments) { this.comments = comments; }

    /**
     * Gets days remaining.
     *
     * @return the days remaining
     */
    public int getDaysRemaining() {
        return daysRemaining;
    }

    /**
     * Sets days remaining.
     *
     * @param daysRemaining the days remaining
     */
    public void setDaysRemaining(int daysRemaining) {
        this.daysRemaining = daysRemaining;
    }

    /**
     * Gets nights remaining.
     *
     * @return the nights remaining
     */
    public int getNightsRemaining() {
        return nightsRemaining;
    }

    /**
     * Sets nights remaining.
     *
     * @param nightsRemaining the nights remaining
     */
    public void setNightsRemaining(int nightsRemaining) {
        this.nightsRemaining = nightsRemaining;
    }

    /**
     * Gets total day required.
     *
     * @return the total day required
     */
    public int getTotalDayRequired() {
        return TOTAL_DAY_REQUIRED;
    }

    /**
     * Gets total night required.
     *
     * @return the total night required
     */
    public int getTotalNightRequired() {
        return TOTAL_NIGHT_REQUIRED;
    }

    /**
     * Gets day progress percentage.
     *
     * @return the day progress percentage
     */
    public int getDayProgressPercentage() {
        int completed = TOTAL_DAY_REQUIRED - daysRemaining;
        return (int) (((float) completed / TOTAL_DAY_REQUIRED) * 100);
    }

    /**
     * Gets night progress percentage.
     *
     * @return the night progress percentage
     */
    public int getNightProgressPercentage() {
        int completed = TOTAL_NIGHT_REQUIRED - nightsRemaining;
        return (int) (((float) completed / TOTAL_NIGHT_REQUIRED) * 100);
    }

    /**
     * Gets motivational message based on progress.
     *
     * @return the motivational message
     */
    public String getMotivationalMessage() {
        int dayProgress = getDayProgressPercentage();
        int nightProgress = getNightProgressPercentage();
        int averageProgress = (dayProgress + nightProgress) / 2;

        if (averageProgress < 25) {
            return "You're just getting started! Keep going!";
        } else if (averageProgress < 50) {
            return "Great progress! You're moving forward!";
        } else if (averageProgress < 75) {
            return "You're more than halfway there! Keep it up!";
        } else if (averageProgress < 100) {
            return "Almost there! The finish line is in sight!";
        } else {
            return "Congratulations! You've completed your guardian time!";
        }
    }
}
package com.example.asaf_avisar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The type Student user.
 */
public class StudentUser {
    private String id;
    private String name;
    private String email;
    private String password;
    private Date birthday;
    private String bio;
    // Driving-related fields
    private boolean hasLicense;
    private boolean hasGreenForm;
    private boolean passedTheory;
    private Date licenseDate;
    private int lessonCount;
    private int timeHaveLicense; // days since license acquired
    private boolean isTeacher;

    // Location and driver type
    private boolean driverType;
    private String city;

    // Social stats
    private int followerCount;
    private int followingCount;

    // Friend relationships and requests
    private boolean isPrivate;
    private List<String> friends = new ArrayList<>();
    private List<String> friendRequests = new ArrayList<>();     // incoming requests
    private List<String> sentRequests = new ArrayList<>();       // outgoing requests

    // Investment in driving school
    private int drivingInvestment;

    // Profile photo (Base64)
    private String profilePhotoBase64;

    /**
     * Instantiates a new Student user.
     */
    public StudentUser() {
        // Default constructor for Firebase
    }

    /**
     * Convenience constructor for registration
     *
     * @param name     the name
     * @param email    the email
     * @param password the password
     * @param birthday the birthday
     */
    public StudentUser(String name, String email, String password, Date birthday) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.birthday = birthday;
        this.isPrivate = false; // default public
    }

    /**
     * Gets bio.
     *
     * @return the bio
     */
    public String getBio() {
        return bio;
    }

    /**
     * Sets bio.
     *
     * @param bio the bio
     */
    public void setBio(String bio) {
        this.bio = bio;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
// Getters and setters
    public String getId() { return id; }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(String id) { this.id = id; }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() { return name; }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) { this.name = name; }

    /**
     * Gets email.
     *
     * @return the email
     */
    public String getEmail() { return email; }

    /**
     * Sets email.
     *
     * @param email the email
     */
    public void setEmail(String email) { this.email = email; }

    /**
     * Gets password.
     *
     * @return the password
     */
    public String getPassword() { return password; }

    /**
     * Sets password.
     *
     * @param password the password
     */
    public void setPassword(String password) { this.password = password; }

    /**
     * Gets birthday.
     *
     * @return the birthday
     */
    public Date getBirthday() { return birthday; }

    /**
     * Sets birthday.
     *
     * @param birthday the birthday
     */
    public void setBirthday(Date birthday) { this.birthday = birthday; }

    /**
     * Is has license boolean.
     *
     * @return the boolean
     */
    public boolean isHasLicense() { return hasLicense; }

    /**
     * Sets has license.
     *
     * @param hasLicense the has license
     */
    public void setHasLicense(boolean hasLicense) { this.hasLicense = hasLicense; }

    /**
     * Is has green form boolean.
     *
     * @return the boolean
     */
    public boolean isHasGreenForm() { return hasGreenForm; }

    /**
     * Sets has green form.
     *
     * @param hasGreenForm the has green form
     */
    public void setHasGreenForm(boolean hasGreenForm) { this.hasGreenForm = hasGreenForm; }

    /**
     * Is passed theory boolean.
     *
     * @return the boolean
     */
    public boolean isPassedTheory() { return passedTheory; }

    /**
     * Sets passed theory.
     *
     * @param passedTheory the passed theory
     */
    public void setPassedTheory(boolean passedTheory) { this.passedTheory = passedTheory; }

    /**
     * Gets license date.
     *
     * @return the license date
     */
    public Date getLicenseDate() { return licenseDate; }

    /**
     * Sets license date.
     *
     * @param licenseDate the license date
     */
    public void setLicenseDate(Date licenseDate) { this.licenseDate = licenseDate; }

    /**
     * Gets lesson count.
     *
     * @return the lesson count
     */
    public int getLessonCount() { return lessonCount; }

    /**
     * Sets lesson count.
     *
     * @param lessonCount the lesson count
     */
    public void setLessonCount(int lessonCount) { this.lessonCount = lessonCount; }

    /**
     * Gets time have license.
     *
     * @return the time have license
     */
    public int getTimeHaveLicense() { return timeHaveLicense; }

    /**
     * Sets time have license.
     *
     * @param timeHaveLicense the time have license
     */
    public void setTimeHaveLicense(int timeHaveLicense) { this.timeHaveLicense = timeHaveLicense; }

    /**
     * Is driver type boolean.
     *
     * @return the boolean
     */
    public boolean isDriverType() { return driverType; }

    /**
     * Sets driver type.
     *
     * @param driverType the driver type
     */
    public void setDriverType(boolean driverType) { this.driverType = driverType; }

    /**
     * Gets city.
     *
     * @return the city
     */
    public String getCity() { return city; }

    /**
     * Sets city.
     *
     * @param city the city
     */
    public void setCity(String city) { this.city = city; }

    /**
     * Gets follower count.
     *
     * @return the follower count
     */
    public int getFollowerCount() { return followerCount; }

    /**
     * Sets follower count.
     *
     * @param followerCount the follower count
     */
    public void setFollowerCount(int followerCount) { this.followerCount = followerCount; }

    /**
     * Gets following count.
     *
     * @return the following count
     */
    public int getFollowingCount() { return followingCount; }

    /**
     * Sets following count.
     *
     * @param followingCount the following count
     */
    public void setFollowingCount(int followingCount) { this.followingCount = followingCount; }

    /**
     * Gets driving investment.
     *
     * @return the driving investment
     */
    public int getDrivingInvestment() { return drivingInvestment; }

    /**
     * Sets driving investment.
     *
     * @param drivingInvestment the driving investment
     */
    public void setDrivingInvestment(int drivingInvestment) { this.drivingInvestment = drivingInvestment; }

    /**
     * Is private boolean.
     *
     * @return the boolean
     */
    public boolean isPrivate() { return isPrivate; }

    /**
     * Sets private.
     *
     * @param isPrivate the is private
     */
    public void setPrivate(boolean isPrivate) { this.isPrivate = isPrivate; }

    /**
     * Gets friends.
     *
     * @return the friends
     */
    public List<String> getFriends() { return friends; }

    /**
     * Sets friends.
     *
     * @param friends the friends
     */
    public void setFriends(List<String> friends) { this.friends = friends; }

    /**
     * Gets friend requests.
     *
     * @return the friend requests
     */
    public List<String> getFriendRequests() { return friendRequests; }

    /**
     * Sets friend requests.
     *
     * @param friendRequests the friend requests
     */
    public void setFriendRequests(List<String> friendRequests) { this.friendRequests = friendRequests; }

    /**
     * Gets sent requests.
     *
     * @return the sent requests
     */
    public List<String> getSentRequests() { return sentRequests; }

    /**
     * Sets sent requests.
     *
     * @param sentRequests the sent requests
     */
    public void setSentRequests(List<String> sentRequests) { this.sentRequests = sentRequests; }

    // Helpers for friend request flow

    /**
     * Send a friend request (adds to outgoing list)
     *
     * @param targetUserId the target user id
     */
    public void sendFriendRequest(String targetUserId) {
        if (!sentRequests.contains(targetUserId) && !friends.contains(targetUserId)) {
            sentRequests.add(targetUserId);
        }
    }

    /**
     * Receive a friend request (adds to incoming list)
     *
     * @param fromUserId the from user id
     */
    public void receiveFriendRequest(String fromUserId) {
        if (!friendRequests.contains(fromUserId) && !friends.contains(fromUserId)) {
            friendRequests.add(fromUserId);
        }
    }

    /**
     * Accept an incoming friend request
     *
     * @param userId the user id
     * @return true if accepted, false otherwise
     */
    public boolean acceptFriendRequest(String userId) {
        if (friendRequests.remove(userId)) {
            friends.add(userId);
            followerCount++;         // they follow you
            followingCount++;        // you follow them
            return true;
        }
        return false;
    }

    /**
     * Decline an incoming friend request
     *
     * @param userId the user id
     */
    public void declineFriendRequest(String userId) {
        friendRequests.remove(userId);
    }

    /**
     * Check if a given userId is a friend
     *
     * @param userId the user id
     * @return the boolean
     */
    public boolean isFriend(String userId) {
        return friends.contains(userId);
    }

    /**
     * Check if a request has been sent to a specific user
     *
     * @param userId the user id
     * @return the boolean
     */
    public boolean hasSentRequest(String userId) {
        return sentRequests.contains(userId);
    }

    /**
     * Check if there's an incoming request from a specific user
     *
     * @param userId the user id
     * @return the boolean
     */
    public boolean hasIncomingRequest(String userId) {
        return friendRequests.contains(userId);
    }

    /**
     * Gets profile photo base 64.
     *
     * @return the profile photo base 64
     */
    public String getProfilePhotoBase64() { return profilePhotoBase64; }

    /**
     * Sets profile photo base 64.
     *
     * @param profilePhotoBase64 the profile photo base 64
     */
    public void setProfilePhotoBase64(String profilePhotoBase64) { this.profilePhotoBase64 = profilePhotoBase64; }


    /**
     * Compute days left of day-only driving (90 days required)
     *
     * @return the days left
     */
    public int getDaysLeft() {
        return Math.max(0, 90 - timeHaveLicense);
    }

    /**
     * Compute days left of night driving (180 days required)
     *
     * @return the night days left
     */
    public int getNightDaysLeft() {
        return Math.max(0, 180 - timeHaveLicense);
    }

    public boolean isTeacher() {
        return isTeacher;
    }

    public void setTeacher(boolean teacher) {
        isTeacher = teacher;
    }
}

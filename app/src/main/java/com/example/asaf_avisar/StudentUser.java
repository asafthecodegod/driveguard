package com.example.asaf_avisar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public StudentUser() {
        // Default constructor for Firebase
    }

    /**
     * Convenience constructor for registration
     */
    public StudentUser(String name, String email, String password, Date birthday) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.birthday = birthday;
        this.isPrivate = false; // default public
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Date getBirthday() { return birthday; }
    public void setBirthday(Date birthday) { this.birthday = birthday; }

    public boolean isHasLicense() { return hasLicense; }
    public void setHasLicense(boolean hasLicense) { this.hasLicense = hasLicense; }

    public boolean isHasGreenForm() { return hasGreenForm; }
    public void setHasGreenForm(boolean hasGreenForm) { this.hasGreenForm = hasGreenForm; }

    public boolean isPassedTheory() { return passedTheory; }
    public void setPassedTheory(boolean passedTheory) { this.passedTheory = passedTheory; }

    public Date getLicenseDate() { return licenseDate; }
    public void setLicenseDate(Date licenseDate) { this.licenseDate = licenseDate; }

    public int getLessonCount() { return lessonCount; }
    public void setLessonCount(int lessonCount) { this.lessonCount = lessonCount; }

    public int getTimeHaveLicense() { return timeHaveLicense; }
    public void setTimeHaveLicense(int timeHaveLicense) { this.timeHaveLicense = timeHaveLicense; }

    public boolean isDriverType() { return driverType; }
    public void setDriverType(boolean driverType) { this.driverType = driverType; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public int getFollowerCount() { return followerCount; }
    public void setFollowerCount(int followerCount) { this.followerCount = followerCount; }

    public int getFollowingCount() { return followingCount; }
    public void setFollowingCount(int followingCount) { this.followingCount = followingCount; }

    public int getDrivingInvestment() { return drivingInvestment; }
    public void setDrivingInvestment(int drivingInvestment) { this.drivingInvestment = drivingInvestment; }

    public boolean isPrivate() { return isPrivate; }
    public void setPrivate(boolean isPrivate) { this.isPrivate = isPrivate; }

    public List<String> getFriends() { return friends; }
    public void setFriends(List<String> friends) { this.friends = friends; }

    public List<String> getFriendRequests() { return friendRequests; }
    public void setFriendRequests(List<String> friendRequests) { this.friendRequests = friendRequests; }

    public List<String> getSentRequests() { return sentRequests; }
    public void setSentRequests(List<String> sentRequests) { this.sentRequests = sentRequests; }

    // Helpers for friend request flow

    /**
     * Send a friend request (adds to outgoing list)
     */
    public void sendFriendRequest(String targetUserId) {
        if (!sentRequests.contains(targetUserId) && !friends.contains(targetUserId)) {
            sentRequests.add(targetUserId);
        }
    }

    /**
     * Receive a friend request (adds to incoming list)
     */
    public void receiveFriendRequest(String fromUserId) {
        if (!friendRequests.contains(fromUserId) && !friends.contains(fromUserId)) {
            friendRequests.add(fromUserId);
        }
    }

    /**
     * Accept an incoming friend request
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
     */
    public void declineFriendRequest(String userId) {
        friendRequests.remove(userId);
    }

    /**
     * Check if a given userId is a friend
     */
    public boolean isFriend(String userId) {
        return friends.contains(userId);
    }

    /**
     * Check if a request has been sent to a specific user
     */
    public boolean hasSentRequest(String userId) {
        return sentRequests.contains(userId);
    }

    /**
     * Check if there's an incoming request from a specific user
     */
    public boolean hasIncomingRequest(String userId) {
        return friendRequests.contains(userId);
    }

    public String getProfilePhotoBase64() { return profilePhotoBase64; }
    public void setProfilePhotoBase64(String profilePhotoBase64) { this.profilePhotoBase64 = profilePhotoBase64; }





    /**
     * Compute days left of day-only driving (90 days required)
     */
    public int getDaysLeft() {
        return Math.max(0, 90 - timeHaveLicense);
    }

    /**
     * Compute days left of night driving (180 days required)
     */
    public int getNightDaysLeft() {
        return Math.max(0, 180 - timeHaveLicense);
    }
}

package com.example.asaf_avisar.objects;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // Following system - NEW (using Map to match Firebase structure)
    private Map<String, Boolean> followers = new HashMap<>();   // users who follow this user
    private Map<String, Boolean> following = new HashMap<>();   // users this user follows

    // Investment in driving school
    private int drivingInvestment;

    // Profile photo (Base64)
    private String profilePhotoBase64;
    private String phone;
    private int price;

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
    }

    // Basic getters and setters
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
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
    public String getProfilePhotoBase64() { return profilePhotoBase64; }
    public void setProfilePhotoBase64(String profilePhotoBase64) { this.profilePhotoBase64 = profilePhotoBase64; }
    public boolean isTeacher() { return isTeacher; }
    public void setTeacher(boolean teacher) { isTeacher = teacher; }

    // NEW: Following system getters and setters
    public Map<String, Boolean> getFollowers() {
        if (followers == null) followers = new HashMap<>();
        return followers;
    }

    public void setFollowers(Map<String, Boolean> followers) {
        this.followers = followers;
        // Update follower count when setting followers map
        this.followerCount = followers != null ? followers.size() : 0;
    }

    public Map<String, Boolean> getFollowing() {
        if (following == null) following = new HashMap<>();
        return following;
    }

    public void setFollowing(Map<String, Boolean> following) {
        this.following = following;
        // Update following count when setting following map
        this.followingCount = following != null ? following.size() : 0;
    }

    // NEW: Following system helper methods

    /**
     * Check if this user is being followed by another user
     *
     * @param userId the user id to check
     * @return true if the user is following this user
     */
    public boolean isFollowedBy(String userId) {
        return followers != null && followers.containsKey(userId) && Boolean.TRUE.equals(followers.get(userId));
    }

    /**
     * Check if this user is following another user
     *
     * @param userId the user id to check
     * @return true if this user is following the specified user
     */
    public boolean isFollowing(String userId) {
        return following != null && following.containsKey(userId) && Boolean.TRUE.equals(following.get(userId));
    }

    /**
     * Add a follower to this user
     *
     * @param userId the user id to add as follower
     */
    public void addFollower(String userId) {
        if (followers == null) followers = new HashMap<>();
        followers.put(userId, true);
        followerCount = followers.size();
    }

    /**
     * Remove a follower from this user
     *
     * @param userId the user id to remove from followers
     */
    public void removeFollower(String userId) {
        if (followers != null && followers.remove(userId) != null) {
            followerCount = followers.size();
        }
    }

    /**
     * Add a user to this user's following list
     *
     * @param userId the user id to follow
     */
    public void addFollowing(String userId) {
        if (following == null) following = new HashMap<>();
        following.put(userId, true);
        followingCount = following.size();
    }

    /**
     * Remove a user from this user's following list
     *
     * @param userId the user id to unfollow
     */
    public void removeFollowing(String userId) {
        if (following != null && following.remove(userId) != null) {
            followingCount = following.size();
        }
    }

    // Existing friend request methods remain unchanged
    public void sendFriendRequest(String targetUserId) {
        if (!sentRequests.contains(targetUserId) && !friends.contains(targetUserId)) {
            sentRequests.add(targetUserId);
        }
    }

    public void receiveFriendRequest(String fromUserId) {
        if (!friendRequests.contains(fromUserId) && !friends.contains(fromUserId)) {
            friendRequests.add(fromUserId);
        }
    }

    public boolean acceptFriendRequest(String userId) {
        if (friendRequests.remove(userId)) {
            friends.add(userId);
            followerCount++;         // they follow you
            followingCount++;        // you follow them
            return true;
        }
        return false;
    }

    public void declineFriendRequest(String userId) {
        friendRequests.remove(userId);
    }

    public boolean isFriend(String userId) {
        return friends.contains(userId);
    }

    public boolean hasSentRequest(String userId) {
        return sentRequests.contains(userId);
    }

    public boolean hasIncomingRequest(String userId) {
        return friendRequests.contains(userId);
    }

    // Existing date calculation methods
    public int getDaysLeft() {
        return Math.max(0, 90 - timeHaveLicense);
    }

    public int getNightDaysLeft() {
        return Math.max(0, 180 - timeHaveLicense);
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
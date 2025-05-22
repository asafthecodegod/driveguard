package com.example.asaf_avisar.objects;

import java.util.Date;

/**
 * The type Teacher user.
 */
public class TeacherUser extends StudentUser {
    private int rank;
    private String bio;
    private int experience;
    private int price;
    private String location;
    private boolean available;
    private String teacherId; // Specific ID for teacher (separate from user ID)
    private String phone; // Teacher's phone number
    private boolean isTeacher;
    private String profilePhotoBase64; // Profile picture (Base64 or URL)

    /**
     * Instantiates a new Teacher user with minimal fields (for sample data).
     */
    public TeacherUser(String name, String etEmail, String etPassword, Date dpBirthday, int rank) {
        super(name, etEmail, etPassword, dpBirthday);
        this.rank = rank;
        this.experience = 0;
        this.price = 150; // Default price
        this.location = "";
        this.bio = "";
        this.available = true;
        this.teacherId = "T" + System.currentTimeMillis(); // Generate a default teacher ID
        this.phone = "";
        this.profilePhotoBase64 = ""; // Default empty profile picture
    }

    /**
     * Default constructor for Firebase.
     */
    public TeacherUser() {
        // Required empty constructor for Firebase
    }

    @Override
    public boolean isTeacher() {
        return isTeacher;
    }

    @Override
    public void setTeacher(boolean teacher) {
        isTeacher = teacher;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Gets profile picture (Base64 or URL).
     *
     * @return the profile picture string
     */
    public String getProfilePhotoBase64() {
        return profilePhotoBase64;
    }

    /**
     * Sets profile picture (Base64 or URL).
     *
     * @param profilePhotoBase64 the profile picture string
     */
    public void setProfilePhotoBase64(String profilePhotoBase64) {
        this.profilePhotoBase64 = profilePhotoBase64;
    }
}

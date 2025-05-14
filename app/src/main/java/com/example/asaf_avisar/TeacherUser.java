package com.example.asaf_avisar;

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

    /**
     * Gets rank.
     *
     * @return the rank
     */
    public int getRank() {
        return rank;
    }

    /**
     * Sets rank.
     *
     * @param rank the rank
     */
    public void setRank(int rank) {
        this.rank = rank;
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
     * Gets experience in years.
     *
     * @return the experience
     */
    public int getExperience() {
        return experience;
    }

    /**
     * Sets experience.
     *
     * @param experience the experience in years
     */
    public void setExperience(int experience) {
        this.experience = experience;
    }

    /**
     * Gets price per hour.
     *
     * @return the price
     */
    public int getPrice() {
        return price;
    }

    /**
     * Sets price.
     *
     * @param price the price per hour
     */
    public void setPrice(int price) {
        this.price = price;
    }

    /**
     * Gets location.
     *
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets location.
     *
     * @param location the location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Is teacher available.
     *
     * @return the availability status
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * Sets availability.
     *
     * @param available the availability status
     */
    public void setAvailable(boolean available) {
        this.available = available;
    }

    /**
     * Gets teacher ID.
     *
     * @return the teacher ID
     */
    public String getTeacherId() {
        return teacherId;
    }

    /**
     * Sets teacher ID.
     *
     * @param teacherId the teacher ID
     */
    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    /**
     * Gets phone number.
     *
     * @return the phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets phone number.
     *
     * @param phone the phone number
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }


}
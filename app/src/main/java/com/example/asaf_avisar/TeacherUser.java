package com.example.asaf_avisar;

import android.os.Build;

import java.util.Date;


/**
 * The type Teacher user.
 */
public class TeacherUser extends StudentUser  {
    private  int rank;

    /**
     * Instantiates a new Teacher user.
     *
     * @param name       the name
     * @param etEmail    the et email
     * @param etPassword the et password
     * @param dpBirthday the dp birthday
     * @param rank       the rank
     */
    public TeacherUser(String name, String etEmail, String etPassword, Date dpBirthday, int rank) {
        super(name, etEmail, etPassword, dpBirthday);
        this.rank = rank;
    }

    /**
     * Instantiates a new Teacher user.
     */
    public TeacherUser() {
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
}

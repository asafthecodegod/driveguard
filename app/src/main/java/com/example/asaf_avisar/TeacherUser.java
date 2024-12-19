package com.example.asaf_avisar;

import android.os.Build;

import java.util.Date;


public class TeacherUser extends StudentUser  {
    private  int rank;

    public TeacherUser(String name, String etEmail, String etPassword, Date dpBirthday, int rank) {
        super(name, etEmail, etPassword, dpBirthday);
        this.rank = rank;
    }

    public TeacherUser() {
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}

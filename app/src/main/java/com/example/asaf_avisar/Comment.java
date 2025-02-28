package com.example.asaf_avisar;

public class Comment {
    private String userId; // User who posted the comment
    private String text; // Comment text

    // Constructor
    public Comment(String userId, String text) {
        this.userId = userId;
        this.text = text;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

package com.example.asaf_avisar;

import com.example.asaf_avisar.activitys.Post;

import java.util.Date;
import java.util.List;

public class PostImage extends Post {

    private String photo;


    public PostImage(String photo) {
        this.photo = photo;
    }

    public PostImage(String userName, String description, int likesCount, String content, int type, String photo,String userPfp, Date date) {
        super(userName, description, likesCount, content, type,userPfp,date);
        type = 1;
        this.photo = photo;
    }

    public PostImage(String userId, String userName, String description, int likesCount, String content, List<String> likedByUsers,String userPfp, List<Comment> comments, int type, String photo,Date date) {
        super(userId, userName, description, likesCount, content, likedByUsers, comments,userPfp,date);
        type = 1;
        this.photo = photo;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}


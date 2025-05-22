package com.example.asaf_avisar.callbacks;

import com.example.asaf_avisar.objects.Post;

import java.util.ArrayList;

/**
 * The interface Firebase callback posts.
 */
public interface FirebaseCallbackPosts {
    /**
     * On callback posts.
     *
     * @param posts the posts
     */
    void onCallbackPosts(ArrayList<Post> posts); // New method for posts
}

package com.example.asaf_avisar;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.asaf_avisar.activitys.LoginOrRegistretionActivity;
import com.example.asaf_avisar.activitys.Post;
import com.example.asaf_avisar.activitys.menu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Fire base manager.
 */
public class FireBaseManager {
    private static FirebaseAuth mAuth;
    private static FirebaseDatabase database;
    private static DatabaseReference myRef;
    private Context context;

    /**
     * Instantiates a new Fire base manager.
     *
     * @param context the context
     */
    public FireBaseManager(Context context) {
        this.context = context;
    }

    /**
     * Gets auth.
     *
     * @return the auth
     */
// --- Firebase Authentication Helpers ---
    public static FirebaseAuth getmAuth() {
        if (mAuth == null) mAuth = FirebaseAuth.getInstance();
        return mAuth;
    }

    /**
     * Gets database.
     *
     * @return the database
     */
    public static FirebaseDatabase getDatabase() {
        if (database == null) database = FirebaseDatabase.getInstance();
        return database;
    }

    /**
     * Gets my ref.
     *
     * @param key the key
     * @return the my ref
     */
    public static DatabaseReference getMyRef(String key) {
        myRef = getDatabase().getReference(key);
        return myRef;
    }

    /**
     * Is connected boolean.
     *
     * @return the boolean
     */
    public boolean isConnected() {
        return getmAuth().getCurrentUser() != null;
    }

    /**
     * Logout.
     */
    public void logout() {
        getmAuth().signOut();
        context.startActivity(new Intent(context, LoginOrRegistretionActivity.class));
    }

    /**
     * Gets userid.
     *
     * @return the userid
     */
    public String getUserid() {
        return getmAuth().getCurrentUser().getUid();
    }

    /**
     * Create user.
     *
     * @param user the user
     * @param type the type
     */
// --- User Creation & Login ---
    public void createUser(StudentUser user, String type) {
        getmAuth().createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success " + task.getResult().getUser().getUid());
                            context.startActivity(new Intent(context, Addpfp.class));
                            getMyRef(type)
                                    .child(task.getResult().getUser().getUid())
                                    .setValue(user);
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(context, "" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Login user.
     *
     * @param email the email
     * @param pass  the pass
     */
    public void loginUser(String email, String pass) {
        getmAuth().signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "loginUser:success " + task.getResult().getUser().getUid());
                            context.startActivity(new Intent(context, menu.class));
                        } else {
                            Log.w(TAG, "loginUser:failure", task.getException());
                            Toast.makeText(context, "" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Read data.
     *
     * @param firebaseCallback the firebase callback
     * @param key              the key
     * @param id               the id
     */
// --- Data Reading ---
    public void readData(FirebaseCallback firebaseCallback, String key, String id) {
        getMyRef(key).child(id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        StudentUser user = snapshot.getValue(StudentUser.class);
                        firebaseCallback.oncallbackStudent(user);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });
    }

    /**
     * Student data.
     *
     * @param firebaseCallback the firebase callback
     */
    public void studentData(FirebaseCallback firebaseCallback) {
        ArrayList<StudentUser> students = new ArrayList<>();
        getMyRef("Student")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            StudentUser user = data.getValue(StudentUser.class);
                            user.setId(data.getKey());
                            students.add(user);
                        }
                        firebaseCallback.oncallbackArryStudent(students);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });
    }

    /**
     * Teacher data.
     *
     * @param firebaseCallback the firebase callback
     */
    public void teacherData(FirebaseCallback firebaseCallback) {
        ArrayList<TeacherUser> teachers = new ArrayList<>();
        getMyRef("Teacher")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            TeacherUser user = data.getValue(TeacherUser.class);
                            teachers.add(user);
                        }
                        firebaseCallback.onCallbackTeacher(teachers);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });
    }

    /**
     * Update user.
     *
     * @param studentUser the student user
     */
// --- Updates & Saves ---
    public void updateUser(StudentUser studentUser) {
        getMyRef("Student")
                .child(getUserid())
                .setValue(studentUser);
    }

    /**
     * Save image.
     *
     * @param profilePhotoBase64 the profile photo base 64
     */
    public void saveImage(String profilePhotoBase64) {
        getMyRef("Student")
                .child(getUserid())
                .child("profilePhotoBase64")
                .setValue(profilePhotoBase64);
    }

    /**
     * Save event.
     *
     * @param lesson the lesson
     */
    public void saveEvent(Lesson lesson) {
        getMyRef("Events")
                .child(getUserid())
                .push()
                .setValue(lesson);
    }

    /**
     * Gets event.
     *
     * @param firebaseCallback the firebase callback
     */
    public void getEvent(FirebaseCallbackLessons firebaseCallback) {
        ArrayList<Lesson> lessons = new ArrayList<>();
        getMyRef("Events")
                .child(getUserid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            lessons.add(data.getValue(Lesson.class));
                        }
                        firebaseCallback.oncallbackArryLessons(lessons);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });
    }

    /**
     * Save post.
     *
     * @param post the post
     */
    public void savePost(Post post) {
        String userId = getUserid();
        post.setUserId(userId);
        getMyRef("Posts")
                .push()
                .setValue(post)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Post saved successfully.");
                        } else {
                            Log.e(TAG, "Failed to save post.", task.getException());
                        }
                    }
                });
    }

    /**
     * Delete post.
     *
     * @param postKey the post key
     */
    public void deletePost(String postKey) {
        getMyRef("Posts")
                .child(postKey)
                .removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Post deleted successfully.");
                    } else {
                        Log.e(TAG, "Failed to delete post.", task.getException());
                    }
                });
    }


    /**
     * Read posts.
     *
     * @param firebaseCallbackPosts the firebase callback posts
     */
    public void readPosts(FirebaseCallbackPosts firebaseCallbackPosts) {
        ArrayList<Post> posts = new ArrayList<>();

        getMyRef("Posts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) { // ✅ Loop through each post
                    try {
                        Post post = postSnapshot.getValue(Post.class);
                        if (post != null) {
                            post.setKey(postSnapshot.getKey()); // ✅ Store Firebase key
                            posts.add(post);
                        }
                    } catch (Exception e) {
                        Log.e("Firebase", "Error parsing post: " + e.getMessage());
                    }
                    firebaseCallbackPosts.onCallbackPosts(posts); // ✅ Send the list
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });
    }

    /**
     * Update post likes.
     *
     * @param post    the post
     * @param postKey the post key
     */
    public void updatePostLikes(Post post, String postKey) {
        DatabaseReference postRef = getMyRef("Posts").child(postKey);
        postRef.child("likesCount").setValue(post.getLikesCount());
        postRef.child("likedByUsers").setValue(post.getLikedByUsers());
    }

    /**
     * Update post comments.
     *
     * @param post     the post
     * @param comments the comments
     */
    public void updatePostComments(Post post, List<Comment> comments) {
        getMyRef("Posts").child(post.getKey()).child("comments").setValue(comments);
    }

    /**
     * Update comment replies.
     *
     * @param parentComment the parent comment
     */
    public void updateCommentReplies(Comment parentComment) {
        getMyRef("Posts")
                .child(parentComment.getPostId())
                .child("comments")
                .child(parentComment.getKey())
                .setValue(parentComment);
    }

    /**
     * Update comment likes.
     *
     * @param comment the comment
     * @param key     the key
     */
    public void updateCommentLikes(Comment comment, String key) {
        if (comment.getPostId() == null || key == null) return;
        getMyRef("Posts").child(comment.getPostId()).child("comments").child(key)
                .child("likesCount").setValue(comment.getLikesCount());
        getMyRef("Posts").child(comment.getPostId()).child("comments").child(key)
                .child("likedByUsers").setValue(comment.getLikedByUsers());
    }

    /**
     * Read posts for user.
     *
     * @param callback     the callback
     * @param targetUserId the target user id
     */
    public void readPostsForUser(FirebaseCallbackPosts callback, String targetUserId) {
        ArrayList<Post> userPosts = new ArrayList<>();

        // 1) get the same “Posts” reference you debugged
        Query q = FirebaseDatabase
                .getInstance()
                .getReference("Posts")
                .orderByChild("userId")
                .equalTo(targetUserId);

        // 2) single-shot listener
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // 3) collect all matching posts
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    try {
                        Post p = postSnapshot.getValue(Post.class);
                        if (p != null) {
                            p.setKey(postSnapshot.getKey());
                            userPosts.add(p);
                        }
                    } catch (Exception ex) {
                        Log.e("Firebase", "Error parsing post: " + ex.getMessage());
                    }
                }
                // 4) deliver the list once
                callback.onCallbackPosts(userPosts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Firebase", "readPostsForUser:onCancelled", error.toException());
            }
        });
    }



    // --- Friend Request Flow ---
    // ... (שאר הקוד ללא שינוי)


}

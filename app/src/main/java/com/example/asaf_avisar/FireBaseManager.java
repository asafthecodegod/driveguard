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

public class FireBaseManager {
    private static FirebaseAuth mAuth;
    private static FirebaseDatabase database;
    private static DatabaseReference myRef;
    private Context context;

    public FireBaseManager(Context context) {
        this.context = context;
    }

    // --- Firebase Authentication Helpers ---
    public static FirebaseAuth getmAuth() {
        if (mAuth == null) mAuth = FirebaseAuth.getInstance();
        return mAuth;
    }

    public static FirebaseDatabase getDatabase() {
        if (database == null) database = FirebaseDatabase.getInstance();
        return database;
    }

    public static DatabaseReference getMyRef(String key) {
        myRef = getDatabase().getReference(key);
        return myRef;
    }

    public boolean isConnected() {
        return getmAuth().getCurrentUser() != null;
    }

    public void logout() {
        getmAuth().signOut();
        context.startActivity(new Intent(context, LoginOrRegistretionActivity.class));
    }

    public String getUserid() {
        return getmAuth().getCurrentUser().getUid();
    }

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

    // --- Updates & Saves ---
    public void updateUser(StudentUser studentUser) {
        getMyRef("Student")
                .child(getUserid())
                .setValue(studentUser);
    }

    public void saveImage(String profilePhotoBase64) {
        getMyRef("Student")
                .child(getUserid())
                .child("profilePhotoBase64")
                .setValue(profilePhotoBase64);
    }

    public void saveEvent(Lesson lesson) {
        getMyRef("Events")
                .child(getUserid())
                .push()
                .setValue(lesson);
    }

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

    public void updatePostLikes(Post post, String postKey) {
        DatabaseReference postRef = getMyRef("Posts").child(postKey);
        postRef.child("likesCount").setValue(post.getLikesCount());
        postRef.child("likedByUsers").setValue(post.getLikedByUsers());
    }

    public void updatePostComments(Post post, List<Comment> comments) {
        getMyRef("Posts").child(post.getKey()).child("comments").setValue(comments);
    }

    public void updateCommentReplies(Comment parentComment) {
        getMyRef("Posts")
                .child(parentComment.getPostId())
                .child("comments")
                .child(parentComment.getKey())
                .setValue(parentComment);
    }

    public void updateCommentLikes(Comment comment, String key) {
        if (comment.getPostId() == null || key == null) return;
        getMyRef("Posts").child(comment.getPostId()).child("comments").child(key)
                .child("likesCount").setValue(comment.getLikesCount());
        getMyRef("Posts").child(comment.getPostId()).child("comments").child(key)
                .child("likedByUsers").setValue(comment.getLikedByUsers());
    }
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

package com.example.asaf_avisar;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FireBaseManager {
    private static FirebaseAuth mAuth;
    private static FirebaseDatabase database;
    public static DatabaseReference  myRef;
    private Context context;

    public FireBaseManager(Context context) {
        this.context = context;
    }

    public static FirebaseAuth getmAuth(){
        if(mAuth == null)
            mAuth = FirebaseAuth.getInstance();
        return mAuth;
    }

    public static FirebaseDatabase getDatabase(){
        if(database == null)
            database = FirebaseDatabase.getInstance();
        return database;

    }public static DatabaseReference getMyRef(String key){
            myRef = getDatabase().getReference(key);
        return myRef;

    }

    public boolean isConnecet(){
        if(getmAuth().getCurrentUser() != null)
        {
            return true;
        }
        return false;
    }

    public void logout (){

        getmAuth().signOut();
        context.startActivity(new Intent(context, LoginOrRegistretionActivity.class));

    }
    public String getUserid(){
        return getmAuth().getCurrentUser().getUid();
    }


    public void createUser(StudentUser user,String type){
        getmAuth().createUserWithEmailAndPassword(user.getEtEmail(), user.getEtPassword())
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            context.startActivity(new Intent(context, Addpfp.class));
                            //todo
                            Log.d(TAG, "createUserWithEmail:success"+ task.getResult().getUser().getUid());
                            getMyRef(type).child(task.getResult().getUser().getUid()).setValue(user);


                        } else {
                            Toast.makeText(context,"" +task.getException(),Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "failure"+task.getException().getMessage().toString());
                            //Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                            //        Toast.LENGTH_SHORT).show();
                        }
                    }

                });
    }

    public void loginUser(String email,String pass) {
        getmAuth().signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    context.startActivity((new Intent(context, menu.class)));

                    Log.w("TAG", "createUserWithEmail:success" + task.getResult().getUser().getUid());
                } else {
                    Toast.makeText(context, "" + task.getException(), Toast.LENGTH_SHORT).show();
                    Log.w("TAG", "createUserWithEmail:failure", task.getException());
                }
            }
        });
    }
    public void readData(FirebaseCallback firebaseCallback,String key, String id)
    {
        getMyRef(key).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                StudentUser user=snapshot.getValue(StudentUser.class);
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
        getMyRef("Student").addListenerForSingleValueEvent(new ValueEventListener() {

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

    public void teacherData(FirebaseCallback firebaseCallback)
    {
        ArrayList<TeacherUser> teachers= new ArrayList<>();
        getMyRef("Teacher").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data: snapshot.getChildren())
                {
                    TeacherUser user =data.getValue(TeacherUser.class);
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

    public void UpdateUser(StudentUser studentUser) {
        getMyRef("Student").child(getmAuth().getCurrentUser().getUid()).setValue(studentUser);
    }

    public void saveImage(String profilePhotoBase64) {
        getMyRef("Student").child(getmAuth().getCurrentUser().getUid()).child("profilePhotoBase64").setValue(profilePhotoBase64);
    }

    public void saveEvent(Lesson lesson) {
        getMyRef("Events").child(getmAuth().getCurrentUser().getUid()).push().setValue(lesson);

    }



    public void getEvent(FirebaseCallbackLessons firebaseCallback) {
        ArrayList<Lesson> lessons = new ArrayList<>();
        getMyRef("Events").child(getmAuth().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Lesson lesson = data.getValue(Lesson.class);
                    lessons.add(lesson);
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
        String userId = getmAuth().getCurrentUser().getUid();
        post.setUserId(userId);
        // Save the post under the 'Posts' node
        getMyRef("Posts").push().setValue(post)
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
                }
                firebaseCallbackPosts.onCallbackPosts(posts); // ✅ Send the list
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Firebase", "Failed to read posts.", error.toException());
            }
        });
    }
    //public void uploadImage(Uri imageUri, OnImageUploadCallback callback) {
    //    if (imageUri == null) {
    //        callback.onUploadFailed("Image URI is null");
    //        return;
     //   }

        //String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //StorageReference storageRef = FirebaseStorage.getInstance().getReference("PostImages")
       //         .child(userId + "/" + System.currentTimeMillis() + ".jpg");

        //storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
        //        storageRef.getDownloadUrl().addOnSuccessListener(uri -> callback.onUploadSuccess(uri.toString()))
        //).addOnFailureListener(e -> callback.onUploadFailed(e.getMessage()));

    public void updatePostLikes(Post post, String postKey) {
        DatabaseReference postRef = getMyRef("Posts").child(postKey);

        postRef.child("likesCount").setValue(post.getLikesCount());
        postRef.child("likedByUsers").setValue(post.getLikedByUsers()); // ✅ Store liked users
    }
//    public void updatePostLikes(Post post, String postKey) {
//        DatabaseReference postRef = getMyRef("Posts").child(postKey);
//
//        // ✅ Ensure `likedByUsers` is not null before updating Firebase
//        if (post.getLikedByUsers() == null) {
//            post.setLikedByUsers(new ArrayList<>());
//        }
//
//        Map<String, Object> updates = new HashMap<>();
//        updates.put("likesCount", post.getLikesCount());
//        updates.put("likedByUsers", post.getLikedByUsers()); // ✅ Store liked users
//
//        postRef.updateChildren(updates);
//    }


    public void updatePostComments(Post post, List<Comment> comments) {
        // Update the comments list for the post in Firebase
        // Again, replace "postKey" with your actual key
        getMyRef("Posts").child(post.getKey()).child("comments").setValue(comments);
    }


    public void updateCommentReplies(Comment parentComment) {
        String postKey = "POST_KEY_HERE"; // Replace with actual post ID
        DatabaseReference postRef = getMyRef("Posts").child(postKey).child("comments");
        postRef.child(parentComment.getUserId()).setValue(parentComment);
    }

    public void updateCommentLikes(Comment comment, String key) {
        // Check if postId or key is null before proceeding
        if (comment.getPostId() == null || comment.getKey() == null) {
            Log.e("updateCommentLikes", "Post ID or Comment Key is null.");
            return;
        }

        DatabaseReference commentRef = getMyRef("Posts").child(comment.getPostId()).child("comments").child(comment.getKey());

        // Update likes count and liked by users
        commentRef.child("likesCount").setValue(comment.getLikesCount());
        commentRef.child("likedByUsers").setValue(comment.getLikedByUsers());
    }

}



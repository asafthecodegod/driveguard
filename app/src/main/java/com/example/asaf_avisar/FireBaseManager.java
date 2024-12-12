package com.example.asaf_avisar;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.asaf_avisar.activitys.AssessmentActivity;
import com.example.asaf_avisar.activitys.LoginOrRegistretionActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    }public static DatabaseReference getMyRef(){
        if(myRef == null)
            myRef = getDatabase().getReference("Student");
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


    public void createUser(StudentUser user){
        getmAuth().createUserWithEmailAndPassword(user.getEtEmail(), user.getEtPassword())
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            context.startActivity(new Intent(context, AssessmentActivity.class));
                            //todo
                            Log.d(TAG, "createUserWithEmail:success"+ task.getResult().getUser().getUid());
                            getMyRef().child(task.getResult().getUser().getUid()).setValue(user);


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
                    context.startActivity((new Intent(context, AssessmentActivity.class)));

                    Log.w("TAG", "createUserWithEmail:success" + task.getResult().getUser().getUid());
                } else {
                    Toast.makeText(context, "" + task.getException(), Toast.LENGTH_SHORT).show();
                    Log.w("TAG", "createUserWithEmail:failure", task.getException());
                }
            }
        });
    }
    public void readData(FirebaseCallback firebaseCallback)
    {
        getMyRef().child(getmAuth().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
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

    public void UpdateUser(StudentUser studentUser) {
        getMyRef().child(getmAuth().getCurrentUser().getUid()).setValue(studentUser);
    }
}


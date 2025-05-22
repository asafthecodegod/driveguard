package com.example.asaf_avisar.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asaf_avisar.R;
import com.example.asaf_avisar.fragments.ProfileFragment;
import com.example.asaf_avisar.objects.ImageUtils;
import com.example.asaf_avisar.objects.StudentUser;

import java.util.ArrayList;

/**
 * The type Student user adapter.
 */
public class StudentUserAdapter extends RecyclerView.Adapter<StudentUserAdapter.ViewStudentUser> {
    private ArrayList<StudentUser> studentUsers;
    private Context context;

    /**
     * Instantiates a new Student user adapter.
     *
     * @param context      the context
     * @param studentUsers the student users
     */
    public StudentUserAdapter(Context context, ArrayList<StudentUser> studentUsers) {
        this.context = context;
        this.studentUsers = studentUsers;
    }

    @NonNull
    @Override
    public ViewStudentUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View studentView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_teacher, parent, false);
        return new ViewStudentUser(studentView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewStudentUser holder, int position) {
        StudentUser currentStudentUser = studentUsers.get(position);

        // Set data from the current StudentUser object
        holder.nameTextView.setText(currentStudentUser.getName());


        //holder.emailTextView.setText(currentStudentUser.getEtEmail());
        // Optional: Set profile picture if available
        // Uncomment if you handle profile image logic
        if(currentStudentUser.getProfilePhotoBase64() != null)
        {
            holder.profileImageView.setImageBitmap(ImageUtils.convert64base(currentStudentUser.getProfilePhotoBase64()));
        }
        else
        {
            holder.profileImageView.setImageResource(R.drawable.ic_default_profile);

        }



        // Handle item clicks to open the selected student's profile
        holder.itemView.setOnClickListener(v -> {
            // Pass the user ID to the ProfileFragment
            Bundle bundle = new Bundle();
            bundle.putString("userId", currentStudentUser.getId());

            // Create a new ProfileFragment instance and set the arguments
            ProfileFragment profileFragment = new ProfileFragment();
            profileFragment.setArguments(bundle);

            // Get the FragmentManager and start the transaction to replace the current fragment
            FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, profileFragment);  // Replace with your container ID
            transaction.addToBackStack(null);  // Optional: add the transaction to the back stack so the user can navigate back
            transaction.commit();
        });
    }

    @Override
    public int getItemCount() {
        return studentUsers.size();
    }

    /**
     * The type View student user.
     */
    public static class ViewStudentUser extends RecyclerView.ViewHolder {
        /**
         * The Name text view.
         */
        public TextView nameTextView;
        /**
         * The Email text view.
         */
        public TextView emailTextView;
        /**
         * The Rating bar.
         */
        public RatingBar ratingBar;
        /**
         * The Profile image view.
         */
        public ImageView profileImageView;
        /**
         * The Type.
         */
        public TextView type;

        /**
         * Instantiates a new View student user.
         *
         * @param itemView the item view
         */
        public ViewStudentUser(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.teacher_name);
            //  emailTextView = itemView.findViewById(R.id.userEmail);
            profileImageView = itemView.findViewById(R.id.teacher_icon);


            ratingBar = itemView.findViewById(R.id.ratingBar);
            ratingBar.setVisibility(View.GONE);
            type = itemView.findViewById(R.id.type);
            type.setVisibility(View.GONE);


        }
    }
}
package com.example.asaf_avisar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * The type Teacher user adapter.
 */
public class TeacherUserAdapter extends RecyclerView.Adapter<TeacherUserAdapter.ViewTeacherUser> {
    /**
     * The Teacher users.
     */
    ArrayList<TeacherUser> teacherUsers;


    /**
     * Instantiates a new Teacher user adapter.
     *
     * @param teacherUsers the teacher users
     */
    public TeacherUserAdapter(ArrayList<TeacherUser> teacherUsers) {
        this.teacherUsers = teacherUsers;
    }




    @NonNull
    @Override
    public ViewTeacherUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View teacherview = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_teacher, parent,false );
        return new ViewTeacherUser(teacherview);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewTeacherUser holder, int position) {
        TeacherUser currentTeacherUser =teacherUsers.get(position);
        holder.nameTextView.setText(String.valueOf(currentTeacherUser.getName()));
        holder.typeTextView.setText(String.valueOf(currentTeacherUser.getRank()));
        holder.ratingBar.setRating(currentTeacherUser.getRank());
//        holder.iconimageView.setImageResource(
//                holder.iconimageView.getResources().getIdentifier(String.valueOf(currentTeacherUser.getProfileImage()),
//                        "drawable",
//                        holder.nameTextView.getContext().getPackageName())
//
//        );

    }

    @Override
    public int getItemCount() {
        return teacherUsers.size();
    }

    /**
     * The type View teacher user.
     */
    public static class ViewTeacherUser extends RecyclerView.ViewHolder {

        /**
         * The Name text view.
         */
        public TextView nameTextView;
        /**
         * The Type text view.
         */
        public TextView typeTextView;
        /**
         * The Rating bar.
         */
        public RatingBar ratingBar;
        /**
         * The Iconimage view.
         */
        public ImageView iconimageView;


        /**
         * Instantiates a new View teacher user.
         *
         * @param itemView the item view
         */
        public ViewTeacherUser(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.teacher_name);
            typeTextView =itemView.findViewById(R.id.type);
            ratingBar =itemView.findViewById(R.id.ratingBar);
            iconimageView =itemView.findViewById(R.id.teacher_icon);


        }
    }

}

package com.example.asaf_avisar.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asaf_avisar.R;
import com.example.asaf_avisar.objects.TeacherUser;

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
     * Interface for handling teacher item clicks
     */
    public interface OnTeacherClickListener {
        void onTeacherClick(TeacherUser teacher, int position);
    }

    private OnTeacherClickListener listener;

    /**
     * Instantiates a new Teacher user adapter.
     *
     * @param teacherUsers the teacher users
     * @param listener the click listener
     */
    public TeacherUserAdapter(ArrayList<TeacherUser> teacherUsers, OnTeacherClickListener listener) {
        this.teacherUsers = teacherUsers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewTeacherUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View teacherview = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_teacher, parent, false);
        return new ViewTeacherUser(teacherview);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewTeacherUser holder, int position) {
        TeacherUser currentTeacherUser = teacherUsers.get(position);
        holder.nameTextView.setText(String.valueOf(currentTeacherUser.getName()));
        holder.typeTextView.setText(String.valueOf(currentTeacherUser.getRank()));
        holder.ratingBar.setRating(currentTeacherUser.getRank());
//        holder.iconimageView.setImageResource(
//                holder.iconimageView.getResources().getIdentifier(String.valueOf(currentTeacherUser.getProfileImage()),
//                        "drawable",
//                        holder.nameTextView.getContext().getPackageName())
//
//        );

        // Set click listener for the entire item view
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTeacherClick(currentTeacherUser, position);
            }
        });
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
            typeTextView = itemView.findViewById(R.id.type);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            iconimageView = itemView.findViewById(R.id.teacher_icon);
        }
    }
}
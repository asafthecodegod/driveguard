package com.example.asaf_avisar.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asaf_avisar.FireBaseManager;
import com.example.asaf_avisar.R;
import com.example.asaf_avisar.objects.Lesson;

import java.util.List;

/**
 * The type Lesson adapter.
 */
public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder> {

    private Context context;
    private List<Lesson> lessonList;
    private FireBaseManager fireBaseManager;

    /**
     * Instantiates a new Lesson adapter.
     *
     * @param context    the context
     * @param lessonList the lesson list
     */
    public LessonAdapter(Context context, List<Lesson> lessonList) {
        this.context = context;
        this.lessonList = lessonList;
        this.fireBaseManager = new FireBaseManager(context);
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.lesson_item, parent, false);
        return new LessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        Lesson lesson = lessonList.get(position);
        holder.eventName.setText(lesson.getLessonType());
        holder.eventDate.setText("Date: " + lesson.getDate());
        holder.eventTime.setText("Time: " + lesson.getTime());
        holder.paidCheckbox.setChecked(lesson.isPaid());

        // Handle checkbox changes
        holder.paidCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> lesson.setPaid(isChecked));

        // Handle item click for deletion
        holder.itemView.setOnClickListener(v -> showDeleteConfirmation(position));
    }

    @Override
    public int getItemCount() {
        return lessonList.size();
    }

    private void showDeleteConfirmation(int position) {
        Lesson lesson = lessonList.get(position);
        new AlertDialog.Builder(context)
                .setTitle("Delete Lesson")
                .setMessage("Are you sure you want to delete this lesson?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Delete from Firebase first
                    if (lesson.getLessonId() != null) {
                        fireBaseManager.deleteEvent(lesson.getLessonId());
                        // Only remove from local list after successful deletion
                        lessonList.remove(position);
                        notifyItemRemoved(position);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    /**
     * The type Lesson view holder.
     */
    public static class LessonViewHolder extends RecyclerView.ViewHolder {
        /**
         * The Event name.
         */
        TextView eventName, /**
         * The Event date.
         */
        eventDate, /**
         * The Event time.
         */
        eventTime;
        /**
         * The Paid checkbox.
         */
        CheckBox paidCheckbox;

        /**
         * Instantiates a new Lesson view holder.
         *
         * @param itemView the item view
         */
        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.text_event_name);
            eventDate = itemView.findViewById(R.id.text_event_date);
            eventTime = itemView.findViewById(R.id.text_event_time);
            paidCheckbox = itemView.findViewById(R.id.checkbox_paid);
        }
    }
}

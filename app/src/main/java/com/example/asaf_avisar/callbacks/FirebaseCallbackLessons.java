package com.example.asaf_avisar.callbacks;

import com.example.asaf_avisar.objects.Lesson;

import java.util.ArrayList;

/**
 * The interface Firebase callback lessons.
 */
public interface FirebaseCallbackLessons {
    /**
     * Oncallback arry lessons.
     *
     * @param lessons the lessons
     */
    void oncallbackArryLessons(ArrayList<Lesson> lessons);

}

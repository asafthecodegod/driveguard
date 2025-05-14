package com.example.asaf_avisar;

import java.util.ArrayList;

/**
 * The interface Firebase callback.
 */
public interface FirebaseCallback
{
    /**
     * Oncallback arry student.
     *
     * @param students the students
     */
    void oncallbackArryStudent(ArrayList<StudentUser> students);

    /**
     * Oncallback student.
     *
     * @param student the student
     */
    void oncallbackStudent(StudentUser student);

    /**
     * On callback teacher.
     *
     * @param teachers the teachers
     */
    void onCallbackTeacher(ArrayList<TeacherUser> teachers);

    void onCallbackSingleTeacher(TeacherUser teacher);

}

package com.example.asaf_avisar;

import java.util.ArrayList;

public interface FirebaseCallback
{
    void oncallbackArryStudent(ArrayList<StudentUser> students);
    void oncallbackStudent(StudentUser student);
    void onCallbackTeacher(ArrayList<TeacherUser> teachers);

}

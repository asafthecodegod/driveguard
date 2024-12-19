package com.example.asaf_avisar;

import java.util.ArrayList;

public interface FirebaseCallback
{
    void oncallbackStudent(StudentUser user);
    void onCallbackTeacher(ArrayList<TeacherUser> teachers);

}

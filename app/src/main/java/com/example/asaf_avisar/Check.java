package com.example.asaf_avisar;

import android.os.Build;
import android.util.Patterns;
import android.widget.DatePicker;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Check {

    public boolean checkName(String name)
    {
        if (!name.isEmpty())
            return true ;
        return false;
    }
    public boolean checkEmail(String email)
    {
        if (!email.isEmpty()&& Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return true;
        return  false;
    }
    public boolean checkPass (String password)
    {
        if(!password.isEmpty()&&password.length()>=6)
            return  true;
        return false;
    }


}

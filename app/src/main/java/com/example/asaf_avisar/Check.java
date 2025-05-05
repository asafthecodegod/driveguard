package com.example.asaf_avisar;

import android.os.Build;
import android.util.Patterns;
import android.widget.DatePicker;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * The type Check.
 */
public class Check {

    /**
     * Check name boolean.
     *
     * @param name the name
     * @return the boolean
     */
    public boolean checkName(String name)
    {
        if (!name.isEmpty())
            return true ;
        return false;
    }

    /**
     * Check email boolean.
     *
     * @param email the email
     * @return the boolean
     */
    public boolean checkEmail(String email)
    {
        if (!email.isEmpty()&& Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return true;
        return  false;
    }

    /**
     * Check pass boolean.
     *
     * @param password the password
     * @return the boolean
     */
    public boolean checkPass (String password)
    {
        if(!password.isEmpty()&&password.length()>=6)
            return  true;
        return false;
    }


}

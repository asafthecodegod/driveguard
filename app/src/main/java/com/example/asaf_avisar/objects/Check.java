package com.example.asaf_avisar.objects;

import android.util.Patterns;

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
    public boolean checkPhone(String phone) {
        return phone != null
                && phone.matches("\\d{10}");
    }

}

package com.example.asaf_avisar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.Date;

public class StudentUser {
    protected String name;
    protected String etEmail,etPassword;
    protected Date dpBirthday;
    protected boolean license;
    protected boolean greenform;
    protected int type;
    protected boolean theory;
    protected Date licenseDate;
    protected String city;
    protected String id;
    protected int lessonCounter;
    protected String profilePhotoBase64;

    public StudentUser(Date licenseDate, boolean theory, int type, boolean greenform, boolean license,String city) {
        this.licenseDate = licenseDate;
        this.theory = theory;
        this.type = type;
        this.greenform = greenform;
        this.license = license;
        this.city =city;
    }

    public StudentUser() {

    }
    public StudentUser(String name, String etEmail, String etPassword, Date dpBirthday) {
        this.name = name;
        this.etEmail = etEmail;
        this.etPassword = etPassword;
        this.dpBirthday = dpBirthday;

    }


    public String getProfilePhotoBase64() {
        return profilePhotoBase64;
    }

    public void setProfilePhotoBase64(String profilePhotoBase64) {
        this.profilePhotoBase64 = profilePhotoBase64;
    }

    // Encode a Bitmap to Base64
    public  String convertTo64Base(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    // Decode a Base64 String to Bitmap
    public static   Bitmap convert64BaseToBitmap(String base64Code) {
        byte[] decodedString = Base64.decode(base64Code, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    public int getLessonCounter() {
        return lessonCounter;
    }

    public void setLessonCounter(int lessonCounter) {
        this.lessonCounter = lessonCounter;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEtEmail() {
        return etEmail;
    }

    public void setEtEmail(String etEmail) {
        this.etEmail = etEmail;
    }

    public String getEtPassword() {
        return etPassword;
    }

    public void setEtPassword(String etPassword) {
        this.etPassword = etPassword;
    }

    public Date getDpBirthday() {
        return dpBirthday;
    }

    public void setDpBirthday(Date dpBirthday) {
        this.dpBirthday = dpBirthday;
    }

    public boolean isLicense() {
        return license;
    }

    public void setLicense(boolean license) {
        this.license = license;
    }

    public boolean isGreenform() {
        return greenform;
    }

    public void setGreenform(boolean greenform) {
        this.greenform = greenform;
    }

    public int isType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isTheory() {
        return theory;
    }

    public void setTheory(boolean theory) {
        this.theory = theory;
    }

    public Date getLicenseDate() {
        return licenseDate;
    }

    public void setLicenseDate(Date licenseDate) {
        this.licenseDate = licenseDate;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}

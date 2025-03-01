package com.example.asaf_avisar;

public interface OnImageUploadCallback {
    void onUploadSuccess(String imageUrl);
    void onUploadFailed(String errorMessage);
}
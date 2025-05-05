package com.example.asaf_avisar;

/**
 * The interface On image upload callback.
 */
public interface OnImageUploadCallback {
    /**
     * On upload success.
     *
     * @param imageUrl the image url
     */
    void onUploadSuccess(String imageUrl);

    /**
     * On upload failed.
     *
     * @param errorMessage the error message
     */
    void onUploadFailed(String errorMessage);
}
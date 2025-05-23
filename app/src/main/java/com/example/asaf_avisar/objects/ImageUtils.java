package com.example.asaf_avisar.objects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * The type Image utils.
 */
public class ImageUtils {


    /**
     * Convert to 64 base string.
     *
     * @param bitmap the bitmap
     * @return the string
     */
    public static String convertTo64Base(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(data, Base64.DEFAULT);
    }


    /**
     * Convert 64 base bitmap.
     *
     * @param base64String the base 64 string
     * @return the bitmap
     */
// Convert Base64 to Bitmap
    public static Bitmap convert64base(String base64String) {
        if (base64String == null) return null;
        try {
            byte[] decodedString = android.util.Base64.decode(base64String, android.util.Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null; // Return null if the Base64 decoding fails
        }
    }

    /**
     * Rotate image if required bitmap.
     *
     * @param img       the img
     * @param imagePath the image path
     * @return the bitmap
     */
    public static Bitmap rotateImageIfRequired(Bitmap img, String imagePath) {
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int rotationAngle = 0;

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotationAngle = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotationAngle = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotationAngle = 270;
                    break;
            }

            if (rotationAngle != 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate(rotationAngle);
                return Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return img;
    }


}

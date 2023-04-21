package edu.northeastern.numad23sp_team7.huskymarket.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageCodec {

    public static String getEncodedImage(Bitmap bitmap) {
        int width = 800;
        int height = bitmap.getHeight() * width / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public static String getEncodedSmallImage(Bitmap bitmap) {
        int width = 300;
        int height = bitmap.getHeight() * width / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }


    public static Bitmap getDecodedImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * Given an imageUri, convert the imageUri to its encoded bitmap string
     * @param context Activity Context
     * @param uri image Uri
     * @return encoded image string
     */
    public static String getEncodedImageFromUri(Context context, Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getEncodedImage(bitmap);
    }

    public static Bitmap rotateImage(Bitmap bitmap, Uri imageUri, Context context) {
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(context.getContentResolver().openInputStream(imageUri));
        } catch (IOException e) {
            e.printStackTrace();
        }

        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(270);
                break;
            default:
                return bitmap;
        }

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

}

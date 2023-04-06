package edu.northeastern.numad23sp_team7.huskymarket.utils;

import static android.app.Activity.RESULT_OK;
import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/*
 Cannot use this class for now
 */
public class ImageSelector {


    private final ActivityResultLauncher<Intent> selectImageFromFile;
    private String encodedImage ;


    public ImageSelector(ComponentActivity activity) {
        selectImageFromFile = activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if (result.getData() != null) {
                            Uri profileImageUri = result.getData().getData();
                            try {
                                InputStream inputStream = activity.getContentResolver().openInputStream(profileImageUri);
                                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                                encodedImage = encodeImage(bitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        );
    }


    public void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // user grant PERMISSION，后面可以改
        selectImageFromFile.launch(intent);
    }

    public String encodeImage(Bitmap bitmap) { // profile image
        int width = 150;
        int height = bitmap.getHeight() * width / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream); // jpeg to png
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public String getEncodedImage() {
        return encodedImage;
    }
}

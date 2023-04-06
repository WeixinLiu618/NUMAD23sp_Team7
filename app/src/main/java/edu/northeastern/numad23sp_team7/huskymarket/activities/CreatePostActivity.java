package edu.northeastern.numad23sp_team7.huskymarket.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.Manifest;

import com.squareup.picasso.Picasso;

import edu.northeastern.numad23sp_team7.R;

public class CreatePostActivity extends AppCompatActivity {

    // Define the location and category options
    private final String[] LOCATION_OPTIONS = {"Boston", "Seattle", "San Jose", "Vancouver"};
    private final String[] CATEGORY_OPTIONS = {"Electronics", "Furniture", "Clothing", "Books", "Stationary"};
    private static final int REQUEST_PERMISSIONS = 100;
    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText title;
    private EditText description;
    private NumberPicker condition;
    private ImageView imageUploadClick;
    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        imageUploadClick = findViewById(R.id.image_view_post);
        title = findViewById(R.id.edit_text_title);
        description = findViewById(R.id.edit_text_description);
        condition = findViewById(R.id.edit_text_condition);
        condition.setMinValue(10);
        condition.setMaxValue(100);
        condition.setWrapSelectorWheel(false);
        condition.setFormatter(value -> String.format("%.1f", value/10.0));
        condition.setValue(100);
        
        // Create the location spinner and set its options
        Spinner locationSpinner = findViewById(R.id.spinner_location);
        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, LOCATION_OPTIONS);
        locationSpinner.setAdapter(locationAdapter);
        // default location
        locationSpinner.setSelection(0);

        // Create the category spinner and set its options
        Spinner categorySpinner = findViewById(R.id.spinner_category);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, CATEGORY_OPTIONS);
        categorySpinner.setAdapter(categoryAdapter);
        // default category
        categorySpinner.setSelection(0);
    }

    public void onClickMyImageView(View view) {
        requestPermission();
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSIONS);
        } else {
            openGallery();
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            Picasso.get().load(uri).into(imageUploadClick);
        }
    }
}
package edu.northeastern.numad23sp_team7.huskymarket.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import edu.northeastern.numad23sp_team7.R;

public class CreatePostActivity extends AppCompatActivity {

    // Define the location and category options
    private final String[] LOCATION_OPTIONS = {"Boston", "Seattle", "San Francisco"};
    private final String[] CATEGORY_OPTIONS = {"Electronics", "Furniture"};

    private EditText title;
    private EditText description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        title = findViewById(R.id.edit_text_title);
        description = findViewById(R.id.edit_text_description);

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
}
package edu.northeastern.numad23sp_team7.huskymarket.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

import edu.northeastern.numad23sp_team7.R;
import edu.northeastern.numad23sp_team7.huskymarket.database.ProductDao;
import edu.northeastern.numad23sp_team7.huskymarket.model.Product;
import edu.northeastern.numad23sp_team7.huskymarket.utils.Constants;
import edu.northeastern.numad23sp_team7.huskymarket.utils.PreferenceManager;

public class CreatePostActivity extends AppCompatActivity {

    // Define the location and category options
    private final String[] LOCATION_OPTIONS = {"Boston", "Seattle", "San Jose", "Vancouver"};
    private final String[] CATEGORY_OPTIONS = {"Electronics", "Furniture", "Clothing", "Books", "Stationary"};
    private static final int CAMERA_PERMISSION_CODE = 1;
    private static final int STORAGE_PERMISSION_CODE = 2;
    private static final int REQUEST_GALLERY = 1;
    private static final int REQUEST_CAMERA = 2;
    private PreferenceManager preferenceManager;
    private EditText title;
    private EditText description;
    private EditText price;
    private NumberPicker condition;
    private ImageView imageUploadClick;
    private Spinner locationSpinner;
    private Spinner categorySpinner;
    private ImageView sendButton;
    private ImageView backButton;
    private Uri imageUri;
    private String postUserId;
    private TextView selectedImageText;
    private String currentPhotoPath;

    private FirebaseFirestore database;
    private static final ProductDao productDao = new ProductDao();
    private final static String TAG = "create post";



    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        // Get a reference to the products collection and create a new document with a generated ID
        database = FirebaseFirestore.getInstance();


        preferenceManager = new PreferenceManager(getApplicationContext());
        postUserId = preferenceManager.getString(Constants.KEY_USER_ID);
        selectedImageText = findViewById(R.id.select_image);

        sendButton = findViewById(R.id.send_post_btn);
        backButton = findViewById(R.id.back_btn);
        imageUploadClick = findViewById(R.id.image_view_post);
        title = findViewById(R.id.edit_text_title);
        description = findViewById(R.id.edit_text_description);
        condition = findViewById(R.id.edit_text_condition);
        condition.setMinValue(10);
        condition.setMaxValue(100);
        condition.setWrapSelectorWheel(false);
        condition.setFormatter(value -> String.format("%.1f", value / 10.0));
        condition.setValue(100);

        // Create the location spinner and set its options
        locationSpinner = findViewById(R.id.spinner_location);
        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, LOCATION_OPTIONS);
        locationSpinner.setAdapter(locationAdapter);
        // default location
        locationSpinner.setSelection(0);

        // Create the category spinner and set its options
        categorySpinner = findViewById(R.id.spinner_category);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, CATEGORY_OPTIONS);
        categorySpinner.setAdapter(categoryAdapter);
        // default category
        categorySpinner.setSelection(0);

        price = findViewById(R.id.edit_text_price);
        //Set OnClick Activity for send button
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPostData();
                Toast.makeText(getApplicationContext(), "Post Sent Successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), HuskyMainActivity.class);
                startActivity(intent);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HuskyMainActivity.class);
                startActivity(intent);
            }
        });
    }

    public void onClickMyImageView(View view) {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    // Check camera permission
                    if (ContextCompat.checkSelfPermission(CreatePostActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

                        // Create a file to store the captured image
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            // Error occurred while creating the File
                            Log.e("TAG", "Error occurred while creating the file", ex);
                            return;
                        }

                        // Create a Uri for the captured image
                        imageUri = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".provider", photoFile);

                        // Launch the camera app
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(intent, REQUEST_CAMERA);
                    } else {
                        // Request camera permission
                        ActivityCompat.requestPermissions(CreatePostActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
                    }
                } else if (options[item].equals("Choose from Gallery")) {
                    // Check storage permission
                    if (ContextCompat.checkSelfPermission(CreatePostActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        // Open gallery
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, REQUEST_GALLERY);
                    } else {
                        // Request storage permission
                        ActivityCompat.requestPermissions(CreatePostActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                    }
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_CAMERA) {

                Picasso.get().load(imageUri).resize(500, 500)
                        .centerCrop().into(imageUploadClick); // Set the new bitmap

                selectedImageText.setText("Image selected");
            } else if (requestCode == REQUEST_GALLERY) {
                imageUri = data.getData();
                if (imageUri != null) {
                    Picasso.get().load(imageUri).into(imageUploadClick);
                    Log.d("TAG", "selectedImageUri: " + imageUri);
                    selectedImageText.setText("Image selected");
                }
            }
        } else {
            selectedImageText.setText("No image selected");
        }
    }


    private void uploadPostData() {
        String itemTitle = title.getText().toString().trim();
        String itemDescription = description.getText().toString().trim();
        String itemLocation = locationSpinner.getSelectedItem().toString();
        String itemCategory = categorySpinner.getSelectedItem().toString();
        float itemCondition = condition.getValue() / 10.0f;
        String priceString = price.getText().toString();
        Float itemPrice = null;

        if (itemTitle.isEmpty()) {
            title.setError("Title is required");
            title.requestFocus();
            return;
        }

        if (itemDescription.isEmpty()) {
            description.setError("Description is required");
            description.requestFocus();
            return;
        }

        if (priceString.isEmpty()) {
            price.setError("Price is required");
            price.requestFocus();
            return;
        } else {
            itemPrice = Float.parseFloat(priceString);
            if (itemPrice <= 0) {
                price.setError("Price is required");
                price.requestFocus();
                return;
            }
        }

        Product product = new Product();
        product.setTitle(itemTitle);
        product.setStatus(Constants.VALUE_PRODUCT_STATUS_AVAILABLE);
        product.setPrice(itemPrice);
        product.setDescription(itemDescription);
        product.setCondition(itemCondition);
        product.setCategory(itemCategory);
        product.setLocation(itemLocation);
        product.setPostUserId(postUserId);
        product.setImages(Collections.singletonList(imageUri.toString()));

        database.collection(Constants.KEY_COLLECTION_PRODUCTS)
                .add(product)
                .addOnSuccessListener(documentReference -> {
                    product.setProductId(documentReference.getId());
                    productDao.updateProductId(documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "uploadPostData: " + e);
                });
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "POST_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = imageFile.getAbsolutePath();
        return imageFile;
    }
}
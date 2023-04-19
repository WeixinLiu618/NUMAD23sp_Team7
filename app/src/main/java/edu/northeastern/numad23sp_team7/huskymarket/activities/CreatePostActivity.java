package edu.northeastern.numad23sp_team7.huskymarket.activities;

import static edu.northeastern.numad23sp_team7.huskymarket.utils.ImageCodec.getEncodedImageFromUri;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
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
import edu.northeastern.numad23sp_team7.databinding.ActivityCreatePostBinding;
import edu.northeastern.numad23sp_team7.huskymarket.database.ProductDao;
import edu.northeastern.numad23sp_team7.huskymarket.model.Product;
import edu.northeastern.numad23sp_team7.huskymarket.utils.Constants;
import edu.northeastern.numad23sp_team7.huskymarket.utils.ImageCodec;
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

    private ActivityCreatePostBinding binding;
    private Uri imageUri;
    private String postUserId;

    private String currentPhotoPath;
    private String encodedImageString;


    private FirebaseFirestore database;
    private static final ProductDao productDao = new ProductDao();
    private final static String TAG = "create post";


    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreatePostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseFirestore.getInstance();

        // init
        preferenceManager = new PreferenceManager(getApplicationContext());
        postUserId = preferenceManager.getString(Constants.KEY_USER_ID);

        binding.editTextCondition.setMinValue(10);
        binding.editTextCondition.setMaxValue(100);
        binding.editTextCondition.setWrapSelectorWheel(false);
        binding.editTextCondition.setValue(100);

        // Create the location spinner and set its options
        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, LOCATION_OPTIONS);
        binding.spinnerLocation.setAdapter(locationAdapter);
        // default location
        binding.spinnerLocation.setSelection(0);

        // Create the category spinner and set its options
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, CATEGORY_OPTIONS);
        binding.spinnerCategory.setAdapter(categoryAdapter);
        // default category
        binding.spinnerCategory.setSelection(0);


        //send button
        binding.sendPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uploadValidPostData()) {
                    showToast("Post Sent Successfully");
                    Intent intent = new Intent(getApplicationContext(), HuskyMainActivity.class);
                    startActivity(intent);
                }

            }
        });
        // back button
        binding.backBtn.setOnClickListener(v -> onBackPressed());

        // pick product picture
        binding.imageViewPost.setOnClickListener(this::onClickMyImageView);
        binding.imageProduct.setOnClickListener(this::onClickMyImageView);

    }

    public void onClickMyImageView(View view) {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item) {
                            case 0:
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
                                break;
                            case 1:
//                                if (ContextCompat.checkSelfPermission(CreatePostActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                    // Open gallery
//                                    Log.d(TAG, "onClick: " + "来到这里");
                                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    galleryIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                    startActivityForResult(galleryIntent, REQUEST_GALLERY);
//                                } else {
                                    // Request storage permission
//                                    ActivityCompat.requestPermissions(CreatePostActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
//                                }

                                break;
                        }


                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_CAMERA) {
                encodedImageString = getEncodedImageFromUri(this, imageUri);
                Picasso.get().load(imageUri).into(binding.imageProduct); // Set the new bitmap
                binding.imageProduct.setVisibility(View.VISIBLE);
                binding.imageViewPost.setVisibility(View.GONE);
                binding.selectImagePrompt.setText("Image selected");
            } else if (requestCode == REQUEST_GALLERY) {
                imageUri = data.getData();
                if (imageUri != null) {
                    encodedImageString = getEncodedImageFromUri(this, imageUri);
                    Picasso.get().load(imageUri).into(binding.imageProduct);
                    Log.d("TAG", "selectedImageUri: " + imageUri);
                    binding.imageProduct.setVisibility(View.VISIBLE);
                    binding.imageViewPost.setVisibility(View.GONE);
                    binding.selectImagePrompt.setText("Image selected");
                }
            }
        } else {
            if (encodedImageString == null) {
                binding.selectImagePrompt.setText("No image selected");
            }
        }
    }


    private boolean uploadValidPostData() {
        String itemTitle = binding.editTextTitle.getText().toString().trim();
        String itemDescription = binding.editTextDescription.getText().toString().trim();
        String itemLocation = binding.spinnerLocation.getSelectedItem().toString();
        String itemCategory = binding.spinnerCategory.getSelectedItem().toString();
        float itemCondition = binding.editTextCondition.getValue() / 10.0f;
        String priceString = binding.editTextPrice.getText().toString();
        Float itemPrice = null;

        if (itemTitle.isEmpty()) {
            binding.editTextTitle.setError("Title is required");
            binding.editTextTitle.requestFocus();
            return false;
        }

        if (itemDescription.isEmpty()) {
            binding.editTextDescription.setError("Description is required");
            binding.editTextDescription.requestFocus();
            return false;
        }

        if (priceString.isEmpty()) {
            binding.editTextPrice.setError("Price is required");
            binding.editTextPrice.requestFocus();
            return false;
        } else {
            itemPrice = Float.parseFloat(priceString);
            if (itemPrice < 0) {
                binding.editTextPrice.setError("Price is invalid");
                binding.editTextPrice.requestFocus();
                return false;
            }
        }
        if (encodedImageString == null) {
            setDefaultProductImage();
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
        product.setImages(Collections.singletonList(encodedImageString));
        product.setTimestamp(new Date());

        database.collection(Constants.KEY_COLLECTION_PRODUCTS)
                .add(product)
                .addOnSuccessListener(documentReference -> {
                    product.setProductId(documentReference.getId());
                    productDao.updateProductId(documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "uploadPostData: " + e);
                });
        return true;
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

    private void setDefaultProductImage() {
        Drawable drawable = getResources().getDrawableForDensity(R.drawable.neu_furry_friend, DisplayMetrics.DENSITY_MEDIUM, null);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        encodedImageString = ImageCodec.getEncodedImage(bitmap);
    }


    private void showToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }
}
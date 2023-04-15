package edu.northeastern.numad23sp_team7.huskymarket.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import edu.northeastern.numad23sp_team7.R;
import edu.northeastern.numad23sp_team7.databinding.ActivityHuskySignupBinding;
import edu.northeastern.numad23sp_team7.huskymarket.database.UserDao;
import edu.northeastern.numad23sp_team7.huskymarket.model.User;
import edu.northeastern.numad23sp_team7.huskymarket.utils.Constants;
import edu.northeastern.numad23sp_team7.huskymarket.utils.PreferenceManager;

public class HuskySignupActivity extends AppCompatActivity {

    private ActivityHuskySignupBinding binding;
    private FirebaseAuth mAuth;
    private PreferenceManager preferenceManager;
    private static final UserDao userDao = new UserDao();
    private String encodedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHuskySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());

        // go to login page
        binding.textLogin.setOnClickListener(v -> onBackPressed());

        // upload profile image
        binding.imageProfile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            selectImageFromFile.launch(intent);
        });

        // sign up
        binding.buttonSignUp.setOnClickListener(v -> {
            if (isValidSignupInfo()) {
                loading(true);
                String email = binding.inputEmail.getText().toString().trim();
                String password = binding.inputPassword.getText().toString();
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser authUser = mAuth.getCurrentUser();
                                    assert authUser != null;
                                    String userUid = authUser.getUid();
                                    String username = binding.inputUsername.getText().toString().trim();
                                    if (encodedImage == null) {
                                        setDefaultProfileImage();
                                    }
                                    User user = new User(userUid, username, email, encodedImage, new ArrayList<>());
                                    FirebaseFirestore database = FirebaseFirestore.getInstance();
                                    database.collection(Constants.KEY_COLLECTION_USERS)
                                            .document(userUid)
                                            .set(user)
                                            .addOnSuccessListener(documentReference -> {
                                                // User added to Firestore database successfully
                                                loading(false);
                                                preferenceManager.putString(Constants.KEY_USER_ID, userUid);
                                                preferenceManager.putBoolean(Constants.KEY_IS_LOGGED_IN, true);
                                                preferenceManager.putString(Constants.KEY_USERNAME, username);
                                                preferenceManager.putString(Constants.KEY_EMAIL, email);
                                                preferenceManager.putString(Constants.KEY_PROFILE_IMAGE, encodedImage);
                                                updateFCMToken();
                                                Intent intent = new Intent(getApplicationContext(), HuskyMainActivity.class);
                                                // ensures that the activity is started in a new task and any existing tasks are cleared.
                                                // effectively creating a new task stack for the activity to be launched into.
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                            })
                                            .addOnFailureListener(e -> {
                                                // Error adding user to Firestore database
                                                loading(false);
                                                showToast(e.getMessage());
                                            });
                                } else {
                                    // Error creating user in Firebase Authentication
                                    showToast("Fail to sign up");
                                }
                            }
                        });
            }

        });
    }


    private final ActivityResultLauncher<Intent> selectImageFromFile = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri profileImageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(profileImageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.imageProfile.setImageBitmap(bitmap);
                            encodedImage = getEncodedImage(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
    );


    // allow user not upload profile image, and set the default image for them
    private void setDefaultProfileImage() {
        Drawable drawable = getResources().getDrawableForDensity(R.drawable.default_avatar, DisplayMetrics.DENSITY_MEDIUM, null);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        encodedImage = getEncodedImage(bitmap);
    }


    // verify if signup inputs are all valid
    private boolean isValidSignupInfo() {
        if (binding.inputUsername.getText().toString().trim().isEmpty()) {
            showToast("Please enter username.");
            return false;
        } else if (binding.inputEmail.getText().toString().trim().isEmpty()) {
            showToast("Please enter email.");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString().trim()).matches()) {
            showToast("Please enter valid email.");
            return false;
        } else if (binding.inputPassword.getText().toString().isEmpty()) {
            showToast("Please enter password.");
            return false;
        } else if (binding.inputPassword.getText().toString().length() < 6) {
            showToast("Password must be at least 6 characters");
        } else if (binding.inputConfirmPassword.getText().toString().isEmpty()) {
            showToast("Please confirm your password.");
        } else if (!binding.inputConfirmPassword.getText().toString().equals(binding.inputPassword.getText().toString())) {
            showToast("Password and confirm password must be same.");
            return false;
        }
        return true;
    }

    // bitmap -> string
    private String getEncodedImage(Bitmap bitmap) {
        int width = 150;
        int height = bitmap.getHeight() * width / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream); // jpeg to png
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private void showToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }


    private void loading(boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.buttonSignUp.setVisibility(View.INVISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonSignUp.setVisibility(View.VISIBLE);
        }
    }


    //  use the Firebase Cloud Messaging (FCM) library to retrieve the FCM registration token for the current app instance,
    //  and then calls the updateToken method with the token as a parameter.
    private void updateFCMToken() {
        FirebaseMessaging.getInstance()
                .getToken()
                .addOnSuccessListener(token -> {
                    preferenceManager.putString(Constants.KEY_FCM_TOKEN, token);
                    userDao.updateFCMToken(preferenceManager.getString(Constants.KEY_USER_ID), token);
                });
    }
}
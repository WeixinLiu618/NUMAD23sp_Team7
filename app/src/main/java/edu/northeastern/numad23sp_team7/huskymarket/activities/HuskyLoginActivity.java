package edu.northeastern.numad23sp_team7.huskymarket.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import edu.northeastern.numad23sp_team7.R;
import edu.northeastern.numad23sp_team7.TestActivity;
import edu.northeastern.numad23sp_team7.databinding.ActivityHuskyLoginBinding;
import edu.northeastern.numad23sp_team7.huskymarket.database.UserDao;
import edu.northeastern.numad23sp_team7.huskymarket.model.User;
import edu.northeastern.numad23sp_team7.huskymarket.utils.Constants;
import edu.northeastern.numad23sp_team7.huskymarket.utils.PreferenceManager;

public class HuskyLoginActivity extends AppCompatActivity {

    private ActivityHuskyLoginBinding binding;
    private PreferenceManager preferenceManager;
    private FirebaseAuth mAuth;


    private static final UserDao userDao = new UserDao();
    private static final String TAG = "husky-login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());
        Log.d(TAG, "onCreate: " + preferenceManager.getBoolean(Constants.KEY_IS_LOGGED_IN));
        binding = ActivityHuskyLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();

        // if clicking sign up
        binding.textSignup.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), HuskySignupActivity.class);
            startActivity(intent);
        });

        // log in
        binding.buttonLogin.setOnClickListener(v -> {

            String email = binding.inputEmail.getText().toString().trim();
            String password = binding.inputPassword.getText().toString();
            if (isValidLoginInfo()) {
                loading(true);
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful() && mAuth.getCurrentUser() != null) {
                                    loading(false);
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser authUser = mAuth.getCurrentUser();
                                    String userUid = authUser.getUid();
                                    login(userUid);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    loading(false);
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    showToast("invalid email or password.");
                                }
                            }
                        });
            }
        });

        // forget password
        binding.forgetPassword.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(HuskyLoginActivity.this);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_forget_password, null);
            builder.setView(dialogView);
            EditText editTextEmail = dialogView.findViewById(R.id.editTextEmail);
            TextView buttonResetCancel = dialogView.findViewById(R.id.buttonResetCancel);
            TextView buttonResetOk = dialogView.findViewById(R.id.buttonResetOk);

            AlertDialog alertDialog = builder.create();

            // dismiss dialog
            buttonResetCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            // reset
            buttonResetOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String inputEmail = editTextEmail.getText().toString().trim();
                    if (inputEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(inputEmail).matches()) {
                        showToast("Please enter valid email.");
                    }
                    mAuth.sendPasswordResetEmail(inputEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                showToast("Please check your email.");
                                alertDialog.dismiss();
                            }else {
                                showToast("Unable to access this email.");
                            }
                        }
                    });

                }
            });
            alertDialog.show();


        });
    }

    private void login(String userId) {
        userDao.getUserById(userId, currentUser -> {
            preferenceManager.putString(Constants.KEY_USER_ID, userId);
            preferenceManager.putString(Constants.KEY_USERNAME, currentUser.getUsername());
            preferenceManager.putString(Constants.KEY_EMAIL, currentUser.getEmail());
            preferenceManager.putString(Constants.KEY_PROFILE_IMAGE, currentUser.getProfileImage());
            preferenceManager.putBoolean(Constants.KEY_IS_LOGGED_IN, true);
            updateFCMToken();
            Intent intent = new Intent(getApplicationContext(), HuskyMainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        });
    }

    private boolean isValidLoginInfo() {
        if (binding.inputEmail.getText().toString().trim().isEmpty()) {
            showToast("Please enter email.");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()) {
            showToast("Please enter valid email.");
            return false;
        } else if (binding.inputPassword.getText().toString().isEmpty()) {
            showToast("Please enter password.");
            return false;
        }
        return true;
    }


    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void loading(boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.buttonLogin.setVisibility(View.INVISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonLogin.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), HuskyMainActivity.class);
            startActivity(intent);
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
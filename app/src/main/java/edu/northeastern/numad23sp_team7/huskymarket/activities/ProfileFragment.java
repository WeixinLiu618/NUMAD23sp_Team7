package edu.northeastern.numad23sp_team7.huskymarket.activities;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import edu.northeastern.numad23sp_team7.databinding.FragmentProfileBinding;
import edu.northeastern.numad23sp_team7.huskymarket.database.ProductDao;
import edu.northeastern.numad23sp_team7.huskymarket.database.RecentMessageDao;
import edu.northeastern.numad23sp_team7.huskymarket.database.UserDao;
import edu.northeastern.numad23sp_team7.huskymarket.utils.Constants;
import edu.northeastern.numad23sp_team7.huskymarket.utils.ImageCodec;
import edu.northeastern.numad23sp_team7.huskymarket.utils.PreferenceManager;


public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseAuth mAuth;

    private PreferenceManager preferenceManager;
    private static final UserDao userDao = new UserDao();
    private static final ProductDao productDao = new ProductDao();
    private static final RecentMessageDao recentMessageDao = new RecentMessageDao();

    private static final String TAG = "profile fragment";

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();

        preferenceManager = new PreferenceManager(requireContext());
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(getLayoutInflater());

        showUserInfo();

        binding.iconLogout.setOnClickListener(v -> {
            logout();
        });

        binding.iconEditProfileImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });


        return binding.getRoot();
    }

    private void showUserInfo() {
        binding.imageProfile.setImageBitmap(ImageCodec.getDecodedImage(preferenceManager.getString(Constants.KEY_PROFILE_IMAGE)));
        binding.username.setText(preferenceManager.getString(Constants.KEY_USERNAME));
        binding.email.setText(preferenceManager.getString(Constants.KEY_EMAIL));
        binding.editableUsername.setText(preferenceManager.getString(Constants.KEY_USERNAME));

        // set posts number
        productDao.getMyPostsProductsForUser(preferenceManager.getString(Constants.KEY_USER_ID), products -> {
            String postsNum = "0";
            if (products != null && products.size() > 0) {
                postsNum = String.valueOf(products.size());
            }
            Log.d(TAG, "showUserInfo: postNum " + postsNum);
            binding.textPostsNumber.setText(postsNum);
        });

        // set sold number
        productDao.getMySoldProductsForUser(preferenceManager.getString(Constants.KEY_USER_ID), products -> {
            String soldNum = "0";
            if (products != null && products.size() > 0) {
                soldNum = String.valueOf(products.size());
            }
            Log.d(TAG, "showUserInfo: soldNum " + soldNum);
            binding.textSoldNumber.setText(soldNum);
        });

        // set favorites number
        userDao.getUserById(preferenceManager.getString(Constants.KEY_USER_ID), user -> {
            String favoritesNum = "0";
            if (user != null && user.getFavorites() != null && user.getFavorites().size() > 0) {
                favoritesNum = String.valueOf(user.getFavorites().size());
            }
            binding.textFavoritesNumber.setText(favoritesNum);
        });

    }


    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("Are you sure to log out?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signOut();
                        userDao.updateFCMToken(preferenceManager.getString(Constants.KEY_USER_ID), null);
                        preferenceManager.clear();
                        Intent intent = new Intent(getActivity(), HuskyLoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }


    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getActivity().getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            Bitmap rotatedBitmap = ImageCodec.rotateImage(bitmap, imageUri, getContext());
                            binding.imageProfile.setImageBitmap(rotatedBitmap);
                            String encodedImage = ImageCodec.getEncodedSmallImage(rotatedBitmap);
                            preferenceManager.putString(Constants.KEY_PROFILE_IMAGE, encodedImage);
                            userDao.updateUserProfileImage(preferenceManager.getString(Constants.KEY_USER_ID), encodedImage);
                            Log.d(TAG, "image: " + preferenceManager.getString(Constants.KEY_PROFILE_IMAGE));
                            recentMessageDao.updateProfileImage(preferenceManager.getString(Constants.KEY_USER_ID),
                                    encodedImage);


                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    private void showToast(String text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

}
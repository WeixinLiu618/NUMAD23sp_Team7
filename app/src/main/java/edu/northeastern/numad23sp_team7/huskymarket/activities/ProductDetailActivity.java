package edu.northeastern.numad23sp_team7.huskymarket.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import edu.northeastern.numad23sp_team7.R;
import edu.northeastern.numad23sp_team7.databinding.ActivityProductDetailBinding;
import edu.northeastern.numad23sp_team7.huskymarket.database.ProductDao;
import edu.northeastern.numad23sp_team7.huskymarket.database.UserDao;
import edu.northeastern.numad23sp_team7.huskymarket.model.Product;
import edu.northeastern.numad23sp_team7.huskymarket.model.User;
import edu.northeastern.numad23sp_team7.huskymarket.utils.Constants;
import edu.northeastern.numad23sp_team7.huskymarket.utils.ImageCodec;
import edu.northeastern.numad23sp_team7.huskymarket.utils.PreferenceManager;

public class ProductDetailActivity extends AppCompatActivity {
    ActivityProductDetailBinding binding;
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private String productId;
    private Product product;
    private User loggedInUser;
    private AtomicInteger saved;
    private UserDao userDao = new UserDao();
    private ProductDao productDao = new ProductDao();
    private PreferenceManager preferenceManager;
    private boolean isFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userDao = new UserDao();
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(this);
        binding.progressBarHuskyDetail.setVisibility(View.VISIBLE);
        binding.scrollViewHuskyDetail.setVisibility(View.INVISIBLE);

        // Get product Id
        Intent intentFromFormer = getIntent();
        productId = intentFromFormer.getStringExtra(Constants.KEY_PRODUCT_ID);
        String userId = preferenceManager.getString(Constants.KEY_USER_ID);
        if (userId == null || userId.isEmpty()) {
            Intent intent = new Intent(this, HuskyLoginActivity.class);
            startActivity(intent);
        }

        userDao.getUserById(userId, user -> {
            loggedInUser = user;
            isFavorite = user.getFavorites().contains(productId);
            setBookmarkIcon();
        });

        productDao.getProductById(userId, productId, productObj -> {
            product = productObj;
            userDao.getUserById(productObj.getPostUserId(),user -> {
                binding.imageDetailSellerName.setText(user.getUsername());
                binding.imageDetailSellerProfile.setImageBitmap(ImageCodec.getDecodedImage(user.getProfileImage()));
            });
            updateView();
        });

        // Bookmark click listener
        binding.iconHuskyDetailBookmark.setOnClickListener(view -> {
            if (isFavorite) {
                isFavorite = false;
                loggedInUser.getFavorites().remove(productId);
                userDao.removeItemFromFavorites(userId, productId);
                setNotBookmarkedIcon();
            } else {
                isFavorite = true;
                loggedInUser.getFavorites().add(productId);
                userDao.addItemToFavorites(userId, productId);
                setBookmarkedIcon();
            }
        });

        //start chat with seller
        binding.iconHuskyDetailSendMessage.setOnClickListener(v -> {
            userDao.getUserById(product.getPostUserId(), receiver -> {
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra(Constants.KEY_USER, receiver);
                startActivity(intent);
            });
        });

        // back button
        binding.backBtn.setOnClickListener(v -> onBackPressed());
    }

    private void updateView() {
        List<String> images = product.getImages();
        if (!images.isEmpty()) {
            Bitmap imageBitMap = ImageCodec.getDecodedImage(images.get(0));
            if (imageBitMap != null) {
                binding.imageViewHuskyDetailProductImage.setImageBitmap(imageBitMap);
            }
        }
        binding.textViewHuskyDetailProductName.setText(product.getTitle());
        binding.textViewHuskyDetailPriceOfProduct.setText("$ " + String.format("%.2f", product.getPrice()));
        binding.textViewHuskyDetailLocation.setText(product.getLocation());
        binding.textViewHuskyDetailConditionValue.setText(String.format("%.1f", product.getCondition()) + "/10.0");
        binding.textViewHuskyDetailCategoryValue.setText(product.getCategory());
        String status = product.getStatus();
        status = status.substring(0, 1).toUpperCase() + status.substring(1);
        binding.textViewHuskyDetailStatusValue.setText(status);
        binding.textViewHuskyDetailListingTimeValue.setText(getSimpleDate(product.getTimestamp()));
        binding.textViewHuskyDetailDescription.setText(product.getDescription());
        setChatIcon();
        binding.progressBarHuskyDetail.setVisibility(View.INVISIBLE);
        binding.scrollViewHuskyDetail.setVisibility(View.VISIBLE);
    }

    private String getSimpleDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        return sdf.format(date);
    }

    private void setBookmarkIcon() {
        if (isFavorite) {
            setBookmarkedIcon();
        } else {
            setNotBookmarkedIcon();
        }
    }

    private void setBookmarkedIcon() {
        binding.iconHuskyDetailBookmark.setBackground(getDrawable(R.drawable.ic_favorite_solid_22));
    }

    private void setNotBookmarkedIcon() {
        binding.iconHuskyDetailBookmark.setBackground(getDrawable(R.drawable.ic_favorite_hollow_22));
    }

    private void setChatIcon() {
        if (!preferenceManager.getString(Constants.KEY_USER_ID).equals(product.getPostUserId())) {
            binding.iconHuskyDetailSendMessage.setVisibility(View.VISIBLE);
        }
    }
}

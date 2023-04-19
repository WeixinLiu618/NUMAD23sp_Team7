package edu.northeastern.numad23sp_team7.huskymarket.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import edu.northeastern.numad23sp_team7.R;
import edu.northeastern.numad23sp_team7.databinding.ActivityProductDetailBinding;
import edu.northeastern.numad23sp_team7.huskymarket.adapter.SearchResultAdapter;
import edu.northeastern.numad23sp_team7.huskymarket.database.ProductDao;
import edu.northeastern.numad23sp_team7.huskymarket.database.UserDao;
import edu.northeastern.numad23sp_team7.huskymarket.model.ChatMessage;
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
    private int bookmarkIcon;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userDao = new UserDao();
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(this);

        // Get product Id
        Intent intentFromFormer = getIntent();
        productId = intentFromFormer.getStringExtra(Constants.KEY_PRODUCT_ID);
        String userId = preferenceManager.getString(Constants.KEY_USER_ID);
        userDao.getUserById(userId, user -> {
            loggedInUser = user;
        });


        productDao.getProductById(userId, productId, productObj -> {
            product = productObj;
            userDao.getUserById(productObj.getPostUserId(),user -> {
                binding.imageDetailSellerName.setText(user.getUsername());
                binding.imageDetailSellerProfile.setImageBitmap(ImageCodec.getDecodedImage(user.getProfileImage()));
            });
            updateView();
        });

//        binding.bookmarkProductDetail.setOnClickListener(view -> {
//            if (saved.get() == 1) {
//                saved.set(0);
//                loggedInUser.getFavorites().remove(productId);
//                userDao.removeItemFromFavorites(loggedInUser.getId(), productId);
//                bookmarkIcon = R.drawable.ic_bookmarked_not;
//            } else if (saved.get() == 0) {
//                saved.set(1);
//                loggedInUser.getFavorites().add(productId);
//                userDao.addItemToFavorites(loggedInUser.getId(), productId);
//                bookmarkIcon = R.drawable.ic_bookmarked;
//            }
//            binding.bookmarkProductDetail.setBackground(
//                    ContextCompat.getDrawable(binding.bookmarkProductDetail.getContext(),bookmarkIcon));
//        });
//
//        binding.layoutSendMessage.setOnClickListener(v -> {
//            //start chat with seller
//            UserDao userDao = new UserDao();
//            userDao.getUserById(product.getPostUserId(), receiver -> {
//                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
//                intent.putExtra(Constants.KEY_USER, receiver);
//                startActivity(intent);
//            });
//        });
//        // back button
//        binding.backBtn.setOnClickListener(v -> onBackPressed());
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
    }

    private int isFavorite(String productId) {
        // Saved status
        // 0: not saved
        // 1: saved
        // 2: not looged-in™
        if (loggedInUser == null) {
            return 2;
        }

        if (loggedInUser.getFavorites().contains(productId)) {
            return 1;
        }

        return 0;
    }

    private String getSimpleDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        return sdf.format(date);
    }
}

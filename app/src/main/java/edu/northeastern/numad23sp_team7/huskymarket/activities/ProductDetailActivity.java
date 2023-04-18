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
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import edu.northeastern.numad23sp_team7.R;
import edu.northeastern.numad23sp_team7.databinding.ActivityProductDetailBinding;
import edu.northeastern.numad23sp_team7.huskymarket.adapter.SearchResultAdapter;
import edu.northeastern.numad23sp_team7.huskymarket.database.UserDao;
import edu.northeastern.numad23sp_team7.huskymarket.model.ChatMessage;
import edu.northeastern.numad23sp_team7.huskymarket.model.Product;
import edu.northeastern.numad23sp_team7.huskymarket.model.User;
import edu.northeastern.numad23sp_team7.huskymarket.utils.Constants;
import edu.northeastern.numad23sp_team7.huskymarket.utils.ImageCodec;
import edu.northeastern.numad23sp_team7.huskymarket.utils.PreferenceManager;

public class ProductDetailActivity extends AppCompatActivity {
    ActivityProductDetailBinding binding;
    private FirebaseFirestore database;
    private String productId;
    private Product product;
    private User loggedInUser;
    private AtomicInteger saved;
    private UserDao userDao;
    private int bookmarkIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userDao = new UserDao();
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseFirestore.getInstance();

        // Get product Id
        Intent intentFromSearch = getIntent();
        productId = intentFromSearch.getStringExtra(Constants.KEY_PRODUCT_ID);
        loggedInUser = (User) intentFromSearch.getSerializableExtra(Constants.KEY_USER);


        //load product detail from database
        database.collection(Constants.KEY_COLLECTION_PRODUCTS).document(productId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        product = documentSnapshot.toObject(Product.class);
                        binding.productName.setText(product.getTitle());
                        binding.productConditionView.setText(String.valueOf(product.getCondition()));
                        binding.thePriceOfProduct.setText("$" + String.valueOf(product.getPrice()));
                        binding.location.setText(product.getLocation());
                        //TODO: get Image
                        //bookmark
                        //Drawable bookmarkIcon;
                        productId = product.getProductId();
                        saved = new AtomicInteger(isFavorite(productId));
                        if (saved.get() == 1 ) {
                            bookmarkIcon = R.drawable.ic_bookmarked;
                        } else {
                            bookmarkIcon = R.drawable.ic_bookmarked_not;
                        }
                        binding.bookmarkProductDetail.setBackground(
                                ContextCompat.getDrawable(binding.bookmarkProductDetail.getContext(), bookmarkIcon ));

                        List<String> images = product.getImages();
                        if (!images.isEmpty()) {
                            Bitmap imageBitMap = ImageCodec.getDecodedImage(images.get(0));
                            if (imageBitMap != null) {
                                binding.photo.setImageBitmap(imageBitMap);
                            }
                        }
                    }

                });
        binding.bookmarkProductDetail.setOnClickListener(view -> {
            if (saved.get() == 1) {
                saved.set(0);
                loggedInUser.getFavorites().remove(productId);
                userDao.removeItemFromFavorites(loggedInUser.getId(), productId);
                bookmarkIcon = R.drawable.ic_bookmarked_not;
            } else if (saved.get() == 0) {
                saved.set(1);
                loggedInUser.getFavorites().add(productId);
                userDao.addItemToFavorites(loggedInUser.getId(), productId);
                bookmarkIcon = R.drawable.ic_bookmarked;
            }
            binding.bookmarkProductDetail.setBackground(
                    ContextCompat.getDrawable(binding.bookmarkProductDetail.getContext(),bookmarkIcon));
        });

        binding.layoutSendMessage.setOnClickListener(v -> {
            //start chat with seller
            UserDao userDao = new UserDao();
            userDao.getUserById(product.getPostUserId(), receiver -> {
                Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra(Constants.KEY_USER, receiver);
                startActivity(intent);
            });
        });
        // back button
        binding.backBtn.setOnClickListener(v -> onBackPressed());
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
}

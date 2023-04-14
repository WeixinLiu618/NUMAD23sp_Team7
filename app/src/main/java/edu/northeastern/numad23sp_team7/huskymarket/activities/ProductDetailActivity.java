package edu.northeastern.numad23sp_team7.huskymarket.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;

import edu.northeastern.numad23sp_team7.R;
import edu.northeastern.numad23sp_team7.databinding.ActivityProductDetailBinding;
import edu.northeastern.numad23sp_team7.huskymarket.database.UserDao;
import edu.northeastern.numad23sp_team7.huskymarket.model.ChatMessage;
import edu.northeastern.numad23sp_team7.huskymarket.model.Product;
import edu.northeastern.numad23sp_team7.huskymarket.model.User;
import edu.northeastern.numad23sp_team7.huskymarket.utils.Constants;

public class ProductDetailActivity extends AppCompatActivity {
    ActivityProductDetailBinding binding;
    private FirebaseFirestore database;
    private String productId;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseFirestore.getInstance();

        // Get product Id
        Intent intentFromSearch = getIntent();
        productId = intentFromSearch.getStringExtra(Constants.KEY_PRODUCT_ID);

        //load product detail from database
        database.collection(Constants.KEY_COLLECTION_PRODUCTS).document(productId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        product = documentSnapshot.toObject(Product.class);
                        binding.productName.setText(product.getTitle());
                        binding.productConditionView.setText(String.valueOf(product.getCondition()));
                        binding.thePriceOfProduct.setText(String.valueOf(product.getPrice()));
                        binding.location.setText(product.getLocation());
                        //TODO: get Image
                    }
                });
        binding.layoutSendMessage.setOnClickListener(v -> {
            //start chat with seller
            // TODO: go to ChatActivity, passing post user (not post user id)
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
}

package edu.northeastern.numad23sp_team7.huskymarket.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import edu.northeastern.numad23sp_team7.R;
import edu.northeastern.numad23sp_team7.databinding.ActivityProductDetailBinding;
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
                        binding.productConditionView.setText((int) product.getCondition());
                        binding.thePriceOfProduct.setText(getString((int) product.getPrice()));
                        binding.location.setText(product.getLocation());
                        binding.photo.setImageURI((Uri) product.getImages());
                    }
                });
        binding.layoutSend.setOnClickListener(v -> {
            //start chat with seller
            Intent intent = new Intent(ProductDetailActivity.this, CreatePostActivity.class);
            startActivity(intent);
        });
    }
}

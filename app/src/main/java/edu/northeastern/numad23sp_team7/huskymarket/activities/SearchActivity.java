package edu.northeastern.numad23sp_team7.huskymarket.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import edu.northeastern.numad23sp_team7.databinding.ActivitySearchBinding;
import edu.northeastern.numad23sp_team7.huskymarket.adapter.SearchResultAdapter;
import edu.northeastern.numad23sp_team7.huskymarket.database.ProductDao;
import edu.northeastern.numad23sp_team7.huskymarket.database.UserDao;
import edu.northeastern.numad23sp_team7.huskymarket.model.Product;
import edu.northeastern.numad23sp_team7.huskymarket.utils.Constants;
import edu.northeastern.numad23sp_team7.huskymarket.utils.PreferenceManager;

public class SearchActivity extends AppCompatActivity {
    ActivitySearchBinding binding;
    private SearchResultAdapter searchResultAdapter;
    private ArrayList<Product> products = new ArrayList<>();
    private static final String TAG = "Husky Search";
    private static final String CATEGORY_FILTER = "Category";
    private static final String LOCATION_FILTER = "Location";
    private static final ProductDao dbClient = new ProductDao();
    private String category = CATEGORY_FILTER;
    private String location = LOCATION_FILTER;
    private String searchTerm = "";
    private UserDao userDao;
    private String userId;
    private PreferenceManager preferenceManager;
    private static final String IMAGE_BIT_STRING = "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";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        searchResultAdapter = new SearchResultAdapter(products, binding.getRoot().getContext());
        binding.recyclerViewHuskySearchResult.setAdapter(searchResultAdapter);
        preferenceManager = new PreferenceManager(this);

        // Initialize products
//        getProducts();
//        initializeData();
//        dbClient.addProducts(products);

        userId = preferenceManager.getString(Constants.KEY_USER_ID);
        userDao = new UserDao();
        userDao.getUserById("D9gtlUubrMYR9UZyCQlc18uAr7r2", user -> {
            searchResultAdapter.updateLoggedInUser(user);
            searchResultAdapter.notifyDataSetChanged();
        });

        // Get location
        Intent intent = getIntent();
        String userLocation = intent.getStringExtra(Constants.KEY_PRODUCT_LOCATION);
        if (userLocation != null) {
            location = userLocation;
            Resources resources = binding.getRoot().getContext().getResources();
            String packageName = binding.getRoot().getContext().getPackageName();
            String[] locations = resources.getStringArray(resources.getIdentifier("locations2", "array", packageName));
            binding.spinnerHuskySearchFilterLocation.setSelection(Arrays.asList(locations).indexOf(location));

        }

        // Search bar
        binding.editTextHuskySearchBox.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(binding.editTextHuskySearchBox, InputMethodManager.SHOW_IMPLICIT);
        binding.editTextHuskySearchBox.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                initializeFilters();
                performSearch();
                return true;
            }

            return false;
        });

        // Spinners
        binding.spinnerHuskySearchFilterCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String newCategory = String.valueOf(binding.spinnerHuskySearchFilterCategory.getSelectedItem());
                if (!newCategory.equals(category)) {
                    category = newCategory;
                    performSearch();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });

        binding.spinnerHuskySearchFilterLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String newLocation = String.valueOf(binding.spinnerHuskySearchFilterLocation.getSelectedItem());
                if (!newLocation.equals(location)) {
                    location = newLocation;
                    performSearch();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });

        binding.backButtonHuskySearchBar.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    private void initializeData() {
        String[] locations = getResources().getStringArray(binding.getRoot().getResources().getIdentifier(Constants.KEY_PRODUCT_LOCATION_ARRAY, "array", getPackageName()));
        String[] categories = getResources().getStringArray(binding.getRoot().getResources().getIdentifier(Constants.KEY_PRODUCT_CATEGORY_ARRAY, "array", getPackageName()));
        String[] statuses = {Constants.VALUE_PRODUCT_STATUS_AVAILABLE, Constants.VALUE_PRODUCT_STATUS_SOLD};
        for (int i = 0; i < 10; i++) {
            Product product = new Product();
            product.setDescription("Product " + i);
            product.setPostUserId("User " + i);
            product.setLocation(locations[(int) (Math.random() * (locations.length - 1)) + 1]);
            product.setCondition((float) (Math.random() * 5));
            product.setCategory(categories[(int) (Math.random() * (categories.length - 1)) + 1]);
            product.setImages(Arrays.asList("image1", "image2"));
            product.setStatus(statuses[(new Random()).nextInt(statuses.length)]);
            product.setPrice((float) (Math.random() * 100));
            products.add(product);
        }
    }

    private void performSearch() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.editTextHuskySearchBox.getWindowToken(), 0);
        searchTerm = binding.editTextHuskySearchBox.getText().toString();
        searchTerm.trim();
        Log.d(TAG, "search term:" + searchTerm);
        getProducts();
    }

    private void getProducts() {
        String categoryForQuery = category.equals(CATEGORY_FILTER) ? "" : category;
        String locationForQuery = location.equals(LOCATION_FILTER) ? "" : location;

        binding.recyclerViewHuskySearchResult.setVisibility(View.INVISIBLE);
        binding.progressBarHuskySearch.setVisibility(View.VISIBLE);
        dbClient.getProductsBySearch(searchTerm, categoryForQuery, locationForQuery, productsList -> {
            this.products = productsList;
            searchResultAdapter.setProducts(productsList);
            searchResultAdapter.notifyDataSetChanged();
            binding.textViewSearchResultCount.setText("Results: " + this.products.size());
            binding.progressBarHuskySearch.setVisibility(View.INVISIBLE);
            binding.recyclerViewHuskySearchResult.setVisibility(View.VISIBLE);
        });
    }

    private void initializeFilters() {
        binding.spinnerHuskySearchFilterCategory.setSelection(0);
//        binding.spinnerHuskySearchFilterLocation.setSelection(0);
        category = CATEGORY_FILTER;
//        location = LOCATION_FILTER;
    }
}
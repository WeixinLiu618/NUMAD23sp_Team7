package edu.northeastern.numad23sp_team7.huskymarket.activities;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.Arrays;

import edu.northeastern.numad23sp_team7.databinding.ActivitySearchBinding;
import edu.northeastern.numad23sp_team7.huskymarket.adapter.SearchResultAdapter;
import edu.northeastern.numad23sp_team7.huskymarket.database.ProductDao;
import edu.northeastern.numad23sp_team7.huskymarket.model.Product;
import edu.northeastern.numad23sp_team7.huskymarket.utils.Constants;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        searchResultAdapter = new SearchResultAdapter(products);
        binding.recyclerViewHuskySearchResult.setAdapter(searchResultAdapter);
//        initializeData();
//        dbClient.addProducts(products);

        // Search bar
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

    }

    private void initializeData() {
        String[] locations = getResources().getStringArray(binding.getRoot().getResources().getIdentifier(Constants.KEY_PRODUCT_LOCATION_ARRAY, "array", getPackageName()));
        String[] categories = getResources().getStringArray(binding.getRoot().getResources().getIdentifier(Constants.KEY_PRODUCT_CATEGORY_ARRAY, "array", getPackageName()));
        for (int i = 0; i < 10; i++) {
            Product product = new Product();
            product.setDescription("Product " + i);
            product.setPostUderId("User " + i);
            product.setLocation(locations[(int) (Math.random() * (locations.length - 1)) + 1]);
            product.setCondition((float) (Math.random() * 5));
            product.setCategory(categories[(int) (Math.random() * (categories.length - 1)) + 1]);
            product.setColor("Color " + i);
            product.setImages(Arrays.asList("image1", "image2", "image3"));
            product.setMaterial("Material " + i);
            product.setStatus("Status " + i);
            product.setPrice((float) (Math.random() * 100));
            products.add(product);
        }
    }

    private void performSearch() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.editTextHuskySearchBox.getWindowToken(), 0);
        searchTerm = binding.editTextHuskySearchBox.getText().toString();
        searchTerm.trim();
        Log.d(TAG, "term:" + searchTerm);
        getProducts();
    }

    private void getProducts() {
        String categoryForQuery = category.equals(CATEGORY_FILTER) ? "" : category;
        String locationForQuery = location.equals(LOCATION_FILTER) ? "" : location;
        dbClient.getProducts(searchTerm, categoryForQuery, locationForQuery, productsList -> {
            searchResultAdapter.setProducts(productsList);
            searchResultAdapter.notifyDataSetChanged();
        });
    }

    private void initializeFilters() {
        binding.spinnerHuskySearchFilterCategory.setSelection(0);
        binding.spinnerHuskySearchFilterLocation.setSelection(0);
        category = CATEGORY_FILTER;
        location = LOCATION_FILTER;
    }
}
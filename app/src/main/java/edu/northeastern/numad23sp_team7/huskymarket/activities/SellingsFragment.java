package edu.northeastern.numad23sp_team7.huskymarket.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import edu.northeastern.numad23sp_team7.R;
import edu.northeastern.numad23sp_team7.databinding.FragmentSellingsBinding;
import edu.northeastern.numad23sp_team7.huskymarket.adapter.MySellingsAdapter;
import edu.northeastern.numad23sp_team7.huskymarket.database.ProductDao;
import edu.northeastern.numad23sp_team7.huskymarket.database.UserDao;
import edu.northeastern.numad23sp_team7.huskymarket.listeners.MySellingsCardClickListener;
import edu.northeastern.numad23sp_team7.huskymarket.model.Product;
import edu.northeastern.numad23sp_team7.huskymarket.utils.Constants;
import edu.northeastern.numad23sp_team7.huskymarket.utils.PreferenceManager;

public class SellingsFragment extends Fragment implements MySellingsCardClickListener {

    private FragmentSellingsBinding binding;
    private PreferenceManager preferenceManager;
    private ArrayList<Product> mySellings = new ArrayList<>();
    private MySellingsAdapter mySellingsAdapter;
    private FirebaseFirestore database;

    private static final ProductDao productDao = new ProductDao();
    private static final UserDao userDao = new UserDao();

    private static final String TAG = "Fragment Sellings";

    public SellingsFragment() {

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        database = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(requireContext());
        binding = FragmentSellingsBinding.inflate(getLayoutInflater());
        String userId = preferenceManager.getString(Constants.KEY_USER_ID);

        // show all posts products
        productDao.getMyPostsProductsForUser(userId, products -> {
            this.mySellings = products;
            mySellingsAdapter = new MySellingsAdapter(mySellings, this);
            binding.recyclerViewSellings.setAdapter(mySellingsAdapter);
            mySellingsAdapter.notifyDataSetChanged();
            Log.d(TAG, "onCreateView: " + mySellings.size());
            loading(false);
        });


        return binding.getRoot();


    }

    @Override
    public void onSettingClick(Product curProduct) {

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.dialog_sellings_edit_status, null);
        builder.setView(dialogView);

        Button buttonAvailable = (Button) dialogView.findViewById(R.id.setAvailableButton);
        Button buttonSold = (Button) dialogView.findViewById(R.id.setSoldButton);
        AlertDialog alertDialog = builder.create();


        String productId = curProduct.getProductId();

        buttonAvailable.setOnClickListener(v -> {
            if (curProduct.getStatus().equals(Constants.VALUE_PRODUCT_STATUS_SOLD)) {
                productDao.updateProductStatus(productId, Constants.VALUE_PRODUCT_STATUS_AVAILABLE, product -> {
                    for (int position = 0; position < mySellings.size(); position++) {
                        if (mySellings.get(position).getProductId().equals(productId)) {
                            mySellings.set(position, product);
                            mySellingsAdapter.notifyItemChanged(position);
                            break;
                        }
                    }
                });



            }
            alertDialog.dismiss();

        });
        buttonSold.setOnClickListener(v -> {
            if (curProduct.getStatus().equals(Constants.VALUE_PRODUCT_STATUS_AVAILABLE)) {
                productDao.updateProductStatus(productId, Constants.VALUE_PRODUCT_STATUS_SOLD, product -> {
                    for (int position = 0; position < mySellings.size(); position++) {
                        if (mySellings.get(position).getProductId().equals(productId)) {
                            mySellings.set(position, product);
                            mySellingsAdapter.notifyItemChanged(position);
                            break;
                        }
                    }
                });

            }
            alertDialog.dismiss();
        });
        alertDialog.show();


    }

    @Override
    public void onProductImageClick(Product product) {
        // click into details page
        userDao.getUserById(preferenceManager.getString(Constants.KEY_USER_ID), loggedInUser -> {
            Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
            intent.putExtra(Constants.KEY_PRODUCT_ID, product.getProductId());
            intent.putExtra(Constants.KEY_USER, loggedInUser);
            startActivity(intent);
        });
    }

    private void loading(boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.recyclerViewSellings.setVisibility(View.GONE);
            binding.noPostsPrompt.setVisibility(View.GONE);
        } else {
            binding.progressBar.setVisibility(View.GONE);
            if (mySellings.size() == 0) {
                binding.noPostsPrompt.setVisibility(View.VISIBLE);
                binding.recyclerViewSellings.setVisibility(View.GONE);
            } else {
                binding.recyclerViewSellings.smoothScrollToPosition(0);
                Log.d(TAG, "loading: " + "来到这里");
                binding.recyclerViewSellings.setVisibility(View.VISIBLE);
                binding.noPostsPrompt.setVisibility(View.GONE);
            }
        }

    }
}
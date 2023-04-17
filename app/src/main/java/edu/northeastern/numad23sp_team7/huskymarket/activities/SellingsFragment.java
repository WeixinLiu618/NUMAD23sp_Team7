package edu.northeastern.numad23sp_team7.huskymarket.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import edu.northeastern.numad23sp_team7.databinding.FragmentSellingsBinding;
import edu.northeastern.numad23sp_team7.huskymarket.adapter.MySellingsAdapter;
import edu.northeastern.numad23sp_team7.huskymarket.database.ProductDao;
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


        // TODO
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
    public void onEditClick(int position) {

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
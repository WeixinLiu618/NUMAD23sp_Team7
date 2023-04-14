package edu.northeastern.numad23sp_team7.huskymarket.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

        // Inflate the layout for this fragment
        binding = FragmentSellingsBinding.inflate(getLayoutInflater());
        mySellings = new ArrayList<>();
        mySellingsAdapter = new MySellingsAdapter(mySellings, getContext(), this);
        binding.recyclerViewSellings.setAdapter(mySellingsAdapter);

        String userId = preferenceManager.getString(Constants.KEY_USER_ID);

        productDao.getMyPostsProductsForUser(userId, mySellings -> {
            mySellingsAdapter.notifyDataSetChanged();
            Log.d(TAG, "onCreateView: "+mySellings.size());
            if (mySellings.size() == 0) {
                binding.noPostsPrompt.setVisibility(View.VISIBLE);
                binding.recyclerViewSellings.setVisibility(View.GONE);
            } else {
                binding.recyclerViewSellings.smoothScrollToPosition(0);
                binding.recyclerViewSellings.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
            }
            binding.progressBar.setVisibility(View.GONE);
        });




        return binding.getRoot();


    }

    @Override
    public void onEditClick(int position) {

    }
}
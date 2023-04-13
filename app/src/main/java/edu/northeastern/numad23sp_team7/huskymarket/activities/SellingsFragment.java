package edu.northeastern.numad23sp_team7.huskymarket.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import edu.northeastern.numad23sp_team7.databinding.FragmentSellingsBinding;
import edu.northeastern.numad23sp_team7.huskymarket.model.Product;

public class SellingsFragment extends Fragment {

    private FragmentSellingsBinding binding;
    private ArrayList<Product> products = new ArrayList<>();
    private static final String TAG = "Fragment Sellings";

    public SellingsFragment() {

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSellingsBinding.inflate(getLayoutInflater());
        return binding.getRoot();

    }
}
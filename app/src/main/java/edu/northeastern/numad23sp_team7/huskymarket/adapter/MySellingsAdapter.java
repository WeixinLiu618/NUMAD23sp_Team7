package edu.northeastern.numad23sp_team7.huskymarket.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.northeastern.numad23sp_team7.databinding.ItemSellingsBinding;
import edu.northeastern.numad23sp_team7.huskymarket.listeners.RecentMessageCardClickListener;
import edu.northeastern.numad23sp_team7.huskymarket.model.Product;
import edu.northeastern.numad23sp_team7.huskymarket.utils.ImageCodec;

public class MySellingsAdapter {

    private ArrayList<Product> products = new ArrayList<>();


    static class MySellingsViewHolder extends RecyclerView.ViewHolder {
        ItemSellingsBinding binding;

        public MySellingsViewHolder(@NonNull ItemSellingsBinding itemSellingsBinding) {
            super(itemSellingsBinding.getRoot());
            binding = itemSellingsBinding;
        }
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

}

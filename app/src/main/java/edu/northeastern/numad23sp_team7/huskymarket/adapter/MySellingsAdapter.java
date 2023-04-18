package edu.northeastern.numad23sp_team7.huskymarket.adapter;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.northeastern.numad23sp_team7.databinding.ItemSellingsBinding;
import edu.northeastern.numad23sp_team7.huskymarket.listeners.MySellingsCardClickListener;
import edu.northeastern.numad23sp_team7.huskymarket.model.Product;
import edu.northeastern.numad23sp_team7.huskymarket.utils.Constants;
import edu.northeastern.numad23sp_team7.huskymarket.utils.ImageCodec;

public class MySellingsAdapter extends RecyclerView.Adapter<MySellingsAdapter.MySellingsViewHolder> {

    private final ArrayList<Product> products;
    private final MySellingsCardClickListener mySellingsCardClickListener;

    private static final String PRODUCT_IMAGE_STRING = "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";

    private static final String TAG = "selling adapter";

    public MySellingsAdapter(ArrayList<Product> products, MySellingsCardClickListener mySellingsCardClickListener) {
        this.products = products;
        this.mySellingsCardClickListener = mySellingsCardClickListener;
    }

    @NonNull
    @Override
    public MySellingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MySellingsViewHolder(
                ItemSellingsBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull MySellingsViewHolder holder, int position) {
        holder.setData(products.get(position));
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: " + products.size());
        return products.size();
    }


    class MySellingsViewHolder extends RecyclerView.ViewHolder {
        ItemSellingsBinding binding;

        public MySellingsViewHolder(@NonNull ItemSellingsBinding itemSellingsBinding) {
            super(itemSellingsBinding.getRoot());
            binding = itemSellingsBinding;
        }

        public void setData(Product product) {
            // TODO set image of real product image data
//            binding.imageProduct.setImageBitmap(ImageCodec.getDecodedImage(product.getImages().get(0)));
            binding.imageProduct.setImageBitmap(ImageCodec.getDecodedImage(PRODUCT_IMAGE_STRING));
            binding.textProductTitle.setText(product.getTitle());
            String formattedPrice = String.format("$ %.2f", product.getPrice());
            binding.textProductPrice.setText(formattedPrice);


            if (product.getStatus().equals(Constants.VALUE_PRODUCT_STATUS_SOLD)) {
                binding.imageSold.setVisibility(View.VISIBLE);
            } else {
                binding.imageSold.setVisibility(View.GONE);
            }

            // set on image click listener
            binding.imageProduct.setOnClickListener(v -> {
                mySellingsCardClickListener.onProductImageClick(product);
            });
            // set on setting click listener
            binding.iconSettings.setOnClickListener(v -> {
                mySellingsCardClickListener.onSettingClick(product);
            });

        }


    }


}

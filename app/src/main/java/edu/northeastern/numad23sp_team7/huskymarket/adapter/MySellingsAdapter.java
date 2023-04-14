package edu.northeastern.numad23sp_team7.huskymarket.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.northeastern.numad23sp_team7.R;
import edu.northeastern.numad23sp_team7.databinding.ItemSellingsBinding;
import edu.northeastern.numad23sp_team7.huskymarket.database.ProductDao;
import edu.northeastern.numad23sp_team7.huskymarket.listeners.MySellingsCardClickListener;
import edu.northeastern.numad23sp_team7.huskymarket.model.Product;
import edu.northeastern.numad23sp_team7.huskymarket.utils.Constants;

public class MySellingsAdapter extends RecyclerView.Adapter<MySellingsAdapter.MySellingsViewHolder> {

    private final ArrayList<Product> products;
    private final Context context;
    private final MySellingsCardClickListener mySellingsCardClickListener;
    private final static ProductDao productDao = new ProductDao();


    public MySellingsAdapter(ArrayList<Product> products, Context context, MySellingsCardClickListener mySellingsCardClickListener) {
        this.products = products;
        this.context = context;
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
            Bitmap testBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.sample);
            binding.imageProduct.setImageBitmap(testBitmap);

            binding.textProductTitle.setText(product.getTitle());
            String formattedPrice = String.format("$ %2f", product.getPrice());
            binding.textProductPrice.setText(formattedPrice);


            if (product.getStatus().equals(Constants.VALUE_PRODUCT_STATUS_SOLD)) {
                binding.imageSold.setVisibility(View.VISIBLE);
            } else {
                binding.imageSold.setVisibility(View.GONE);
            }

            // set on edit click listener
            binding.setAsSoldContainer.setOnClickListener(v -> {
                int position = getLayoutPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mySellingsCardClickListener.onEditClick(position);
                }
            });

        }


    }


}

package edu.northeastern.numad23sp_team7.huskymarket.adapter;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.numad23sp_team7.R;
import edu.northeastern.numad23sp_team7.databinding.ItemSearchResultCardBinding;
import edu.northeastern.numad23sp_team7.huskymarket.activities.ProductDetailActivity;
import edu.northeastern.numad23sp_team7.huskymarket.database.UserDao;
import edu.northeastern.numad23sp_team7.huskymarket.model.Product;
import edu.northeastern.numad23sp_team7.huskymarket.model.User;
import edu.northeastern.numad23sp_team7.huskymarket.utils.Constants;
import edu.northeastern.numad23sp_team7.huskymarket.utils.ImageCodec;
import edu.northeastern.numad23sp_team7.huskymarket.utils.PreferenceManager;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder> {

    private ArrayList<Product> products = new ArrayList<>();
    private User loggedInUser;
    private Context context;
    private UserDao userDao;
    private PreferenceManager preferenceManager;

    public SearchResultAdapter(ArrayList<Product> arr, Context context) {
        this.products = arr;
        this.context = context;
        this.userDao = new UserDao();
        preferenceManager = new PreferenceManager(context);
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemSearchResultCardBinding binding = ItemSearchResultCardBinding.inflate(layoutInflater, parent, false);
        return new SearchResultViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder holder, int position) {
        holder.binding.setProduct(products.get(position));

        List<String> images = products.get(position).getImages();
        if (!images.isEmpty()) {
            Bitmap imageBitMap = ImageCodec.getDecodedImage(images.get(0));
            if (imageBitMap != null) {
                holder.binding.imageViewHuskySearchResult.setImageBitmap(imageBitMap);
            }
        }

        // Redirect to product detail
        holder.binding.layoutHuskySearchResultContainer.setOnClickListener(view -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra(Constants.KEY_PRODUCT_ID, products.get(position).getProductId());
            context.startActivity(intent);
        });

        // Set bookmark icon
        String productId = products.get(position).getProductId();
        if (loggedInUser.getFavorites().contains(productId)) {
            setBookmarkedIcon(holder);
        } else {
            setNotBookmarkedIcon(holder);
        }

        // Bookmark click listener
        holder.binding.iconHuskySearchResultBookmark.setOnClickListener(view -> {
            if (loggedInUser.getFavorites().contains(productId)) {
                setNotBookmarkedIcon(holder);
                loggedInUser.getFavorites().remove(productId);
                userDao.removeItemFromFavorites(loggedInUser.getId(), productId);
            } else {
                setBookmarkedIcon(holder);
                loggedInUser.getFavorites().add(productId);
                userDao.addItemToFavorites(loggedInUser.getId(), productId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class SearchResultViewHolder extends RecyclerView.ViewHolder {
        ItemSearchResultCardBinding binding;

        public SearchResultViewHolder(@NonNull ItemSearchResultCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    public void updateLoggedInUser(User user) {
        this.loggedInUser = user;
    }

    public void setupLoggedInUser() {
        String userId = preferenceManager.getString(Constants.KEY_USER_ID);
        userDao.getUserById(userId, user -> {
            loggedInUser = user;
            this.notifyDataSetChanged();
        });
    }

    private void setBookmarkedIcon(SearchResultViewHolder holder) {
        int drawableId = R.drawable.ic_bookmarked;
        holder.binding.iconHuskySearchResultBookmark.setBackground(
                ContextCompat.getDrawable(holder.binding.iconHuskySearchResultBookmark.getContext(), drawableId));
    }

    private void setNotBookmarkedIcon(SearchResultViewHolder holder) {
        int drawableId = R.drawable.ic_bookmarked_not;
        holder.binding.iconHuskySearchResultBookmark.setBackground(
                ContextCompat.getDrawable(holder.binding.iconHuskySearchResultBookmark.getContext(), drawableId));
    }
}

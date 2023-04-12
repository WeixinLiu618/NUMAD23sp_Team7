package edu.northeastern.numad23sp_team7.huskymarket.adapter;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import edu.northeastern.numad23sp_team7.R;
import edu.northeastern.numad23sp_team7.databinding.ItemSearchResultCardBinding;
import edu.northeastern.numad23sp_team7.huskymarket.database.UserDao;
import edu.northeastern.numad23sp_team7.huskymarket.model.Product;
import edu.northeastern.numad23sp_team7.huskymarket.model.User;
import edu.northeastern.numad23sp_team7.huskymarket.activities.HuskyMainActivity;
import edu.northeastern.numad23sp_team7.huskymarket.utils.Constants;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder> {

    private ArrayList<Product> products = new ArrayList<>();
    private User loggedInUser;
    private Context context;
    private UserDao userDao;

    public SearchResultAdapter(ArrayList<Product> arr, Context context) {
        this.products = arr;
        this.context = context;
        this.userDao = new UserDao();
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemSearchResultCardBinding binding = ItemSearchResultCardBinding.inflate(layoutInflater, parent, false);
        return new SearchResultViewHolder(binding);
    }

    public Bitmap downloadImage(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.connect();
        InputStream input = connection.getInputStream();
        Bitmap bitmap = BitmapFactory.decodeStream(input);
        return bitmap;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder holder, int position) {
        holder.binding.setProduct(products.get(position));

        // Display image
        String imageUrl = "content://com.google.android.apps.photos.contentprovider/-1/1/content%3A%2F%2Fmedia%2Fexternal%2Fimages%2Fmedia%2F15/ORIGINAL/NONE/image%2Fjpeg/57271938";
        Picasso.get().load(Uri.parse(imageUrl)).into(holder.binding.imageViewHuskySearchResult);

        // Redirect to product detail
        holder.binding.layoutHuskySearchResultContainer.setOnClickListener(view -> {
            Intent intent = new Intent(context, HuskyMainActivity.class);
            intent.putExtra(Constants.INTENT_KEY_PRODUCT_DETAIL_ID, products.get(position).getProductId());
            context.startActivity(intent);
        });

        // Set bookmark icon
        String productId = products.get(position).getProductId();
        AtomicInteger saved = new AtomicInteger(isFavorite(productId));
        if (saved.get() == 1) {
            setBookmarkedIcon(holder);
        } else {
            setNotBookmarkedIcon(holder);
        }

        // Set bookmark icon click event listener
        holder.binding.iconHuskySearchResultBookmark.setOnClickListener(view -> {
            if (saved.get() == 1) {
                saved.set(0);
                loggedInUser.getFavorites().remove(productId);
                userDao.removeItemFromFavorites(loggedInUser.getId(), productId);
                setNotBookmarkedIcon(holder);
            } else if (saved.get() == 0) {
                saved.set(1);
                loggedInUser.getFavorites().add(productId);
                userDao.addItemToFavorites(loggedInUser.getId(), productId);
                setBookmarkedIcon(holder);
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

    private int isFavorite(String productId) {
        // Saved status
        // 0: not saved
        // 1: saved
        // 2: not looged-in™
        if (loggedInUser == null) {
            return 2;
        }

        if (loggedInUser.getFavorites().contains(productId)) {
            return 1;
        }

        return 0;
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

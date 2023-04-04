package edu.northeastern.numad23sp_team7.huskymarket.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import edu.northeastern.numad23sp_team7.R;
import edu.northeastern.numad23sp_team7.databinding.ItemSearchResultCardBinding;
import edu.northeastern.numad23sp_team7.huskymarket.database.UserDao;
import edu.northeastern.numad23sp_team7.huskymarket.model.Product;
import edu.northeastern.numad23sp_team7.huskymarket.model.User;

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

    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder holder, int position) {
        holder.binding.setProduct(products.get(position));

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

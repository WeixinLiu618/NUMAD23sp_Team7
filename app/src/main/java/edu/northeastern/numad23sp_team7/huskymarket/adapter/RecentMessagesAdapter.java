package edu.northeastern.numad23sp_team7.huskymarket.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.northeastern.numad23sp_team7.databinding.ItemRecentMessageBinding;
import edu.northeastern.numad23sp_team7.huskymarket.database.UserDao;
import edu.northeastern.numad23sp_team7.huskymarket.listeners.RecentMessageCardClickListener;
import edu.northeastern.numad23sp_team7.huskymarket.model.RecentMessageCard;

public class RecentMessagesAdapter extends RecyclerView.Adapter<RecentMessagesAdapter.RecentMessageViewHolder> {
    private final List<RecentMessageCard> recentMessageCards;
    private final RecentMessageCardClickListener recentMessageCardClickListener;
    private final UserDao userDao = new UserDao();


    public RecentMessagesAdapter(List<RecentMessageCard> recentMessageCards, RecentMessageCardClickListener recentMessageCardClickListener) {
        this.recentMessageCards= recentMessageCards;
        this.recentMessageCardClickListener = recentMessageCardClickListener;
    }

    @NonNull
    @Override
    public RecentMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecentMessageViewHolder(
                ItemRecentMessageBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull RecentMessageViewHolder holder, int position) {
        holder.setData(recentMessageCards.get(position));
    }

    @Override
    public int getItemCount() {
        return recentMessageCards.size();
    }


    class RecentMessageViewHolder extends RecyclerView.ViewHolder {
        ItemRecentMessageBinding binding;

        public RecentMessageViewHolder(ItemRecentMessageBinding itemRecentMessageBinding) {
            super(itemRecentMessageBinding.getRoot());
            binding = itemRecentMessageBinding;
        }

        public void setData(RecentMessageCard recentMessageCard) {
            binding.imageProfile.setImageBitmap(decodeProfileImageString(recentMessageCard.getDisplayedUserImage()));
            binding.textName.setText(recentMessageCard.getDisplayedUsername());
            binding.textRecentMessage.setText(recentMessageCard.getLastMessage());
            binding.textDateTime.setText(getDateTimeText(recentMessageCard.getTimestamp()));
            binding.getRoot().setOnClickListener(v -> {
                userDao.getUserById(recentMessageCard.getDisplayedUserId(), recentMessageCardClickListener::onDisplayedUserClick);
            });
        }

        private String getDateTimeText(Date date) {
            return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
        }
    }


    //     string -> bitmap
    private Bitmap decodeProfileImageString(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}

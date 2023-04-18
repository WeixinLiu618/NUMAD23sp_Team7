package edu.northeastern.numad23sp_team7.huskymarket.listeners;

import edu.northeastern.numad23sp_team7.huskymarket.model.Product;

public interface MySellingsCardClickListener {
    void onSettingClick(Product product);

    void onProductImageClick(Product product);
}

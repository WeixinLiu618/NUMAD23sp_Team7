package edu.northeastern.numad23sp_team7.huskymarket.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {

    private String id;
    private String username;
    private String email;
    private String profileImage;
    private List<String> favorites = new ArrayList<>(); //put productId in it


    public User() {
    }

    public User(String id, String username, String email, String profileImage, List<String> favorites) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.profileImage = profileImage;
        this.favorites= favorites;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public List<String> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<String> favorites) {
        this.favorites = favorites;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return this.id;
    }

}

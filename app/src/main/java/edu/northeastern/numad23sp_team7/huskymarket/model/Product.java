package edu.northeastern.numad23sp_team7.huskymarket.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Product implements Parcelable {
    private String productId;
    private String title;
    private String description;
    private String postUserId;
    private String location;
    private float condition;
    private String category;
    private List<String> images;
    private String status;
    private float price;
    private @ServerTimestamp Date timestamp;

    public Product() {
    }

    public Product(String productId, String title, String description, String postUserId, String location, float condition, String category, List<String> images, String status, float price) {
        this.productId = productId;
        this.title = title;
        this.description = description;
        this.postUserId = postUserId;
        this.location = location;
        this.condition = condition;
        this.category = category;
        this.images = images;
        this.status = status;
        this.price = price;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPostUserId() {
        return postUserId;
    }

    public void setPostUserId(String postUserId) {
        this.postUserId = postUserId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public float getCondition() {
        return condition;
    }

    public void setCondition(float condition) {
        this.condition = condition;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    protected Product(Parcel in) {
        productId = in.readString();
        title = in.readString();
        description = in.readString();
        postUserId = in.readString();
        location = in.readString();
        condition = in.readFloat();
        category = in.readString();
        images = new ArrayList<String>();
        in.readList(images, String.class.getClassLoader());
        status = in.readString();
        price = in.readFloat();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(productId);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(postUserId);
        dest.writeString(location);
        dest.writeFloat(condition);
        dest.writeString(category);
        dest.writeList(images);
        dest.writeString(status);
        dest.writeFloat(price);
    }

}

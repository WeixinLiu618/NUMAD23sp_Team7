package edu.northeastern.numad23sp_team7.huskymarket.model;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;
import java.util.List;

public class Product {
    private String productId;
    private String title;
    private String description;
    private String postUserId;
    private String location;
    private float condition;
    private String category;
    private String color;
    private List<String> images;
    private String material;
    private String status;
    private float price;
    private @ServerTimestamp Date timestamp;

    public Product() {
    }

    public Product(String productId, String title, String description, String postUserId, String location, float condition, String category, String color, List<String> images, String material, String status, float price) {
        this.productId = productId;
        this.title = title;
        this.description = description;
        this.postUserId = postUserId;
        this.location = location;
        this.condition = condition;
        this.category = category;
        this.color = color;
        this.images = images;
        this.material = material;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
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
}

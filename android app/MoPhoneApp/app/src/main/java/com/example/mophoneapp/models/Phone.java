package com.example.mophoneapp.models;

public class Phone {
    private String name;
    private double price;
    private int imageResId;         // ảnh từ drawable
    private String imageUrl;        // ảnh từ Internet
    private String description;
    private String seller;
    private int quantity;

    // Bắt buộc có constructor rỗng cho Firestore
    public Phone() {
    }

    // Constructor khi dùng ảnh từ drawable
    public Phone(String name, double price, int imageResId, String description, String seller, int quantity) {
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
        this.description = description;
        this.seller = seller;
        this.quantity = quantity;
    }

    // Constructor khi dùng ảnh từ URL
    public Phone(String name, double price, String imageUrl, String description, String seller, int quantity) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.description = description;
        this.seller = seller;
        this.quantity = quantity;
    }

    // Getters và Setters
    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getSeller() {
        return seller;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

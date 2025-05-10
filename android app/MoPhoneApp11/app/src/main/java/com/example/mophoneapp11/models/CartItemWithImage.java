package com.example.mophoneapp11.models;

public class CartItemWithImage extends CartItem {
    private String imageUrl;

    public CartItemWithImage(String productId, String productName, double price, int quantity, String imageUrl, String id) {
        super(productId, productName, price, quantity, imageUrl, id);
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

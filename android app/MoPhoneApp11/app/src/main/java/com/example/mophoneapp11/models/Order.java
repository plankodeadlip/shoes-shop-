package com.example.mophoneapp11.models;

public class Order {
    private String id;
    private double totalAmount;
    private String timestamp;
    private String status;
    private String paymentMethod;
    private String deliveryAddress;

    public Order() {} // Firestore cần constructor rỗng

    // Getters và Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
}

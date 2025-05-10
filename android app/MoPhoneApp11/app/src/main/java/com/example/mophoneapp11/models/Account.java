package com.example.mophoneapp11.models;

public class Account {
    private String uid;
    private String name;
    private String email;
    private String role;

    private String address;
    // Constructor rỗng bắt buộc (Firebase cần)
    public Account() {}

    public Account(String uid, String name, String email, String role,String address) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.role = role;
        this.role = address;
    }

    // Getter & Setter
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
}

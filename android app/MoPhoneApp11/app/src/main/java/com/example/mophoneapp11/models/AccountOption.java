package com.example.mophoneapp11.models;

public class AccountOption {
    private String title;
    private int iconResId;

    public AccountOption(String title, int iconResId) {
        this.title = title;
        this.iconResId = iconResId;
    }

    public String getTitle() {
        return title;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }
}

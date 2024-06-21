package com.example.skillnet.Models;

public class ReviewModel {
    private String categoryCode;
    private String clientCode;
    private String description;
    private String imageUrl;
    private int stars;
    private String title;
    private String date;

    public ReviewModel() {
    }

    // Constructor
    public ReviewModel(String categoryCode, String clientCode, String description, String imageUrl, int stars, String title, String date) {
        this.categoryCode = categoryCode;
        this.clientCode = clientCode;
        this.description = description;
        this.imageUrl = imageUrl;
        this.stars = stars;
        this.title = title;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    // Getters
    public String getCategoryCode() { return categoryCode; }
    public String getClientCode() { return clientCode; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public int getStars() { return stars; }
    public String getTitle() { return title; }
}


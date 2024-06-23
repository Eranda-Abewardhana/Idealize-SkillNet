package com.example.skillnet.Models;

public class ReviewModel {
    private String categoryCode;
    private String clientCode;
    private String description;
    private String imageUrl;
    private int stars;
    private String title;
    private String dateTime;
    private boolean review;

    public ReviewModel() {
    }

    // Constructor
    public ReviewModel(String categoryCode, String clientCode, String description, String imageUrl, int stars, String title, String dateTime, boolean review) {
        this.categoryCode = categoryCode;
        this.clientCode = clientCode;
        this.description = description;
        this.imageUrl = imageUrl;
        this.stars = stars;
        this.title = title;
        this.dateTime = dateTime;
        this.review = review;
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

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public boolean isReview() {
        return review;
    }

    public void setReview(boolean review) {
        this.review = review;
    }

    // Getters
    public String getCategoryCode() { return categoryCode; }
    public String getClientCode() { return clientCode; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public int getStars() { return stars; }
    public String getTitle() { return title; }
}


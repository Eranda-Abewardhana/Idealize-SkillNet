package com.example.skillnet.Models;

public class Post {
    String postCode;
    String userCode;
    String categoryCode;
    String title;
    double price;
    String location;
    String dateTime;
    String description;
    String mobileNo;
    String imageUrl;

    // No-argument constructor
    public Post() {
    }

    public Post(String postCode, String userCode, String categoryCode, String title, double price, String location, String dateTime, String description, String mobileNo, String imageUrl) {
        this.postCode = postCode;
        this.userCode = userCode;
        this.categoryCode = categoryCode;
        this.title = title;
        this.price = price;
        this.location = location;
        this.dateTime = dateTime;
        this.description = description;
        this.mobileNo = mobileNo;
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}

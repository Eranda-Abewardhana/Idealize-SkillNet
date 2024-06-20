package com.example.skillnet.Models;

public class PersonData {

    private String name;
    private String pCode;
    private String ImageUrl;
    private int stars;
    private boolean isworker;

    public boolean isIsworker() {
        return isworker;
    }

    public void setIsworker(boolean isworker) {
        this.isworker = isworker;
    }

    public PersonData() {

    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public PersonData(String name, String pCode, String imageUrl, boolean isworker) {
        this.name = name;
        this.pCode = pCode;
        this.ImageUrl = imageUrl;
        this.isworker = isworker;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getpCode() {
        return pCode;
    }

    public void setpCode(String pCode) {
        this.pCode = pCode;
    }
}

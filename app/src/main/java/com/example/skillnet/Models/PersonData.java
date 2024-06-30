package com.example.skillnet.Models;

public class PersonData {

    private String name;
    private String pCode;
    private String twitter;
    private String location;
    private String imageUrl;
    private int stars;
    private boolean isworker;
    private String fb;
    private String bio;
    private String insta;
    private String linkedin;
    private String skype;
    private String website;
    private String phone;

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

    public PersonData(String name, String pCode, String imageUrl, boolean isworker, String fb, String insta, String phone,
                      String linkedin, String skype, String website, String location, String bio,String twitter) {
        this.name = name;
        this.pCode = pCode;
        this.imageUrl = imageUrl;
        this.isworker = isworker;
        this.fb = fb;
        this.insta = insta;
        this.linkedin = linkedin;
        this.skype = skype;
        this.website = website;
        this.location = location;
        this.bio = bio;
        this.twitter = twitter;
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public String getFb() {
        return fb;
    }

    public void setFb(String fb) {
        this.fb = fb;
    }

    public String getInsta() {
        return insta;
    }

    public void setInsta(String insta) {
        this.insta = insta;
    }

    public String getLinkedin() {
        return linkedin;
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }

    public String getSkype() {
        return skype;
    }

    public void setSkype(String skype) {
        this.skype = skype;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}

package com.example.skillnet.Models;

public class Project {
    private String title;
    private String imageUrl;
    private String description;
    private String datetime;

    public Project() {
        // Default constructor required for calls to DataSnapshot.getValue(Project.class)
    }

    public Project(String title, String imageUrl, String description, String datetime) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.description = description;
        this.datetime = datetime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}

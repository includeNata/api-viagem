package com.api.apiviagem.model;

public class Destiny {

    private String name;
    private String location;
    private String slug;
    private String image;
    private String description;


    public Destiny() {
    }

    public Destiny(String name, String location, String slug, String image, String description) {
        this.name = name;
        this.location = location;
        this.slug = slug;
        this.image = image;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

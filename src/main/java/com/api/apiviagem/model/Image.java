package com.api.apiviagem.model;

public class Image {

    private String  touristic;
    private String url;

    public Image(String touristic, String url) {
        this.touristic = touristic;
        this.url = url;
    }

    public String getTouristic() {
        return touristic;
    }

    public void setTouristic(String touristic) {
        this.touristic = touristic;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Image{" +
                "touristic='" + touristic + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}

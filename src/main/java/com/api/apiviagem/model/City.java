package com.api.apiviagem.model;

import java.util.ArrayList;
import java.util.List;

public class City {

    private String culture;
    private String economy;
    private String transportation;
    private String sports;
    private String parksAndRecreation;
    private List<Image> images;
    public City() {
    }

    public City(String culture, String economy, String transportation, String sports, String parksAndRecreation, List<Image> images) {
        this.culture = culture;
        this.economy = economy;
        this.transportation = transportation;
        this.sports = sports;
        this.parksAndRecreation = parksAndRecreation;
        this.images = images;
    }

    public String getCulture() {
        return culture;
    }

    public void setCulture(String culture) {
        this.culture += culture;
    }

    public String getEconomy() {
        return economy;
    }

    public void setEconomy(String economy) {
        this.economy += economy;
    }

    public String getTransportation() {
        return transportation;
    }

    public void setTransportation(String transportation) {
        this.transportation += transportation;
    }


    public String getSports() {
        return sports;
    }

    public void setSports(String sports) {
        this.sports += sports;
    }

    public String getParksAndRecreation() {
        return parksAndRecreation;
    }

    public void setParksAndRecreation(String parksAndRecreation) {
        this.parksAndRecreation += parksAndRecreation;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "City{" +
                "culture='" + culture + '\'' +
                ", economy='" + economy + '\'' +
                ", transportation='" + transportation + '\'' +
                ", sports='" + sports + '\'' +
                ", parksAndRecreation='" + parksAndRecreation + '\'' +
                ", images=" + images +
                '}';
    }
}

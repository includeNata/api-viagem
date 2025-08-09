package com.api.apiviagem.model;

import javax.print.attribute.standard.Destination;
import java.util.List;

public class TravelStyle {

    private String slug;
    private String styleName;
    private String titlePage;
    private String descriptionPage;

    private List<Destination> destinations;

    public TravelStyle() {
    }

    public TravelStyle(String slug, String styleName, String titlePage, String descriptionPage, List<Destination> destinations) {
        this.slug = slug;
        this.styleName = styleName;
        this.titlePage = titlePage;
        this.descriptionPage = descriptionPage;
        this.destinations = destinations;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getStyleName() {
        return styleName;
    }

    public void setStyleName(String styleName) {
        this.styleName = styleName;
    }

    public String getTitlePage() {
        return titlePage;
    }

    public void setTitlePage(String titlePage) {
        this.titlePage = titlePage;
    }

    public String getDescriptionPage() {
        return descriptionPage;
    }

    public void setDescriptionPage(String descriptionPage) {
        this.descriptionPage = descriptionPage;
    }

    public List<Destination> getDestinations() {
        return destinations;
    }

    public void setDestinations(List<Destination> destinations) {
        this.destinations = destinations;
    }
}

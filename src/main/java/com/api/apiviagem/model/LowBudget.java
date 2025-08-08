package com.api.apiviagem.model;

public class LowBudget {

    private String destination;
    private Integer durationInDays;
    private Integer numberOfTravelers;

    public LowBudget() {
    }

    public LowBudget(String destination, Integer durationInDays, Integer numberOfTravelers) {
        this.destination = destination;
        this.durationInDays = durationInDays;
        this.numberOfTravelers = numberOfTravelers;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Integer getDurationInDays() {
        return durationInDays;
    }

    public void setDurationInDays(Integer durationInDays) {
        this.durationInDays = durationInDays;
    }

    public Integer getNumberOfTravelers() {
        return numberOfTravelers;
    }

    public void setNumberOfTravelers(Integer numberOfTravelers) {
        this.numberOfTravelers = numberOfTravelers;
    }

    @Override
    public String toString() {
        return "LowBudget{" +
                "destination='" + destination + '\'' +
                ", durationInDays=" + durationInDays +
                ", numberOfTravelers=" + numberOfTravelers +
                '}';
    }
}

package edu.MTSSimulation;

public class Location {

    private Double locationLatitude;
    private Double locationLongititude;

    public Location() {

    }

    public Location(Double locationLatitudeStartValue, Double locationLongititudeStartValue) {
        this.locationLatitude = locationLatitudeStartValue;
        this.locationLongititude = locationLongititudeStartValue;
    }

    public Double getLocationLatitude() {
        return this.locationLatitude;
    }

    public void setLocationLatitude(Double locationLatitudeValue) {
        this.locationLatitude = locationLatitudeValue;
    }

    public Double getLocationLongititude() {
        return this.locationLongititude;
    }

    public void setLocationLongititude(Double locationLongititudeValue) {
        this.locationLongititude = locationLongititudeValue;
    }

    public void printLocation() {
        System.out.println("------- Location Details -------");
        System.out.println("Latitude: " + this.getLocationLatitude());
        System.out.println("Longititude: " + this.getLocationLatitude());
        System.out.println("------------ END -------------------");
    }
}

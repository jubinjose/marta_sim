package edu.MTSSimulation;

public class TransitStop {

    private Integer transitStopID;
    private String transitStopName;
    private String transitStopAbbreviation;
    private Location transitStopLocation;
    private Integer transitStopRiders;
    private Boolean transitStopIsDepot;


    // default constructor
    public TransitStop() {
    }

    // constructor overload
    public TransitStop(Integer transitStopIDStartValue, String transitStopNameStartValue,
                       String transitStopAbbreviationStartValue, Location transitStopLocationStartValue,
                       Integer transitStopRidersStartValue, Boolean transitStopIsDepotStartValue) {
        this.transitStopID = transitStopIDStartValue;
        this.transitStopName = transitStopNameStartValue;
        this.transitStopAbbreviation = transitStopAbbreviationStartValue;
        this.transitStopLocation = transitStopLocationStartValue;
        this.transitStopRiders = transitStopRidersStartValue;
        this.transitStopIsDepot = transitStopIsDepotStartValue;
    }

    public Integer getTransitStopID() {
        return this.transitStopID;
    }

    public void setTransitStopID(Integer transitStopIDValue) {
        this.transitStopID = transitStopIDValue;
    }

    public String getTransitStopName() {
        return this.transitStopName;
    }

    public void setTransitStopName(String transitStopNameValue) {
        this.transitStopName = transitStopNameValue;
    }

    public String getTransitStopAbbreviation() {
        return this.transitStopAbbreviation;
    }

    public void setTransitStopAbbreviation(String transitStopAbbreviationValue) {
        this.transitStopAbbreviation = transitStopAbbreviationValue;
    }

    public Location getTransitStopLocation() {
        return this.transitStopLocation;
    }

    public void setTransitStopLocation(Location transitStopLocationValue) {
        this.transitStopLocation = transitStopLocationValue;
    }

    public int getTransitStopRiders() {
        return this.transitStopRiders;
    }

    public void setTransitStopRiders(Integer transitStopRidersValue) {
        this.transitStopRiders = transitStopRidersValue;
    }

    public Boolean getTransitStopIsdepot() {
        return this.transitStopIsDepot;
    }

    public void setTransitStopIsdepot(Boolean transitStopIsDepotValue) {
        this.transitStopIsDepot = transitStopIsDepotValue;
    }

    public void printTransitStop() {
        System.out.println("------- Transit Stop Details -------");
        System.out.println("ID: " + this.transitStopID);
        System.out.println("Name: " + this.transitStopName);
        System.out.println("Abbreviation: " + this.transitStopAbbreviation);
        System.out.println("Latitude: " + this.transitStopLocation.getLocationLatitude());
        System.out.println("Longititude: " + this.transitStopLocation.getLocationLongititude());
        System.out.println("Riders: " + this.transitStopRiders);
        System.out.println("IsDepot: " + this.transitStopIsDepot);
        System.out.println("------------ END -------------------");
    }
}




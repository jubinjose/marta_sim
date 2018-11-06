package edu.MTSSimulation;

import java.util.*;

public class Route {

    private Integer routeID;
    private Integer routeNumber;
    private String routeName;
    private Integer routeTransitStopIndex = 0;
    private Map<Integer, TransitStop> routeStops = new HashMap<Integer, TransitStop>();

    // default constructor
    public Route() {
    }

    // constructor overload
    public Route(Integer routeIDStartValue, Integer routeNumberStartValue, String routeNameStartValue) {
        this.routeID = routeIDStartValue;
        this.routeNumber = routeNumberStartValue;
        this.routeName = routeNameStartValue;
    }

    public Integer getRouteID() {
        return this.routeID;
    }

    public void setRouteID(Integer routeIDValue) {
        this.routeID = routeIDValue;
    }

    public Integer getRouteNumber() {
        return this.routeNumber;
    }

    public void setRouteNumber(Integer routeNumberValue) {
        this.routeNumber = routeNumberValue;
    }

    public String getRouteName() {
        return this.routeName;
    }

    public void setRouteName(String routeNameValue) {
        this.routeName = routeNameValue;
    }

    public Map<Integer, TransitStop> getRouteStops() {
        return this.routeStops;
    }

    public TransitStop getRouteTransitStop(Integer routeStopIndex) {
        return this.routeStops.get(routeStopIndex);
    }

    public Location getRouteTransitStopLocation(Integer routeIndex) {
        return this.routeStops.get(routeIndex).getTransitStopLocation();
    }

    public void addRouteStop(TransitStop transitStop) {
        if(this.routeStops != null){
            this.routeStops.put(routeTransitStopIndex,transitStop);
            // increment route stop index
            routeTransitStopIndex = routeTransitStopIndex + 1;
        }
    }

    public void printRoute() {
        System.out.println("------- Route Details -------");
        System.out.println("ID: " + this.getRouteID());
        System.out.println("Number: " + this.getRouteNumber());
        System.out.println("Name: " + this.getRouteName());


        for (Map.Entry<Integer, TransitStop> item : this.getRouteStops().entrySet()) {
            TransitStop routeTransitStopObject = item.getValue();

            System.out.println("--- Route Transit Stop: " + routeTransitStopObject.getTransitStopID());
            System.out.println("Name: " + routeTransitStopObject.getTransitStopName());
            System.out.println("Abbreviation: " + routeTransitStopObject.getTransitStopAbbreviation());
            System.out.println("Latitude: " + routeTransitStopObject.getTransitStopLocation().getLocationLatitude());
            System.out.println("Longititude: " + routeTransitStopObject.getTransitStopLocation().getLocationLongititude());
            System.out.println("Riders: " + routeTransitStopObject.getTransitStopRiders());
            System.out.println("IsDepot: " + routeTransitStopObject.getTransitStopIsdepot());
        }
        System.out.println("------------ END ------------");

    }
}

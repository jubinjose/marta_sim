package edu.MTSSimulation;

public class Bus {

    private Integer busID;
    private Route busRoute;
    private Location busLocation;
    private Integer busRiders;
    private Integer busRidersCapacity;
    private Double busFuel;
    private Double busFuelCapacity;
    private Double busSpeed;
    private Integer busCurrentRouteTransitStopIndex;
    private Integer busNextRouteTransitStopIndex;

    // default constructor
    public Bus() {
    }

    // constructor overload
    public Bus(Integer busIDStartValue, Route busRouteStartValue,
               Location busLocationStartValue, Integer busRidersStartValue, Integer busRidersCapacityStartValue,
               Double busFuelStartValue, Double busFuelCapacityStartValue, Double busSpeedStartValue,
               //TransitStop busCurrentTransitStopStartValue, TransitStop busNextTransitStopStartValue,
               Integer busCurrentRouteTransitStopIndexStartValue, Integer busNextRouteTransitStopIndexStartValue) {

        this.busID = busIDStartValue;
        this.busRoute = busRouteStartValue;
        this.busLocation = busLocationStartValue;
        this.busRiders = busRidersStartValue;
        this.busRidersCapacity = busRidersCapacityStartValue;
        this.busFuel = busFuelStartValue;
        this.busFuelCapacity = busFuelCapacityStartValue;
        this.busSpeed = busSpeedStartValue;
        //this.busCurrentTransitStop = busCurrentTransitStopStartValue;
        //this.busNextTransitStop = busNextTransitStopStartValue;
        this.busCurrentRouteTransitStopIndex = busCurrentRouteTransitStopIndexStartValue;
        this.busNextRouteTransitStopIndex = busNextRouteTransitStopIndexStartValue;
    }

    public Integer getBusID() {
        return this.busID;
    }

    public void setBusID(Integer busIDValue) {
        this.busID = busIDValue;
    }

    public Route getBusRoute() {
        return this.busRoute;
    }

    public void setBusRoute(Route busRouteValue) {
        this.busRoute = busRouteValue;
    }

    public Location getBusLocation() {
        return this.busLocation;
    }

    public void setBusLocation(Location busLocationValue) {
        this.busLocation = busLocationValue;
    }

    public Integer getBusRiders() {
        return this.busRiders;
    }

    public void incrementBusRiders(Integer busRidersValue) {
        this.busRiders = this.busRiders + busRidersValue;
    }

    public void decrementBusRiders(Integer busRidersValue) {
        this.busRiders = this.busRiders - busRidersValue;
    }

    public Integer getBusRiderCapacity() {
        return this.busRidersCapacity;
    }

    public void setBusRiderCapacity(Integer busRiderCapacityValue) {
        this.busRidersCapacity = busRiderCapacityValue;
    }

    public Double getBusFuel() {
        return this.busFuel; }


    public void incrementBusFuel(Double busFuelValue) {
        this.busFuel = this.busFuel + busFuelValue;
    }

    public void decrementBusFuel(Double busFuelValue) {
        this.busFuel = this.busFuel - busFuelValue;
    }

    public Double getBusFuelCapacity() {
        return this.busFuelCapacity;
    }

    public void setBusFuelCapacity(Double busFuelCapacityValue) {
        this.busFuelCapacity = busFuelCapacityValue;
    }

    public Double getBusSpeed() {
        return this.busSpeed;
    }

    public void setBusSpeed(Double busSpeedValue) {
        this.busSpeed = busSpeedValue;
    }

    public Integer getBusCurrentRouteTransitStopIndex() {
        return this.busCurrentRouteTransitStopIndex;
    }

    public void setBusCurrentRouteTransitStopIndex(Integer busCurrentRouteTransitStopIndexValue) {
        this.busCurrentRouteTransitStopIndex = busCurrentRouteTransitStopIndexValue;
    }

    public Integer getBusNextRouteTransitStopIndex() {
        return this.busNextRouteTransitStopIndex;
    }

    public void setBusNextRouteTransitStopIndex(Integer busNextRouteTransitStopIndexValue) {
        this.busNextRouteTransitStopIndex = busNextRouteTransitStopIndexValue;
    }

    public void printBus() {
        System.out.println("------- Bus Details -------");
        System.out.println("ID: " + this.getBusID());
        System.out.println("Route ID: " + this.busRoute.getRouteID());
        System.out.println("Latitude: " + this.busLocation.getLocationLatitude());
        System.out.println("Longititude: " + this.busLocation.getLocationLongititude());
        System.out.println("Rider: " + this.getBusRiders());
        System.out.println("Rider Capacity: " + this.getBusRiderCapacity());
        System.out.println("Fuel: " + this.getBusFuel());
        System.out.println("Fuel Capacity: " + this.getBusFuelCapacity());
        System.out.println("Speed: " + this.getBusSpeed());
        //System.out.println("Current Transit Stop Latitude: " + this.getBusCurrentTransitStop().getTransitStopLocation().getLocationLatitude());
        //System.out.println("Current Transit Stop Longititude: " + this.getBusCurrentTransitStop().getTransitStopLocation().getLocationLongititude());
        //System.out.println("Next Transit Stop Latitude: " + this.getBusNextTransitStop().getTransitStopLocation().getLocationLatitude());
        //System.out.println("Next Transit Stop Longititude: " + this.getBusNextTransitStop().getTransitStopLocation().getLocationLongititude());
        System.out.println("Current Route Transit Stop Index: " + this.getBusCurrentRouteTransitStopIndex());
        System.out.println("Next Route Transit Stop Index: " + this.getBusNextRouteTransitStopIndex());
        System.out.println("------------ END ------------");
    }
}


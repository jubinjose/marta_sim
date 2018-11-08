import java.util.Date;

public class Bus {
    private Integer busID ;
    private Integer currentStopId ;
    private Integer passengerCapacity ;
    private Integer initialFuel ;
    private Integer fuelCapacity ;
    private Integer speed ;
    private Integer passengerCountinBus ;
    private Route route ;
    private Integer currentTimePointer ;
    private Integer startingLocation  ;


    public Integer getCurrentTimeTrack() {
        return currentTimePointer;
    }

    public void setCurrentTimeTrack(Integer currentTimeTrack) {
        this.currentTimePointer = currentTimeTrack;
    }



    public Integer getBusID() {
        return busID;
    }

    public void setBusID(Integer busID) {
        this.busID = busID;
    }

    public Integer getCurrentStopId() {
        return currentStopId;
    }

    public void setCurrentStopId(Integer currentStopId) {
        this.currentStopId = currentStopId;
    }

    public Integer getPassengerCapacity() {
        return passengerCapacity;
    }

    public void setPassengerCapacity(Integer passengerCapacity) {
        this.passengerCapacity = passengerCapacity;
    }

    public Integer getInitialFuel() {
        return initialFuel;
    }

    public void setInitialFuel(Integer initialFuel) {
        this.initialFuel = initialFuel;
    }

    public Integer getFuelCapacity() {
        return fuelCapacity;
    }

    public void setFuelCapacity(Integer fuelCapacity) {
        this.fuelCapacity = fuelCapacity;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    public Integer getPassengerCountinBus() {
        return passengerCountinBus;
    }

    public void setPassengerCountinBus(Integer passengerCountinBus) {
        this.passengerCountinBus = passengerCountinBus;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }


    public Integer getStartingLocation() {
        return startingLocation;
    }

    public void setStartingLocation(Integer startingLocation) {
        this.startingLocation = startingLocation;
    }

    public Integer getSpeed() {
        return speed ;
    }

    public Bus(Integer ID, Route busRoute,  Integer buslocation, Integer  passengerCount, Integer capacity, Integer busInitialFuel, Integer busfuelCapacity, Integer busSpeed)
    {

        this.route = new Route() ;

        this.busID  = ID ;
        this.route.copy(busRoute) ;
        this.currentStopId = 0 ;
        this.startingLocation = buslocation ; //starting Location Index with in Route-First location as 0
        this.route.setCurrentRouteStopIndex(buslocation);
        this.passengerCapacity = capacity ;
        this.passengerCountinBus = passengerCount ;
        this.initialFuel = busInitialFuel ;
        this.fuelCapacity = busfuelCapacity ;
        this.speed = busSpeed ;
        this.currentTimePointer  = 0 ;
        this.startingLocation   = 0 ;
    }

}

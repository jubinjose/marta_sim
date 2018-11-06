public class Bus{

    private int busId;
    public int getBusId(){
        return busId;
    }
    public void setBusId(int val){
        busId = val;
    }

    private String name;
    public String getName(){
        return name;
    }
    public void setName(String val){
        name = val;
    }

    private Route route;
    public Route getRoute(){
        return route;
    }
    public void setRoute(Route val){
        route = val;
    }

    private Stop currentStop;
    public Stop getCurrentStop(){
        return currentStop;
    }
    public void setCurrentStop(Stop val){
        currentStop = val;
    }

    private int nextStopIndex;
    public int getNextStopIndex(){
        return nextStopIndex;
    }
    public void setNextStopIndex(int val){
        nextStopIndex = val;
    }

    private int arrivalTime;
    public int getArrivaltime(){
        return arrivalTime;
    }
    public void setArrivaltime(int val){
        arrivalTime = val;
    }

    private int passengerCount;
    public int getPassengerCount(){
        return passengerCount;
    }
    public void setPassengerCount(int val){
        passengerCount = val;
    }

    private int passengerCapacity;
    public int getPassengerCapacity(){
        return passengerCapacity;
    }
    public void setPassengerCapacity(int val){
        passengerCapacity = val;
    }

    private int speed;
    public int getSpeed(){
        return speed;
    }
    public void setSpeed(int val){
        speed = val;
    }


    private int fuel;
    public int getFuel(){
        return fuel;
    }
    public void setFuel(int val){
        fuel = val;
    }

    private int fuelCapacity;
    public int getFuelCapacity(){
        return fuelCapacity;
    }
    public void setFuelCapacity(int val){
        fuelCapacity = val;
    }

    public Bus(int busId, Route route, int stopIndex, int passengerCount, int passengerCapacity, int initialFuel, int fuelCapacity, int speed ) throws Exception {

        if (stopIndex <0 || stopIndex >= route.getStopList().size()){
            throw new Exception("invalid Stop Index");
        }

        this.busId = busId;
        this.route = route;
        this.currentStop = route.getStopAtindex(stopIndex);
        this.nextStopIndex = stopIndex;
        this.passengerCount = passengerCount;
        this.passengerCapacity = passengerCapacity;
        this.fuel = initialFuel;
        this.fuelCapacity = fuelCapacity;
        this.speed = speed;
    }

    public String displayInfo(){
        return "bus" + name;
    }
}
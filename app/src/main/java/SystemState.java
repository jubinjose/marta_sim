public class SystemState{

    private int busId;
    public int getBusId(){
        return busId;
    }
    public void setBusId(int val){
        busId = val;
    }

    private int busArrivalTime;
    public int getBusArrivaltime(){
        return busArrivalTime;
    }
    public void setBusArrivaltime(int val){
        busArrivalTime = val;
    }

    private int busCapacity;
    public int getBusCapacity(){
        return busCapacity;
    }
    public void setBusCapacity(int val){
        busCapacity = val;
    }

    private Stop busCurrentStop;
    public Stop getBusCurrentStop(){
        return busCurrentStop;
    }
    public void setBusCurrentStop(Stop val){
        busCurrentStop = val;
    }

    private int busNextStopIndex;
    public int getBusNextStopIndex(){
        return busNextStopIndex;
    }
    public void setBusNextStopIndex(int val){
        busNextStopIndex = val;
    }

    private int busRiderCount;
    public int getRiderCount(){
        return busRiderCount;
    }
    public void setRiderCount(int val){
        busRiderCount = val;
    }

    private BusRoute busRoute;
    public BusRoute getBusRoute(){
        return busRoute;
    }
    public void setBusRoute(BusRoute val){
        busRoute = val;
    }

    private int busSpeed;
    public int getBusSpeed(){
        return busSpeed;
    }
    public void setBusSpeed(int val){
        busSpeed = val;
    }

    public SystemState(int busId, int busArrivalTime, int busCapacity, 
                    Stop busCurrentStop, int busNextStopIndex, int busRiderCount,
                    BusRoute busRoute, int busSpeed) {

        this.busId = busId;
        this.busArrivalTime = busArrivalTime;
        this.busCapacity = busCapacity;
        this.busCurrentStop = busCurrentStop;
        this.busNextStopIndex = busNextStopIndex;
        this.busRiderCount = busRiderCount;   
        this.busRoute = busRoute;
        this.busSpeed = busSpeed; 
    }

    public String toString(){
        return String.format("b:%d->s:%d@%d//p:%d", this.busId, this.busRoute.getStopAtindex(this.busNextStopIndex).getStopId(), 
                        this.busArrivalTime, this.busRiderCount);
    }
}


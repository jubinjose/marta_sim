public class SystemState{

    private int busId;
    public int getBusId(){
        return busId;
    }
    public void setBusId(int val){
        busId = val;
    }
    private int busRiderCount;
    public int getRiderCount(){
        return busRiderCount;
    }
    public void setRiderCount(int val){
        busRiderCount = val;
    }

    private Stop busAtStop;
    public Stop getBusAtStop(){
        return busAtStop;
    }
    public void setBusAtStop(Stop val){
        busAtStop = val;
    }

    private int  riderWaitingCount;
    public int getRiderWaitingCount(){
        return riderWaitingCount;
    }
    public void setRiderWaitingCount(int val){
        riderWaitingCount = val;
    }

    private int busCapacity;
    public int getBusCapacity(){
        return busCapacity;
    }
    public void setBusCapacity(int val){
        busCapacity = val;
    }

    private int busSpeed;
    public int getBusSpeed(){
        return busSpeed;
    }
    public void setBusSpeed(int val){
        busSpeed = val;
    }

    private BusRoute busRoute;
    public BusRoute getBusRoute(){
        return busRoute;
    }
    public void setBusRoute(BusRoute val){
        busRoute = val;
    }
    
    private int busNextStopIndex;
    public int getBusNextStopIndex(){
        return busNextStopIndex;
    }
    public void setBusNextStopIndex(int val){
        busNextStopIndex = val;
    }

    private int busArrivalTime;
    public int getBusArrivaltime(){
        return busArrivalTime;
    }
    public void setBusArrivaltime(int val){
        busArrivalTime = val;
    }

    
    public SystemState(int busId, int busRiderCount, Stop busAtStop, int riderWaitingCount, int busCapacity, BusRoute busRoute, 
    int busSpeed, int busNextStopIndex, int busArrivalTime) {

        this.busId = busId;
        this.busRiderCount = busRiderCount;
        this.busAtStop = busAtStop;
        this.riderWaitingCount = riderWaitingCount;
        this.busCapacity = busCapacity;
        this.busRoute = busRoute;
        this.busSpeed = busSpeed;
        this.busNextStopIndex = busNextStopIndex;
        this.busArrivalTime = busArrivalTime;
    }

    public String toString(){
        return String.format("b:%d->s:%d@%d//p:%d", this.busId, this.busRoute.getStopAtindex(this.busNextStopIndex).getStopId(), 
                        this.busArrivalTime, this.busRiderCount);
    }
}


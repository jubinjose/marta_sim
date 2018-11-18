public class Bus implements Cloneable{

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

    private BusRoute route;
    public BusRoute getRoute(){
        return route;
    }
    public void setRoute(BusRoute val){
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

    private int riderCount;
    public int getRiderCount(){
        return riderCount;
    }
    public void setRiderCount(int val){
        riderCount = val;
    }

    private int capacity;
    public int getCapacity(){
        return capacity;
    }
    public void setCapacity(int val){
        capacity = val;
    }

    private int speed;
    public int getSpeed(){
        return speed;
    }
    public void setSpeed(int val){
        speed = val;
    }

    public Bus(int busId, BusRoute route, int stopIndex, int riderCapacity, int speed ) throws Exception {

        if (stopIndex <0 || stopIndex >= route.getStopList().size()){
            throw new Exception("invalid Stop Index");
        }

        this.busId = busId;
        this.route = route;
        this.currentStop = route.getStopAtindex(stopIndex);
        this.nextStopIndex = stopIndex;
        this.capacity = riderCapacity;
        this.speed = speed;
    }

    public String toString(){
        return String.format("b:%d->s:%d@%d//p:%d", this.busId, this.route.getStopAtindex(this.nextStopIndex).getStopId(), 
                        this.arrivalTime, this.riderCount);
    }

    public Object clone() throws CloneNotSupportedException{  
        return super.clone();  
    }  
}


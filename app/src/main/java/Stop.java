import java.util.Random;

public class Stop{

    private int stopId;
    public int getStopId(){
        return stopId;
    }
    public void setStopId(int val){
        stopId = val;
    }

    private String name;
    public String getName(){
        return name;
    }
    public void setName(String val){
        name = val;
    }

    private double latitude;
    public double getLatitude(){
        return latitude;
    }
    public void setLatitude(double val){
        latitude = val;
    }

    private double longitude;
    public double getLongitude(){
        return longitude;
    }
    public void setLongitude(double val){
        longitude = val;
    }

    private int waitingCount;
    public int getWaitingCount(){
        return waitingCount;
    }
    public void setWaitingCount(int val){
        waitingCount = val;
    }

    private int ridersArriveHigh;
    public int getRidersArriveHigh(){
        return ridersArriveHigh;
    }
    public void setRidersArriveHigh(int val){
        ridersArriveHigh = val;
    }

    private int ridersArriveLow;
    public int getRidersArriveLow(){
        return ridersArriveLow;
    }
    public void setRidersArriveLow(int val){
        ridersArriveLow = val;
    }

    private int ridersOffHigh;
    public int getRidersOffHigh(){
        return ridersOffHigh;
    }
    public void setRidersOffHigh(int val){
        ridersOffHigh = val;
    }

    private int ridersOffLow;
    public int getRidersOffLow(){
        return ridersOffLow;
    }
    public void setRidersOffLow(int val){
        ridersOffLow = val;
    }

    private int ridersOnHigh;
    public int getRidersOnHigh(){
        return ridersOnHigh;
    }
    public void setRidersOnHigh(int val){
        ridersOnHigh = val;
    }

    private int ridersOnLow;
    public int getRidersOnLow(){
        return ridersOnLow;
    }
    public void setRidersOnLow(int val){
        ridersOnLow = val;
    }

    private int ridersDepartHigh;
    public int getRidersDepartHigh(){
        return ridersDepartHigh;
    }
    public void setRidersDepartHigh(int val){
        ridersDepartHigh = val;
    }

    private int ridersDepartLow;
    public int getRidersDepartLow(){
        return ridersDepartLow;
    }
    public void setRidersDepartLow(int val){
        ridersDepartLow = val;
    }

    public Stop(int aStopid, String aName, double aLatitude, double aLongitude){
        stopId = aStopid;
        name = aName;
        latitude = aLatitude;
        longitude = aLongitude;
    }

    private int generateRandomInteger(int lower, int upper){
        return new Random().ints(lower, (upper + 1)).limit(1).findFirst().getAsInt();
    }

    public int getRidersArrive(){
        return generateRandomInteger(ridersArriveLow, ridersArriveHigh);
    }

    public int getRidersOff(){
        return generateRandomInteger(ridersOffLow, ridersOffHigh);
    }

    public int getRidersOn(){
        return generateRandomInteger(ridersOnLow, ridersOnHigh);
    }

    public int getRidersDepart(){
        return generateRandomInteger(ridersDepartLow, ridersDepartHigh);
    }
   
}
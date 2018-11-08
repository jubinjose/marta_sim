import java.util.Date ;

public class Stop {

    private Integer stopID ;
    private String stopName ;
    private Location location ;
    private Integer passengerCount ;
    //private Date busArrivalTime ;
    //private Date busDepartureTime ;
    private Integer distanceNextstop ;
    private Integer timeNextstop ;
    private Integer nextStopid ;

    protected  Stop() {
        location =new Location() ;
    }

    public  Stop(Integer stopid, String Name, Integer passCount, Double xlocation, Double ylocation)
    {
        this.stopID = stopid ;
        this.stopName = Name ;
        this.passengerCount = passCount ;
        location =new Location() ;
        location.latitude = xlocation ;
        location.longitude = ylocation ;
        this.distanceNextstop = -1 ;
        this.timeNextstop = -1 ;
        this.nextStopid = -1 ;

    }


    public Integer getStopID() {
        return stopID;
    }

    public void setStopID(Integer stopID) {
        this.stopID = stopID;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Integer getPassengerCount() {
        return passengerCount;
    }

    public void setPassengerCount(Integer passengerCount) {
        this.passengerCount = passengerCount;
    }
/*
    public Date getBusArrivalTime() {
        return busArrivalTime;
    }

    public void setBusArrivalTime(Date busArrivalTime) {
        this.busArrivalTime = busArrivalTime;
    }

    public Date getBusDepartureTime() {
        return busDepartureTime;
    }

    public void setBusDepartureTime(Date busDepartureTime) {
        this.busDepartureTime = busDepartureTime;
    }
*/
    public Integer getDistanceNextstop() {
        return distanceNextstop;
    }

    public void setDistanceNextstop(Integer distanceNexttstop) {
        this.distanceNextstop = distanceNexttstop;
    }

    public Integer getTimeNextstop() {
        return timeNextstop;
    }

    public void setTimeNextstop(Integer timeNextstop) {
        this.timeNextstop = timeNextstop;
    }

    public Integer getNextStopid() {
        return nextStopid;
    }

    public void setNextStopid(Integer nextStopid) {
        this.nextStopid = nextStopid;
    }
    public void clone(Stop stop1)
    {
        this.stopID = stop1.stopID ;
        this.stopName = stop1.stopName ;
        this.passengerCount = stop1.passengerCount ;
        this.location.latitude = stop1.location.latitude ;
        this.location.longitude = stop1.location.longitude  ;
        this.distanceNextstop = stop1.distanceNextstop ;
        this.timeNextstop = stop1.timeNextstop ;
        this.nextStopid = stop1.nextStopid ;
    }

}

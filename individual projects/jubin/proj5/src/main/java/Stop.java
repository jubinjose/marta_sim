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

    public Stop(int aStopid, String aName, double aLatitude, double aLongitude){
        stopId = aStopid;
        name = aName;
        latitude = aLatitude;
        longitude = aLongitude;
    }
    
}
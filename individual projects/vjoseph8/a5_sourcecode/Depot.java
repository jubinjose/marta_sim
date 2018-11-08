import java.util.Date;

public class Depot {
    private boolean depot = true ;
    private Integer depotID ;
    private String depotName ;
    private Location location ;

    public Depot(Integer depotid, String strDepotName, Double xlocation, Double ylocation){
        this.depotID = depotid ;
        this.depotName = strDepotName ;
        location = new Location() ;
        location.latitude = xlocation ;
        location.longitude = ylocation ;
    }

    public boolean isDepot() {
        return depot;
    }

    public void setDepot(boolean depot) {
        this.depot = depot;
    }

    public Integer getDepotID() {
        return depotID;
    }

    public void setDepotID(Integer depotID) {
        this.depotID = depotID;
    }

    public String getDepotName() {
        return depotName;
    }

    public void setDepotName(String depotName) {
        this.depotName = depotName;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }




}

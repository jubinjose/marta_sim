import java.util.*;

public class BusRoute{

    private List<Stop> stopList = new ArrayList<Stop>();
    public List<Stop> getStopList(){
        return stopList;
    }

    private int routeId;
    public int getRouteId(){
        return routeId;
    }
    public void setRouteId(int val){
        routeId = val;
    }

    private int routeNumber;
    public int getRouteNumber(){
        return routeNumber;
    }
    public void setRouteNumber(int val){
        routeNumber = val;
    }

    private String name;
    public String getName(){
        return name;
    }
    public void setName(String val){
        name = val;
    }

    public BusRoute(int aRouteid, int aRouteNumber, String aName){
        routeId = aRouteid;
        routeNumber = aRouteNumber;
        name = aName;
    }

    public void addStop(Stop stop){
        stopList.add(stop);
    }

    public Stop getStopAtindex(int index){
        return index>=0 && index<stopList.size()? stopList.get(index):null;
    }

    public int getNextStopIndex(int currentStopIndex){
        return currentStopIndex == (stopList.size() - 1)? 0 : currentStopIndex + 1;
    }
}

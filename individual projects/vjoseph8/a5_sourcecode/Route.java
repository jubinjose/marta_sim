import java.util.Date;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;

public class Route {
    private Integer routeID ;
    private Integer number ;
    private String  name ;
    private HashMap<Integer, Stop> routeStops;
    private Integer InitialStopId  ;
    private Integer currentRouteStopIndex ;

    public Route(){
        routeStops = new HashMap<Integer, Stop>();
    }

    public Route(Integer ID, Integer routeNumber, String routeName){
        this.routeID = ID ;
        this.number = routeNumber ;
        this.name = routeName ;
        routeStops = new HashMap<Integer, Stop>();
        currentRouteStopIndex = 0 ;
    }

    public Integer getRouteID() {
        return routeID;
    }

    public void setRouteID(Integer routeID) {
        this.routeID = routeID;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<Integer, Stop> getRouteStops() {
        return routeStops;
    }

    public void setRouteStops(HashMap<Integer, Stop> routeStops) {
        this.routeStops = routeStops;
    }

    public Integer getInitialStopId() {
        return InitialStopId;
    }

    public void setInitialStopId(Integer initialStopId) {
        InitialStopId = initialStopId;
    }

    public Integer getCurrentRouteStopIndex() {
        return currentRouteStopIndex;
    }

    public void setCurrentRouteStopIndex(Integer currentRouteStopIndex) {
        this.currentRouteStopIndex = currentRouteStopIndex;
    }

    public void extendRoute(Integer routeID, Stop  stop2)  {
        if(routeStops.size() > 0){
            //Round Robbin to first stop
            stop2.setNextStopid(routeStops.get(0).getStopID() );

            Stop stop1= routeStops.get(routeStops.size() -1) ;
            stop1.setNextStopid(stop2.getStopID());
            Double distance = getDistanceBetweenStops(stop1 , stop2) ;
            stop1.setDistanceNextstop(distance.intValue() );
        }
        routeStops.put(routeStops.size(),stop2) ; //Key ois increasing number
    }
    public Double getDistanceBetweenStops(Stop stop1 , Stop stop2) {
        //Double distance  = 70.0D * Math.sqrt(   Math.pow((stop1.getLocation().longitude - stop2.getLocation().longitude), 2.0D)
        //        +  Math.pow((stop1.getLocation().latitude - stop2.getLocation().latitude), 2.0D)   );
        Double distance  = 70.0 * Math.sqrt(   Math.pow((stop1.getLocation().longitude - stop2.getLocation().longitude), 2.0)
                +  Math.pow((stop1.getLocation().latitude - stop2.getLocation().latitude), 2.0)   );
        return distance ;
    }
    public Integer getTravelTime(Integer speed, Stop stop1 , Stop stop2) {
        Integer distance = stop1.getDistanceNextstop() ;
        if(distance == -1){
            Double distance1  = getDistanceBetweenStops(stop1, stop2) ;
            distance = distance1.intValue() ;
            stop1.setDistanceNextstop(distance) ;
        }
        Integer travelTime = 0;
       //if (bus != null) {
            /* Integer speed = bus.getSpeed(); */
            if (speed != 0) {
                travelTime = 1 + distance * 60 / speed;
            }
        //}
        return travelTime;
    }

    public Stop getCurrentStop() {
        if (routeStops.size() == 0 )
            return null ;
        else
            return routeStops.get(currentRouteStopIndex) ;
    }
    public void setNextStopIndex() {
        if (routeStops.size()  ==  0)
            return  ;
        else if (routeStops.size() == currentRouteStopIndex+1 )
                currentRouteStopIndex = 0 ; //goes back to first one
        else if (routeStops.size() > currentRouteStopIndex+1 )
            currentRouteStopIndex++ ; ;
        return  ;
    }
    public Stop getNextStop(Integer currentIndex) {
        if (routeStops.size()  ==  0)
            return null ;
        if (routeStops.size() == currentIndex+1 )
            return routeStops.get(0) ; //goes back to first one
        if (routeStops.size() > currentIndex+1 )
            return routeStops.get(currentIndex+1) ;
        return null ;
    }

    public void populateTimetoStops(Integer speed)
    {
        Integer noofStops  = this.routeStops.size() ;
        for (int i=0; i<noofStops; i++ )
        {
            Stop stop1 = routeStops.get(i) ;
            if( stop1.getNextStopid() != -1)
            {
                Stop stop2 = getNextStop(i) ;
                if(stop2 != null ) {
                    Integer timeToTravel = getTravelTime(speed, stop1, stop2);
                    stop1.setTimeNextstop(timeToTravel);
                }

            }
        }
    }
    public void copy(Route route1)  {
        Stop stop1 = null;
        routeID =  route1.routeID ;
        number = route1.number ;
        name = route1.name ;
        Integer noofStops  = route1.routeStops.size() ;
        for (int i=0; i<noofStops; i++ ) {
            stop1  = new Stop() ;
            stop1.clone(route1.routeStops.get(i));
            routeStops.put(i, stop1) ;
        }
        InitialStopId = route1.InitialStopId ;
        currentRouteStopIndex = route1.currentRouteStopIndex;
    }
}

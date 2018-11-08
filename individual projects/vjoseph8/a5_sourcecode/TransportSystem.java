import org.omg.PortableInterceptor.INACTIVE;

import java.util.HashMap;

public class TransportSystem {
    private HashMap<Integer, Stop> stops;
    private HashMap<Integer, Bus> buses;
    private HashMap<Integer, Depot> depots;
    private HashMap<Integer, Route> routes;

    public TransportSystem() {
        stops = new HashMap<Integer, Stop>();
        buses = new HashMap<Integer, Bus>();
        depots = new HashMap<Integer, Depot>();
        routes = new HashMap<Integer, Route>();

    }

    public Integer extendRoute(Integer routeID, Integer stopID) {
        Route route = routes.get(routeID) ;
        Stop stop = new Stop() ;
        stop.clone(stops.get(stopID)) ;
        if(route != null ) {
            route.extendRoute(routeID, stop);
        }
        return routeID;
    }

    public Integer addRoute(Integer routeID, Integer Number, String Name) {
        Route route = new Route(routeID, Number, Name);

        //Please check Object  already created for routes TODO
        routes.put(routeID, route);
        return routeID;
    }

    public Integer addBus(Integer busID, Integer routeID, Integer busLocation,
                          Integer passengerCount, Integer Capacity, Integer initialFuel,
                          Integer fuelCapacity, Integer speed) {
        Route route = this.routes.get(routeID) ;
        Bus bus = new Bus(busID, route, busLocation, passengerCount, Capacity, initialFuel, fuelCapacity, speed);
        //Calculate distance to next stops based the bus speeds
        route  =  bus.getRoute() ;
        route.populateTimetoStops(speed) ;
        buses.put(busID, bus);
        return busID;
    }

    public Integer addStop(Integer stopID, String Name, Integer passengerCount, Double xlocation, Double ylocation) {
        Stop stop = new Stop(stopID, Name, passengerCount, xlocation, ylocation);
        stops.put(stopID, stop);
        return stopID ;
    }

/*    public Double getDistanceBetweenStops(Integer stopID1, Integer stopID2) {
        Stop stop1 = stops.get(stopID1) ;
        Stop stop2 = stops.get(stopID2) ;
        Double distance  = 70.0D * Math.sqrt(   Math.pow((stop1.getLocation().longitude - stop2.getLocation().longitude), 2.0D)
                                           +  Math.pow((stop1.getLocation().latitude - stop2.getLocation().latitude), 2.0D)   );
        return distance  ;
     }


    public Integer getTravelTime(Integer busId, Integer stopID1, Integer stopID2) {
        Double distance = getDistanceBetweenStops(stopID1, stopID2);
        Integer travelTime = 0;
        Bus bus = buses.get(busId);
        if (bus != null) {
            Integer speed = bus.getSpeed();
            if (speed != 0) {
                travelTime = 1 + distance.intValue() * 60 / speed;
            }
        }
        return travelTime;
    }
*/


    public Integer addDepot(Integer ID, String Name, Double xLocation, Double yLocation) {
        Depot depot = new Depot(ID, Name, xLocation, yLocation);
        //Please check Object  already created for routes TODO
        depots.put(ID, depot);
        return ID ;
    }

    public void resetAll() {
        stops.clear();
        routes.clear();
        depots.clear();
        buses.clear();
    }

    public Boolean runNextEvent() {
        return true ;
    }

    public Bus getBusInformation(Integer busID) {
        return buses.get(busID) ;
    }

    public Route getRouteInformation(Integer routeID) {
        return routes.get(routeID) ;
    }

    public Stop getStopInformation(Integer stopID) {
        return stops.get(stopID) ;
    }

    public Depot getDepotInformation(Integer depotID) {
        return  depots.get(depotID) ;
    }
}
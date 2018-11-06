import java.io.IOException;
import java.util.*;
import java.nio.file.*;

public class SimulationEngine{

    private HashMap<Integer,Bus> busMap = new HashMap<Integer,Bus>();
    private HashMap<Integer,Stop> stopMap = new HashMap<Integer,Stop>();
    private HashMap<Integer,Route> routeMap = new HashMap<Integer,Route>();
    private EventQueue eventQueue = new EventQueue();

    public void addBus(Bus bus){
        busMap.put(bus.getBusId(), bus);
    }

    public void addStop(Stop stop){
        stopMap.put(stop.getStopId(), stop);
    }

    public void addRoute(Route route){
        routeMap.put(route.getRouteId(), route);
    }

    public void addEvent(Event event){
        eventQueue.addEvent(event);
    }

    public Bus getBus(int busId){
        return busMap.get(busId);
    }

    public Stop getStop(int stopId){
        return stopMap.get(stopId);
    }

    public Route getRoute(int routeid){
        return routeMap.get(routeid);
    }

    public double calcDistance(Stop stop1, Stop stop2){
        return 70 * Math.sqrt(Math.pow(stop1.getLatitude() - stop2.getLatitude(), 2) + Math.pow(stop1.getLongitude() - stop2.getLongitude(), 2));
    }

    public int calcTravelTime(Stop stopFrom, Stop stopTo, int speed){
        double dist = calcDistance(stopFrom, stopTo);
        return 1 + ((int)dist) * 60 / speed;
    }

    // Run upto the maximum number of iterations passed as argument and returns list of event outputs at each iteration
    // We don't write directly to console or return a string so that we keep this method as generic/flexible as possible
    // Let the caller handle what to do with the results
    public List<String> runSimulation(int maxIterations){

        List<String> resultList = new ArrayList<String>();

        int iterationCount = 0;
        while (iterationCount < maxIterations){

            Event e = eventQueue.popEventWithLowestTime();
            if (e == null) break;

            if (e.getType() != EventType.MOVE_BUS) continue; // Ignoring out of scope event types
            
            Bus bus = getBus(e.getObjectid());
            Route route = bus.getRoute();

            int stopCurrentlyReachedIndex = bus.getNextStopIndex();
            Stop stopCurrentlyReached = route.getStopAtindex(stopCurrentlyReachedIndex);

            int stopHeadedToIndex = bus.getRoute().getNextStopIndex(stopCurrentlyReachedIndex);
            Stop stopHeadedTo = route.getStopAtindex(stopHeadedToIndex);

            int arrivalTimeAtNextStop = e.getTime() + calcTravelTime(stopCurrentlyReached, stopHeadedTo, bus.getSpeed());

            String message = String.format("b:%d->s:%d@%d//p:%d/f:%d", 
                                            bus.getBusId(),
                                            stopHeadedTo.getStopId(),
                                            arrivalTimeAtNextStop,
                                            0, // Assignment 5 expects 0 for passenger count
                                            0 // Assignment 5 expects 0 for fuel capacity
                                            );

            resultList.add(message);

            addEvent(new Event(arrivalTimeAtNextStop, EventType.MOVE_BUS, bus.getBusId()));

            bus.setCurrentStop(stopCurrentlyReached);
            bus.setNextStopIndex(stopHeadedToIndex);

            iterationCount++;
        }

        return resultList;
    }

    public void initFromFile(String filePath) throws IOException, Exception{

        // Clear all state since we might be reusing the same instance after it completed its run
        busMap = new HashMap<Integer,Bus>();
        stopMap = new HashMap<Integer,Stop>();
        routeMap = new HashMap<Integer,Route>();
        eventQueue = new EventQueue();

        Path path = Paths.get(filePath);
        List<String> fileContents = Files.readAllLines(path);

        for (String line : fileContents) {

            if (line.length() == 0) continue;

            String[] splitLines = line.trim().split("\\s*,\\s*"); // This regex gives trimmed entries after split by comma

            try{
                ScenarioType scenarioType;
                try{
                    scenarioType = ScenarioType.valueOf(splitLines[0].toUpperCase());
                }
                catch(IllegalArgumentException  iae){
                    // Ignore unknown scenario types in input file
                    continue;
                }
                switch (scenarioType){

                    case ADD_DEPOT:
                        // Out of scope for Assignment 5
                        break;

                    case ADD_STOP:
                        this.addStop(new Stop(
                                                Integer.parseInt(splitLines[1]), 
                                                splitLines[2], 
                                                Double.parseDouble(splitLines[4]),
                                                Double.parseDouble(splitLines[5]))
                                        );
                        break;

                    case ADD_ROUTE:
                        this.addRoute(new Route(
                                                Integer.parseInt(splitLines[1]), 
                                                Integer.parseInt(splitLines[2]), 
                                                splitLines[3])
                                        );
                        break;

                    case EXTEND_ROUTE:
                        Route route = this.getRoute(Integer.parseInt(splitLines[1]));
                        route.addStop(this.getStop(Integer.parseInt(splitLines[2])));    
                        break;

                    case ADD_BUS:
                        this.addBus(new Bus(
                                                Integer.parseInt(splitLines[1]), 
                                                this.getRoute(Integer.parseInt(splitLines[2])),
                                                Integer.parseInt(splitLines[3]),
                                                Integer.parseInt(splitLines[4]),
                                                Integer.parseInt(splitLines[5]),
                                                Integer.parseInt(splitLines[6]),
                                                Integer.parseInt(splitLines[7]),
                                                Integer.parseInt(splitLines[8]))
                                        );
                        break;

                    case ADD_EVENT:
                        EventType eventType;
                        
                        try{
                            eventType = EventType.valueOf(splitLines[2].toUpperCase());
                        }
                        catch(IllegalArgumentException  iae){
                            // Unsupported event type
                            continue;
                        }
                        
                        this.addEvent(new Event(Integer.parseInt(splitLines[1]), eventType, Integer.parseInt(splitLines[3])));
                        
                        break;
                }
            }
            catch(Exception e){
                // Bad data in input file
                throw new Exception(String.format("Bad input data -> '%s'", line), e);
            }
        }
    }
}
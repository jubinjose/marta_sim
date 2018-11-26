import java.io.IOException;
import java.util.*;

import org.slf4j.Logger;

import java.nio.file.*;
import org.slf4j.LoggerFactory;

public class SimulationEngine{

    private static final Logger logger = LoggerFactory.getLogger("");

    private HashMap<Integer,Bus> busMap = new HashMap<Integer,Bus>();
    private HashMap<Integer,Stop> stopMap = new HashMap<Integer,Stop>();
    private HashMap<Integer,BusRoute> routeMap = new HashMap<Integer,BusRoute>();
    private EventQueue eventQueue = new EventQueue();
    private Deque<SystemState> rewindStack = new ArrayDeque<>();

    List<String> initialSetupData; // Will store all lines from input setup file
    List<String> initialRiderData; // will store all lines from input reader file

    // using singleton here so the web request gets hold of this instance
    private SimulationEngine(){};

    private static SimulationEngine instance;
    public static SimulationEngine getInstance(){
        if (instance == null) instance = new SimulationEngine();
        return instance;        
    }
    
    /* System Efficiency Constants */
    private double sysSpeed = 1;
    public double getSysSpeed(){
        return sysSpeed;
    }
    public void setSysSpeed(double val){
        sysSpeed = val;
    }

    private double sysCapacity = 1;
    public double getSysCapacity(){
        return sysCapacity;
    }
    public void setCapacity(double val){
        sysCapacity = val;
    }

    private double sysWaiting = 1;
    public double getSysWaiting(){
        return sysWaiting;
    }
    public void setSysWaiting(double val){
        sysWaiting = val;
    }

    private double sysBuses = 1;
    public double getSysBuses(){
        return sysBuses;
    }
    public void setSysBuses(double val){
        sysBuses = val;
    }

    private double sysCombined = 1;
    public double getSysCombined(){
        return sysCombined;
    }
    public void setSysCombined(double val){
        sysCombined = val;
    }

    /* Methods */
    public void addBus(Bus bus){
        busMap.put(bus.getBusId(), bus);
    }

    public void addStop(Stop stop){
        stopMap.put(stop.getStopId(), stop);
    }

    public void addRoute(BusRoute route){
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

    public BusRoute getRoute(int routeid){
        return routeMap.get(routeid);
    }

    public Collection<BusRoute> getRoutes(){
        return Collections.unmodifiableCollection(routeMap.values());
    }

    public Collection<Stop> getStops(){
        return Collections.unmodifiableCollection(stopMap.values());
    }

    public Collection<Bus> getBuses(){
        return Collections.unmodifiableCollection(busMap.values());
    }

    public double calcDistance(Stop stop1, Stop stop2){
        return 70 * Math.sqrt(Math.pow(stop1.getLatitude() - stop2.getLatitude(), 2) + Math.pow(stop1.getLongitude() - stop2.getLongitude(), 2));
    }

    public int calcTravelTime(Stop stopFrom, Stop stopTo, int speed){
        double dist = calcDistance(stopFrom, stopTo);
        return 1 + ((int)dist) * 60 / speed;
    }

    public double calcSystemEfficiency(){

        double busCost = 0;

        for (Bus bus : busMap.values()) {
            busCost = busCost + sysSpeed * bus.getSpeed() + sysCapacity * bus.getCapacity();
        }

        int waiting = 0;

        for (Stop stop : stopMap.values()) {
            waiting += stop.getWaitingCount();
        }

        return sysWaiting * waiting + sysBuses * busCost + sysCombined * waiting * busCost;

    }

    public int getNumberOfRewindsPossible(){
        return rewindStack.size();
    }

    // Initialize the simulation from input files
    public void initFromFile(String setupFile, String riderFile) throws IOException, Exception{
        this.initialSetupData = Files.readAllLines(Paths.get(setupFile));
        this.initialRiderData = Files.readAllLines(Paths.get(riderFile));
        init();
    }

    // Initialize the simulation from list of strings
    // Re-factored this as a separate method to support reset of system to very beginning
    public void init() throws IOException, Exception{

        // Clear all state since we might be reusing the same instance after it completed its run
        busMap = new HashMap<Integer,Bus>();
        stopMap = new HashMap<Integer,Stop>();
        routeMap = new HashMap<Integer,BusRoute>();

        eventQueue = new EventQueue();
        rewindStack = new ArrayDeque<>();

        for (String line : this.initialSetupData) {

            if (line.trim().length() == 0) continue;

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
                    case ADD_STOP:
                        this.addStop(new Stop(
                                                Integer.parseInt(splitLines[1]), 
                                                splitLines[2], 
                                                Double.parseDouble(splitLines[4]),
                                                Double.parseDouble(splitLines[5]))
                                        );
                        break;

                    case ADD_ROUTE:
                        this.addRoute(new BusRoute(
                                                Integer.parseInt(splitLines[1]), 
                                                Integer.parseInt(splitLines[2]), 
                                                splitLines[3])
                                        );
                        break;

                    case EXTEND_ROUTE:
                    BusRoute route = this.getRoute(Integer.parseInt(splitLines[1]));
                        route.addStop(this.getStop(Integer.parseInt(splitLines[2])));    
                        break;

                    case ADD_BUS:
                        this.addBus(new Bus(
                                                Integer.parseInt(splitLines[1]), 
                                                this.getRoute(Integer.parseInt(splitLines[2])),
                                                Integer.parseInt(splitLines[3]),
                                                Integer.parseInt(splitLines[4]),
                                                Integer.parseInt(splitLines[5]))
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
                    default:

                }
            }
            catch(Exception e){
                // Bad data in scenario file
                throw new Exception(String.format("Bad data in scenario file:%n%s", line), e);
            }
        }

        for (String line : this.initialRiderData) {

            if (line.trim().length() == 0) continue;

            String[] splitLines = line.trim().split("\\s*,\\s*"); // This regex gives trimmed entries after split by comma

            try{
                Stop stop = getStop(Integer.parseInt(splitLines[0]));
                if (stop!=null) {
                	stop.setRidersArriveHigh(Integer.parseInt(splitLines[1]));
                    stop.setRidersArriveLow(Integer.parseInt(splitLines[2]));
                    stop.setRidersOffHigh(Integer.parseInt(splitLines[3]));
                    stop.setRidersOffLow(Integer.parseInt(splitLines[4]));
                    stop.setRidersOnHigh(Integer.parseInt(splitLines[5]));
                    stop.setRidersOnLow(Integer.parseInt(splitLines[6]));
                    stop.setRidersDepartHigh(Integer.parseInt(splitLines[7]));
                    stop.setRidersDepartLow(Integer.parseInt(splitLines[8]));
                }
            }
            catch(Exception e){
                 // Bad data in rider file
                 throw new Exception(String.format("Bad data in rider file:%n%s%n", line), e);
            }
        }
    }

    // Run upto the specified number of iterations and returns list of event outputs at each iteration
    // Don't write directly to console or return a string so that we keep the method as generic/flexible as possible
    // Let the caller handle what to do with the results
    public List<String> runSimulation(int maxIterations){

        List<String> resultList = new ArrayList<String>();

        int iterationCount = 0;
        while (iterationCount < maxIterations){

            Event event = eventQueue.popEventWithLowestTime();
            if (event == null) break;

            if (event.getType() != EventType.MOVE_BUS) continue; // Ignoring out of scope event types
            
            Bus bus = getBus(event.getObjectid());
            BusRoute route = bus.getRoute();

            int stopReachedIndex = bus.getNextStopIndex();
            Stop stopReached = route.getStopAtindex(stopReachedIndex);

            bus.setCurrentStop(stopReached);

            // add curent state to rewind queue for rewind capability
            if (rewindStack.size()==3)  { rewindStack.removeLast(); }
            SystemState systemState = null;
            
            try{
                systemState = new SystemState((Bus)bus.clone(), (Stop)stopReached.clone(), event);
            }
            catch(CloneNotSupportedException cex){

            }
            rewindStack.push(systemState);

            // Apply bus changes and calculate next stop

            int stopHeadedToIndex;
            Stop stopHeadedTo;
            boolean hadRouteChange = bus.getHasPendingRouteChange();

            if (bus.getHasPendingChanges()){
                bus.applyPendingChanges();
            }

            if (hadRouteChange){
                // Use route and stop from the bus changes that got applied
                stopHeadedToIndex = bus.getNextStopIndex();
                stopHeadedTo = bus.getRoute().getStopAtindex(bus.getNextStopIndex());
            }
            else{ // Find next stop on route
                stopHeadedToIndex = bus.getRoute().getNextStopIndex(stopReachedIndex);
                stopHeadedTo = route.getStopAtindex(stopHeadedToIndex);
                bus.setNextStopIndex(stopHeadedToIndex);
            }

            int arrivalTimeAtNextStop = event.getTime() + calcTravelTime(stopReached, stopHeadedTo, bus.getSpeed());
            bus.setArrivaltime(arrivalTimeAtNextStop);

            // Passenger math
            logger.info(String.format("Bus %d reached stop %d", bus.getBusId(), stopReached.getStopId()));

            logger.info("Currently waiting at stop: " + stopReached.getWaitingCount());
            int ridersArrive = stopReached.getRidersArrive();
            logger.info("ridersArrive: " + ridersArrive);
            stopReached.setWaitingCount(stopReached.getWaitingCount() + ridersArrive); // Set new waiting count for stop
            logger.info("New waiting at stop before boarding: " + stopReached.getWaitingCount());

            logger.info("Bus rider count before exchange: " + bus.getRiderCount());
            int ridersOff = stopReached.getRidersOff();
            logger.info("ridersOff: " + ridersOff);
            int transfers = Math.min(bus.getRiderCount(), ridersOff); // can't disembark more than those who are already on the bus
            logger.info("Actual getting off bus (transfers): " + transfers);
            bus.setRiderCount(bus.getRiderCount() - transfers); // People got off the bus

            int ridersOn = stopReached.getRidersOn();
            logger.info("ridersOn: " + ridersOn);
            int whoWantToGetOnBus = Math.min(stopReached.getWaitingCount(), ridersOn);

            logger.info("Bus capacity: " + bus.getCapacity());
            int actualWhoCanGetOnBus = Math.min(bus.getCapacity() - bus.getRiderCount(), whoWantToGetOnBus);
            logger.info("Those who can get on bus based on capacity: " + actualWhoCanGetOnBus);
            bus.setRiderCount(bus.getRiderCount() + actualWhoCanGetOnBus); // Board bus
            stopReached.setWaitingCount(stopReached.getWaitingCount() - actualWhoCanGetOnBus); // Adjust stop's waiting count after boarding bus
            logger.info("Bus post Rider Count: " + bus.getRiderCount());

            int ridersDepart = stopReached.getRidersDepart();
            if (ridersDepart <= transfers){
                transfers = transfers - ridersDepart;
                stopReached.setWaitingCount(stopReached.getWaitingCount() + transfers);
            }
            else{
                int peopleLeavingStop = Math.min(stopReached.getWaitingCount(), ridersDepart - transfers);
                stopReached.setWaitingCount(stopReached.getWaitingCount() - peopleLeavingStop);
            }
            logger.info("ridersDepart: " + ridersDepart);
            logger.info("Final waiting count at stop: " + stopReached.getWaitingCount());

            resultList.add(bus.toString());

            addEvent(new Event(arrivalTimeAtNextStop, EventType.MOVE_BUS, bus.getBusId()));

            iterationCount++;
        }

        return resultList;
    }

    public String moveBus(){
        
        logger.info("command:moveBus");
        List<String> resultList = runSimulation(1);

        if (!resultList.isEmpty()){
            return resultList.get(0);
        }
        else{
            return null;
        }
    }

    public ArrayList<SystemState> rewind(int numEvents){

        logger.info("command:rewind");
        ArrayList<SystemState> resultList = new ArrayList<SystemState>();
        
        while (numEvents>resultList.size()){

            SystemState systemState = rewindStack.poll();
            if (systemState == null) return resultList;

            resultList.add(systemState);

            // Extract cloned bus, stop from System State 
            Bus savedBus = systemState.getBus();
            Stop savedStop = systemState.getStop();
        
            // reset the actual Bus properties 
            Bus bus = this.getBus(savedBus.getBusId());
            bus.setArrivaltime(savedBus.getArrivaltime());
            bus.setCapacity(savedBus.getCapacity());
            bus.setCurrentStop(savedBus.getCurrentStop());
            bus.setNextStopIndex(savedBus.getNextStopIndex());
            bus.setRiderCount(savedBus.getRiderCount());
            bus.setRoute(savedBus.getRoute());
            bus.setSpeed(savedBus.getSpeed());

            // reset the actual Stop properties 
            Stop stop = this.getStop(savedStop.getStopId());
            stop.setWaitingCount(savedStop.getWaitingCount());

            // Remove the current event in eventQueue for this bus
            eventQueue.removeEventWithObjectId(bus.getBusId());

            // Reinstate the former event that caused the move that was rolled back
            eventQueue.addEvent(systemState.getEvent());
        }

        return resultList;
    }

    public Bus changeBus(int busId, int speed, int capacity){
        
        Bus bus = getBus(busId);
        bus.changeBus(speed, capacity);
        return bus;
    }

    public Bus changeBus(int busId, int speed, int capacity, int routeId, int stopIndex){
        
        Bus bus = getBus(busId);
        BusRoute route = getRoute(routeId);
        bus.changeBus(speed, capacity, route, stopIndex);
        return bus;
    }
}
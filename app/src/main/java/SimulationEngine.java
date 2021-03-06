import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// This is the heart of the application. 
// It holds all domain objects (Bus, Stop, Route, Event) etc. and is responsible for handling the main simulation logic
// It also does many calculations like system efficiency and travel time to keep Single Responsibility Principle for Domain Objects
// This design also plays well with inversion of control concept as in specialzied functions aren't vested in Domain objects

public class SimulationEngine{

    private static final Logger logger = LoggerFactory.getLogger("");

    private HashMap<Integer,Bus> busMap = new HashMap<Integer,Bus>();
    private HashMap<Integer,Stop> stopMap = new HashMap<Integer,Stop>();
    private HashMap<Integer,BusRoute> routeMap = new HashMap<Integer,BusRoute>();
    private EventQueue eventQueue = new EventQueue();
    private Deque<SystemState> rewindStack = new ArrayDeque<>();

    // Below two are stored so as to facilitate the 'reset' functionality
    List<String> initialSetupData; // Will store all lines from input setup file
    List<String> initialRiderData; // will store all lines from input reader file

    // using singleton pattern here so that SimulationService (when it process a web request) always gets hold of the same instance of SimulationEngine
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
    public void setSysCapacity(double val){
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

    /* Methods to add Domain object (Bus, Stop, Route), get Domain object by ID and return all domain objects*/
    public void addBus(Bus bus){
        busMap.put(bus.getBusId(), bus);
    }

    public Bus getBus(int busId){
        return busMap.get(busId);
    }

    public Collection<Bus> getBuses(){
        return Collections.unmodifiableCollection(busMap.values());
    }

    public void addStop(Stop stop){
        stopMap.put(stop.getStopId(), stop);
    }

    public Stop getStop(int stopId){
        return stopMap.get(stopId);
    }

    public Collection<Stop> getStops(){
        return Collections.unmodifiableCollection(stopMap.values());
    }

    public void addRoute(BusRoute route){
        routeMap.put(route.getRouteId(), route);
    }

    public BusRoute getRoute(int routeid){
        return routeMap.get(routeid);
    }

    public Collection<BusRoute> getRoutes(){
        return Collections.unmodifiableCollection(routeMap.values());
    }

    public void addEvent(Event event){
        eventQueue.addEvent(event);
    }

    // Distance and Time calculations
    public double calcDistance(Stop stop1, Stop stop2){
        return 70 * Math.sqrt(Math.pow(stop1.getLatitude() - stop2.getLatitude(), 2) + Math.pow(stop1.getLongitude() - stop2.getLongitude(), 2));
    }

    public int calcTravelTime(Stop stopFrom, Stop stopTo, int speed){
        double dist = calcDistance(stopFrom, stopTo);
        return 1 + ((int)dist) * 60 / speed;
    }

    // System efficiency calculation
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

    // We can only rewind as many times as there are SystemSatte entries in the rewind stack
    public int getNumberOfRewindsPossible(){
        return rewindStack.size();
    }

    // Initialize the simulation from input files
    public void initFromFile(String setupFile, String riderFile) throws IOException, Exception{
        initFromData(Files.readAllLines(Paths.get(setupFile)),Files.readAllLines(Paths.get(riderFile)));
    }

    // Initialize the simulation from setup and rider data passed as List of strings 
    // Useful for batch testing against many input files
    public void initFromData(List<String> setupData, List<String> riderData) throws IOException, Exception{
        this.initialSetupData = setupData;
        this.initialRiderData = riderData;
        init();
    }

    // Initialize the simulation from list of strings
    // Re-factored this as a separate method to support reset of system to very beginning
    public void init() throws IOException, Exception{

        System.out.println("\n\n");
        logger.info("COMMAND:init");
        
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

            // add curent state to rewind queue for rewind capability
            if (rewindStack.size()==3)  { rewindStack.removeLast(); }
            SystemState systemState = null;
            
            try{
                systemState = new SystemState((Bus)bus.clone(), (Stop)stopReached.clone(), event);
            }
            catch(CloneNotSupportedException cex){

            }
            rewindStack.push(systemState);

            // Update current Stop of Bus
            bus.setCurrentStop(stopReached);

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

            if (bus.getRiderCount()>bus.getCapacity()){
                logger.info(String.format("Bus capacity %d is less than riders in the bus %d", 
                bus.getCapacity(), bus.getRiderCount() ));
                
                int forcedOutOfBus = bus.getRiderCount() - bus.getCapacity();
                stopReached.setWaitingCount(stopReached.getWaitingCount() + forcedOutOfBus);
                bus.setRiderCount(bus.getCapacity());

                logger.info(String.format("Re-adjusted bus's rider count to %d and stop's waiting count to %d due to above", 
                bus.getRiderCount(), stopReached.getWaitingCount() ));
            }

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
        
        System.out.println("\n\n");
        logger.info("COMMAND:moveBus");
        List<String> resultList = runSimulation(1);

        if (!resultList.isEmpty()){
            return resultList.get(0);
        }
        else{
            return null;
        }
    }

    public ArrayList<SystemState> rewind(int numEvents){

        System.out.println("\n\n");
        logger.info("COMMAND:rewind");
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

    public void changeBus(int busId, int speed, int capacity){
        System.out.println("\n\n");
        logger.info("COMMAND:changeBus with no route change");
        Bus bus = getBus(busId);
        bus.changeBus(speed, capacity);
    }

    public void changeBus(int busId, int speed, int capacity, int routeId, int stopIndex){
        System.out.println("\n\n");
        logger.info("COMMAND:changeBus with route change");
        Bus bus = getBus(busId);
        BusRoute route = getRoute(routeId);
        bus.changeBus(speed, capacity, route, stopIndex);
    }
}
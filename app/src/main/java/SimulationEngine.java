import java.io.IOException;
import java.util.*;
import java.nio.file.*;
import java.util.stream.*;

public class SimulationEngine{

    private HashMap<Integer,Bus> busMap = new HashMap<Integer,Bus>();
    private HashMap<Integer,Stop> stopMap = new HashMap<Integer,Stop>();
    private HashMap<Integer,BusRoute> routeMap = new HashMap<Integer,BusRoute>();
    private EventQueue eventQueue = new EventQueue();
    private Deque<Integer> eventReplay = new ArrayDeque<>();
    private HashMap<Integer,SystemState> systemStatesMap = new HashMap<Integer,SystemState>();

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

    public void addEventReplay(int eventStateId){
        if(eventReplay.size() == 3){
            System.out.println("We only go back 3 events, removing event before adding more.");
            Integer e = eventReplay.removeLast();
            System.out.println("Removed: " + e);
        }

        eventReplay.push(eventStateId);
        System.out.println("Added event: " + eventStateId );
        System.out.println(eventReplay);
    }

    public void addSystemState(int systemStateId, SystemState systemState){
        systemStatesMap.put(systemStateId, systemState);
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

    public int getEventReplay(){
        return eventReplay.pollFirst();
    }

    public SystemState getSystemState(int systemStateId){
        return systemStatesMap.get(systemStateId);
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

    // Run upto the specified number of iterations and returns list of event outputs at each iteration
    // Don't write directly to console or return a string so that we keep the method as generic/flexible as possible
    // Let the caller handle what to do with the results
    public List<String> runSimulation(int maxIterations){

        List<String> resultList = new ArrayList<String>();

        int iterationCount = 0;
        while (iterationCount < maxIterations){

            Event e = eventQueue.popEventWithLowestTime();
            if (e == null) break;

            if (e.getType() != EventType.MOVE_BUS) continue; // Ignoring out of scope event types
            
            Bus bus = getBus(e.getObjectid());
            BusRoute route = bus.getRoute();

            int stopCurrentlyReachedIndex = bus.getNextStopIndex();
            Stop stopCurrentlyReached = route.getStopAtindex(stopCurrentlyReachedIndex);

            int stopHeadedToIndex = bus.getRoute().getNextStopIndex(stopCurrentlyReachedIndex);
            Stop stopHeadedTo = route.getStopAtindex(stopHeadedToIndex);

            int arrivalTimeAtNextStop = e.getTime() + calcTravelTime(stopCurrentlyReached, stopHeadedTo, bus.getSpeed());

            int busPrevRiderCount = bus.getRiderCount();

            // Passenger math
            int waitingAtStop = stopCurrentlyReached.getWaitingCount() + stopCurrentlyReached.getRidersArrive();
            int gettingOffBus = Math.min(bus.getRiderCount(), stopCurrentlyReached.getRidersOff());
            int boardingBus = Math.min(waitingAtStop, stopCurrentlyReached.getRidersOn());
            bus.setRiderCount(bus.getRiderCount() - gettingOffBus + boardingBus);
            int leavingStop = Math.min(waitingAtStop + gettingOffBus, stopCurrentlyReached.getRidersDepart());
            stopCurrentlyReached.setWaitingCount(waitingAtStop + gettingOffBus - leavingStop);

            // add to replayEvent and eventStatesMap for replay capabilities  
            Integer systemStateId = 0;
            if(eventReplay != null && eventReplay.size() > 0) {
                systemStateId = this.eventReplay.peekFirst() + 1;
            }
            else {
                systemStateId = 1;
            }
            this.addEventReplay(systemStateId);
            SystemState systemState = new SystemState(bus.getBusId(), busPrevRiderCount, bus.getCurrentStop(), waitingAtStop, bus.getCapacity(), bus.getRoute(), bus.getSpeed(), bus.getNextStopIndex(), bus.getArrivaltime());
            this.addSystemState(systemStateId, systemState);

            resultList.add(bus.toString());

            addEvent(new Event(arrivalTimeAtNextStop, EventType.MOVE_BUS, bus.getBusId()));

            bus.setCurrentStop(stopCurrentlyReached);
            bus.setNextStopIndex(stopHeadedToIndex);
            bus.setArrivaltime(arrivalTimeAtNextStop);

            iterationCount++;
        }

        return resultList;
    }

    // Initialize the simulation from input files
    public void initFromFile(String setupFile, String riderFile) throws IOException, Exception{
        this.initialSetupData = Files.readAllLines(Paths.get(setupFile));
        this.initialRiderData = Files.readAllLines(Paths.get(riderFile));
        init();
    }

    // Initialize the simulation from list of strings
    // Refactored this as a seperate method to support reset of system to very beginning
    public void init() throws IOException, Exception{

        // Clear all state since we might be reusing the same instance after it completed its run
        busMap = new HashMap<Integer,Bus>();
        stopMap = new HashMap<Integer,Stop>();
        routeMap = new HashMap<Integer,BusRoute>();

        eventQueue = new EventQueue();

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
                stop.setRidersArriveHigh(Integer.parseInt(splitLines[1]));
                stop.setRidersArriveLow(Integer.parseInt(splitLines[2]));
                stop.setRidersOffHigh(Integer.parseInt(splitLines[3]));
                stop.setRidersOffLow(Integer.parseInt(splitLines[4]));
                stop.setRidersOnHigh(Integer.parseInt(splitLines[5]));
                stop.setRidersOnLow(Integer.parseInt(splitLines[6]));
                stop.setRidersDepartHigh(Integer.parseInt(splitLines[7]));
                stop.setRidersDepartLow(Integer.parseInt(splitLines[8]));
            }
            catch(Exception e){
                 // Bad data in rider file
                 throw new Exception(String.format("Bad data in rider file:%n%s", line), e);
            }
        }
    }

    // JSON Returning methods below

    // We can always return entire system state for every operation and have the UI redraw everything
    // But it is much better to just send what is really updated for actions like Move Bus etc where onyl a bus and stop need to be redrawn in UI


    private String createBusJson(Bus bus){
        return String.format(
            "{\"id\":%d, \"current_stop_id\":%d, \"arrival_time\":%d, \"status\": \"%s\", \"rider_count\":%d,\"route\":%d, \"capacity\":%d, \"speed\":%d}",
            bus.getBusId(), bus.getCurrentStop().getStopId(), bus.getArrivaltime(), bus.toString(), bus.getRiderCount(), 
            bus.getRoute().getRouteId(),bus.getCapacity(),bus.getSpeed());
    }

    private String createStopJson(Stop stop){
        return String.format("{\"id\":%d,\"name\":\"%s\",\"waiting\":%d}", stop.getStopId(), stop.getName(), stop.getWaitingCount());
    }

    private String createRouteJson(BusRoute route){
        return String.format("{\"id\":%d,\"stops\":[%s]}", route.getRouteId(), 
                route.getStopList().stream()
                .map(s -> s.getStopId() + "")
                .collect(Collectors.joining(",")));
    }

    public String getState(){
        String json = String.format("{\"kspeed\":%s,\"kcapacity\":%s,\"kwaiting\":%s,\"kbuses\":%s,\"kcombined\":%s,\"efficiency\":%s,\"routes\":[",
                            sysSpeed + "", sysCapacity + "", sysWaiting + "", sysBuses + "", sysCombined + "", calcSystemEfficiency());
                            
        
        String csv = "";

        for (BusRoute route : routeMap.values()) {
            //StringJoiner
            csv = csv + String.format("%s,", createRouteJson(route));
        }
        csv = csv.replaceAll(",$", ""); // Remove trailing comma

        json = json + csv + "],\"stops\":[";

        csv = "";
        for (Stop stop : stopMap.values()) {
            csv = csv + String.format("%s,", createStopJson(stop));
        }
        csv = csv.replaceAll(",$", ""); // Remove trailing comma

        json = json + csv + "],\"buses\":[";

        csv = "";
        for (Bus bus : busMap.values()) {
            csv = csv + String.format("%s,", createBusJson(bus));
        }
        csv = csv.replaceAll(",$", ""); // Remove trailing comma

        return json + csv + "]}";
        
    }

    public String moveBus(){
        List<String> resultList = runSimulation(1);
        if (!resultList.isEmpty()){
            String result = resultList.get(0);

            // Extract bus  id and stop id from a string like "b:67->s:16@0//p:0"
            int busId = Integer.parseInt(result.split(":")[1].split("-")[0]);
            int stopId = Integer.parseInt(result.split(">")[1].split(":")[1].split("@")[0]);

            return String.format("{\"move\":true,\"bus\":%s,\"stop\":%s,\"efficiency\":%s, \"replay\":true}", 
                    createBusJson(getBus(busId)), createStopJson(getStop(stopId)), calcSystemEfficiency());
        }
        else{
            return "{\"move\":false}";
        }
    }

    public String replay(){
        int systemStateId = this.getEventReplay();
        SystemState systemState = null;
        if(systemStateId != 0) {
            systemState = this.getSystemState(systemStateId);
        }
        if (systemState != null){
            // Extract bus  id and stop id from System State 
            int busId = systemState.getBusId();
            int stopId = systemState.getBusNextStopIndex();
           
            // resetting the Bus properties 
            Bus bus = this.getBus(busId);
            bus.setRiderCount(systemState.getRiderCount());
            bus.setCurrentStop(systemState.getBusAtStop());
            bus.setCapacity(systemState.getBusCapacity());
            bus.setRoute(systemState.getBusRoute());
            bus.setSpeed(systemState.getBusSpeed());
            bus.setNextStopIndex(systemState.getBusNextStopIndex());
            bus.setArrivaltime(systemState.getBusArrivaltime());

            Stop stop = this.getStop(stopId);
            stop.setWaitingCount(systemState.getRiderWaitingCount());
        

            if(this.eventReplay.size() > 0){
                return String.format("{\"move\":true,\"bus\":%s,\"stop\":%s,\"efficiency\":%s, \"replay\":true}", 
                    createBusJson(getBus(busId)), createStopJson(getStop(stopId)), calcSystemEfficiency());
            } else {
                return String.format("{\"move\":true,\"bus\":%s,\"stop\":%s,\"efficiency\":%s, \"replay\":false}", 
                    createBusJson(getBus(busId)), createStopJson(getStop(stopId)), calcSystemEfficiency());
            }
        }
        else{
            return "{\"move\":false, \"replay\":false}";
        }
    }
}
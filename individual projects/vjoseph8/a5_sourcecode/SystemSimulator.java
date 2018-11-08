import java.util.Date ;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map;
;


 class Busmovement{
     public Integer sequenceID;
     public Integer startTime ;
     public Integer busID;
     public Integer StopID;
     public Integer nextStopID ;
     public Integer timeTonextstop;
     public Integer triggerTime;

     @Override
     public String toString() {
         //return ("startTime:"+startTime+" StopID:" +StopID+ " busID:"+ busID+ "->s:"+nextStopID+"@" +timeTonextstop  ) ;
         return ("b:"+ busID+ "->s:"+nextStopID+"@" +timeTonextstop+"//p:0/f:0") ;
     }
 };

public class SystemSimulator {
    Integer simulationStarttime ;
    public TransportSystem transportSystem ;
    private HashMap<Integer, Event> events;
    private HashMap<Integer, Busmovement> simevents;
    private SortedMap<Integer,Busmovement> timebasedEvents ;

    public SystemSimulator()
    {
        events = new HashMap<Integer, Event>();
        simevents = new HashMap<Integer, Busmovement>();
        simulationStarttime = 0 ;
        timebasedEvents  = new TreeMap<Integer,Busmovement>() ;
    }

    public Integer getSimulationStarttime() {
        return simulationStarttime;
    }

    public void setSimulationStarttime(Integer simulationStarttime) {
        this.simulationStarttime = simulationStarttime;
    }

    public TransportSystem getTransportSystem() {
        return transportSystem;
    }

    public void setTransportSystem(TransportSystem transportSystem) {
        this.transportSystem = transportSystem;
    }

    public void addEvent(Integer logicalTime, String bType, Integer id) {
        Event event = new Event(logicalTime, bType, id) ;
        events.put(events.size(), event) ;
    }
    public void startSimulation() {
        //Load the data file
        //Set the start time Start the Simulation
        Integer bCount = 20 ;
        generateAllEvents(bCount) ;

    }
    public void generateAllEvents(Integer simCount) {
        Integer currentTime = 0 ;
        int nCount  = 0 ;
        simevents.clear();
        setDelayStartTimetoBus() ;

        generateQueue() ;

        for (Map.Entry<Integer, Busmovement> entry : simevents.entrySet()) {
            Integer time = (Integer)entry.getKey()  ;
            Busmovement busmovement = (Busmovement)entry.getValue() ;
            System.out.println(busmovement.toString() );
            //this.simevents.put(this.simevents.size(),busmovement ) ;
            currentTime = busmovement.timeTonextstop ;
        }
    }
    public void moveBus() {
        //Move  current stop in the route.
        for (Map.Entry<Integer, Busmovement> entry : simevents.entrySet()) {
            Integer time = (Integer)entry.getKey()  ;
            Busmovement busmovement = (Busmovement)entry.getValue() ;
            moveOneBus(busmovement) ;
        }
    }
    public void moveOneBus(Busmovement busmovement) {
       Bus bus = transportSystem.getBusInformation(busmovement.busID) ;
       Route route = bus.getRoute() ;
       if( route.getRouteStops().size() == route.getCurrentRouteStopIndex() ){
           route.setCurrentRouteStopIndex(0) ;
       }
       else {
           route.setCurrentRouteStopIndex(route.getCurrentRouteStopIndex() + 1);
       }
    }


    public void setDelayStartTimetoBus() {
        Integer noofEvents = events.size();
        int busid  = -1 ;
        Bus bus = null ;
        for (int i = 0; i < noofEvents; i++) {
            Event event = events.get(i);
            busid = event.getBusID();
            bus = transportSystem.getBusInformation(busid);
            if (bus != null)
            {
                bus.setCurrentTimeTrack(event.eventTriggerTime);
            }
        }
    }

    public Integer findNextBustoMove() {

        //lowest Start time
        int time = -1 ;
        int busid  =-1 ;
        int nextBusid  = -1 ;
        Bus bus ;
        Integer noofEvents = events.size();
        for (int i = 0; i < noofEvents; i++) {
            Event event = events.get(i);
            busid = event.getBusID();
            //Simulation start time
            bus = transportSystem.getBusInformation(busid);
            int testvalue = bus.getCurrentTimeTrack() ;
            if ( time == -1 || time >= bus.getCurrentTimeTrack())
            {
                time = bus.getCurrentTimeTrack();
                nextBusid = bus.getBusID() ;
            }
        }
        return nextBusid ;
    }


    public void generateQueue( ){
        int MAXSIM = 20 ;
        Integer startTime = 0 ;
        for ( int i=0; i<MAXSIM; i++ ) {
            int busId = findNextBustoMove() ;
            Bus bus = transportSystem.getBusInformation(busId);
            if (bus != null) {
                startTime = bus.getCurrentTimeTrack() ;
                Route route = bus.getRoute();
                if (route != null) {
                    Integer routeloc = route.getCurrentRouteStopIndex();
                    Stop stop1 = route.getRouteStops().get(routeloc);
                    Integer timetoNextStop = stop1.getTimeNextstop() ;
                    //this is stop gap measure to adding any routes later
                    if(timetoNextStop == -1){
                        timetoNextStop = route.getTravelTime(bus.getSpeed(), stop1, route.getNextStop(routeloc)) ;
                        stop1.setTimeNextstop(timetoNextStop);
                    }
                    route.setNextStopIndex() ; //Rolling to the next one
                    Busmovement busmovement =  new Busmovement() ;
                    busmovement.busID = busId ;
                    busmovement.sequenceID = simevents.size() ;
                    busmovement.startTime = startTime ;
                    busmovement.nextStopID =   stop1.getNextStopid()  ;
                    busmovement.StopID = stop1.getStopID() ;
                    busmovement.timeTonextstop = timetoNextStop +startTime ;
                    bus.setCurrentTimeTrack(timetoNextStop+startTime);
                    simevents.put(simevents.size(), busmovement);
                    int ii=0 ;
                }
            }
        }
        return  ;
    }

}

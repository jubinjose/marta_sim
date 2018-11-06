package edu.MTSSimulation;

import java.util.*;

public class EventHandler {

    // Event Queue Management
    private Integer timeCounter;
    private Comparator<Event> eventComparator = new EventComparator();
    private PriorityQueue<Event> eventPriorityQueue = new PriorityQueue<Event>(eventComparator);

    // Objects Graph
    private Map<Integer, TransitStop> eventTransitStopObjects = new HashMap<Integer, TransitStop>();
    private Map<Integer, Route> eventRouteObjects = new HashMap<Integer, Route>();
    private Map<Integer, Bus> eventBusObjects = new HashMap<Integer, Bus>();


    public EventHandler() {
        this.timeCounter = 0;
    }

    public EventHandler(int timeCounterStartValue) {
        this.timeCounter = timeCounterStartValue;
    }

    public void setTimeCouter(Integer timeCounterValue) {
        this.timeCounter = timeCounterValue;
    }

    public Integer getTimeCouter() {
        return this.timeCounter;
    }

    public void addEvent(Event event) {
       this.eventPriorityQueue.add(event);
    }

    public Event getEvent() {
        return this.eventPriorityQueue.poll();
    }

    public PriorityQueue<Event> getEventPriorityQueue() {
        return this.eventPriorityQueue;
    }

    public void setEventTransitObjects(Map<Integer, TransitStop>  transitStopObjectsValue) {
       this.eventTransitStopObjects = transitStopObjectsValue;
    }

    public Map<Integer, TransitStop>  getEventTransitObjects() {
        return this.eventTransitStopObjects;
    }

    public void addTransitStopObject(Integer transitStopIDValue, TransitStop  transitStopValue) {
        this.eventTransitStopObjects.put(transitStopIDValue,transitStopValue);
    }

    public void setEventRouteObjects(Map<Integer, Route>  routeObjectsValue) {
        this.eventRouteObjects = routeObjectsValue;
    }

    public Map<Integer, Route>  getEventRouteObjects() {
        return this.eventRouteObjects;
    }

    public void addRouteObject(Integer routeIDValue, Route routeObjectValue) {
        //this.eventRouteObjects = routeObjectsValue;
        this.eventRouteObjects.put(routeIDValue, routeObjectValue);
    }

    public void setEventBusObjects(Map<Integer, Bus>  busObjectsValue) {
        this.eventBusObjects = busObjectsValue;
    }

    public Map<Integer, Bus>  getEventBusObjects() {
        return this.eventBusObjects;
    }

    public void addBusObject(Integer busIDValue, Bus  busObjectValue) {
        this.eventBusObjects.put(busIDValue, busObjectValue);
        //this.eventBusObjects = busObjectsValue;
    }

    public void printTransitObjects(Map<Integer, TransitStop>  transitStopObjectsPrint) {
        for (Map.Entry<Integer, TransitStop> item : transitStopObjectsPrint.entrySet()) {
            Integer key = item.getKey();
            TransitStop transitStopObject = item.getValue();
            transitStopObject.printTransitStop();
        }
    }

    public void printRouteObjects(Map<Integer, Route>  routeObjectsPrint) {
        for (Map.Entry<Integer, Route> item : routeObjectsPrint.entrySet()) {
            Integer key = item.getKey();
            Route routeObject = item.getValue();
            routeObject.printRoute();
        }
    }

    public void printBusObjects(Map<Integer, Bus>  busObjectsPrint) {
        for (Map.Entry<Integer, Bus> item : busObjectsPrint.entrySet()) {
            Integer key = item.getKey();
            Bus busObject = item.getValue();
            busObject.printBus();
        }
    }

    public void printEvents() {

        while (this.getEventPriorityQueue().size() != 0)
        {
            Event event = this.getEvent();
            event.printEvent();
        }
    }

    public void processEvents() {

        // loop for twenty (20) iterations:
        for(int i = 1; i <= 20; i++){
            // make sure PQ has event to process
            if(this.getEventPriorityQueue().size() != 0) {
                // Step 2: Determine which bus should be selected for processing (based on lowest arrival time)
                // PQ Data Structure will handle this
                Event event = this.getEvent();

                // Step 3: Determine which stop the bus will travel to next (based on the current location and route)
                // Step 4: Calculate the distance and travel time between the current and next stops
                // Step 5: Display the output line of text to the display
                // Step 6: Update system state and generate new events as needed
                // NOTE: if needed, we can check event.eventType to below to process different type of events.

                this.processBus(event);
            }
        }
        // end loop
    }

    private void processBus(Event event){

        try {

            // Step 3: Determine which stop the bus will travel to next (based on the current location and route)

            // get bus object
            Bus bus = this.eventBusObjects.get(event.getEventObjectID());

            // get route for the bus
            Route busRoute = bus.getBusRoute();

            // find where the bus is currently in the route
            Integer busCurrentRouteTransitStopIndex = bus.getBusCurrentRouteTransitStopIndex();

            // find where the next stop for the bus is in the route
            Integer busRouteLastTransitStopIndex = Collections.max(busRoute.getRouteStops().keySet());
            Integer busNextRouteTransitStopIndex = busCurrentRouteTransitStopIndex + 1;
            // if bus has reached end of the route, move the bus to beginning of the route
            // a circular linked list would have been a better data structure for this but we can do that later
            if(busNextRouteTransitStopIndex > busRouteLastTransitStopIndex) {
                busNextRouteTransitStopIndex = 0;
            }
            // end Step 3

            // Step 4: Calculate the distance and travel time between the current and next stops
            Location busCurrentLocation = busRoute.getRouteTransitStop(busCurrentRouteTransitStopIndex).getTransitStopLocation();
            Location busNextLocation = busRoute.getRouteTransitStop(busNextRouteTransitStopIndex).getTransitStopLocation();

            Double distance = 70.0 * Math.sqrt(Math.pow((busCurrentLocation.getLocationLatitude() - busNextLocation.getLocationLatitude()), 2)
                    + Math.pow((busCurrentLocation.getLocationLongititude() - busNextLocation.getLocationLongititude()), 2));

            Integer travelTime = 1 + (distance.intValue() * 60 / bus.getBusSpeed().intValue());
            // business rule - travel time need to be at least one minute
            if (travelTime == 0) {
                travelTime = 1;
            }

            Integer newEventRank = event.getEventRank() + travelTime;
            // end Step 4

            // Step 5: Display the output line of text to the display
            Integer busNextRouteTransitStopID = busRoute.getRouteTransitStop(busNextRouteTransitStopIndex).getTransitStopID();
            //  example output b:67->s:5@12//p:0/f:0
            System.out.println("b:" + bus.getBusID() + "->s:" + busNextRouteTransitStopID + "@" + newEventRank + "//p:0/f:0");

            // Step 6: Update system state and generate new events as needed
            bus.setBusCurrentRouteTransitStopIndex(busNextRouteTransitStopIndex);
            String eventType = "move_bus";
            Event newBusEvent = new Event(newEventRank, eventType, bus.getBusID());
            this.addEvent(newBusEvent);
            // end Step 6

        } catch (Exception ex) {
            System.out.println("Error processing bus " + event.getEventObjectID());
        }
    }

}
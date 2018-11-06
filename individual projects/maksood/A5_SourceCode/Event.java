package edu.MTSSimulation;

public class Event {
    private Integer eventRank;
    private String eventType;
    private Integer eventObjectID;

    // default constructor
    public Event() {
    }

    // constructor overload
    public Event(Integer eventRankStartValue, String eventTypeStartValue, Integer eventObjectIDStartValue) {
        this.eventRank = eventRankStartValue;
        this.eventType = eventTypeStartValue;
        this.eventObjectID = eventObjectIDStartValue;
    }

    public Integer getEventRank() {
        return this.eventRank;
    }

    public void setEventRank(Integer eventRankValue) {
        this.eventRank = eventRankValue;
    }

    public String getEventType() {
        return this.eventType;
    }

    public void setEventType(String eventTypeValue) {
        this.eventType = eventTypeValue;
    }

    public Integer getEventObjectID() {
        return this.eventObjectID;
    }

    public void setEventObjectID(Integer eventObjectIDValue) {
        this.eventObjectID = eventObjectIDValue;
    }

    public void printEvent() {
        System.out.println("------- Event Details -------");
        System.out.println("Event Rank: " + this.getEventRank());
        System.out.println("Event Type: " + this.getEventType());
        System.out.println("Event Object ID: " + this.getEventObjectID());
        System.out.println("------------ END -------------------");
    }
}


public class Event {

    Integer eventTriggerTime ;
    String eventType ;
    Integer busID ;

    public Integer getBusID() {
        return busID;
    }

    public void setBusID(Integer busID) {
        this.busID = busID;
    }



    public  Event(Integer logicalTime, String bType, Integer id){
        this.eventTriggerTime = logicalTime ;
        this.eventType = bType ;
        this.busID = id ;

    }

    public Integer getEventTriggerTime() {
        return eventTriggerTime;
    }

    public void setEventTriggerTime(Integer eventTriggerTime) {
        this.eventTriggerTime = eventTriggerTime;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }


}

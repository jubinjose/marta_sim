public class Event{

    private int time;
    public int getTime(){
        return time;
    }
    public void setTime(int val){
        time = val;
    }

    private EventType type;
    public EventType getType(){
        return type;
    }
    public void setType(EventType val){
        type = val;
    }

    private int objectId;
    public int getObjectid(){
        return objectId;
    }
    public void setOjectId(int val){
        objectId = val;
    }

    public Event(int time, EventType eventtype, int objectId){
        this.time = time;
        this.type = eventtype;
        this.objectId = objectId;
    }

}
// Used to store info that will be needed to rewind system after a move bus
public class SystemState{

    private Bus bus;
    public Bus getBus(){
        return bus;
    }

    private Stop stop;
    public Stop getStop(){
        return stop;
    }

    private Event event;
    public Event getEvent(){
        return event;
    }
    
    public SystemState(Bus bus, Stop stop, Event event) {

        this.bus = bus;
        this.stop = stop;
        this.event = event;
    }

    public String toString(){
        return String.format("Bus: %s%nStop: %s%nEvent: %s", bus, stop, event);
    }
}


import java.util.Comparator;
import java.util.*;

public class EventQueue {

    private Comparator<Event> eventComparator = Comparator.comparingInt(Event::getTime);
    private PriorityQueue<Event> events = new PriorityQueue<Event>(eventComparator);

    

    public void addEvent(Event event){
        events.add(event);
    }

    public Event popEventWithLowestTime(){
        return events.poll();
    }

    public void removeEventWithObjectId(int id){
        //Ideally we expect only one match because a ObjectId(BusId) won't be repeated in the eventQueue
        events.removeIf(event -> event.getObjectid() == id); 

    }

    @Override
    public String toString(){

        Iterator<Event> it = events.iterator();

        StringBuffer sb = new StringBuffer();

        while (it.hasNext()) {
            Event event = it.next();
            sb.append(String.format("%s , %s , %d%n", event.getTime() , event.getType() , event.getObjectid()));
         }

        return sb.toString();
    }

}
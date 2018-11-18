import java.util.Comparator;
import java.util.*;
import java.util.stream.Collectors;

public class EventQueue {

    private ArrayList<Event> eventList = new ArrayList<Event>() {};

    public void addEvent(Event event){
        eventList.add(event);
    }

    public Event popEventWithLowestTime(){
        
        // Break a tie (same logical time) with lowest bus number (for consistency across multiple runs)
        Optional<Event> lowestTimeOptionalEvent = eventList.stream().min(Comparator.comparing(Event::getTime)
                                                                                .thenComparing(Event::getObjectid)); 
        
        if (lowestTimeOptionalEvent.isPresent()){
            Event event = lowestTimeOptionalEvent.get();
            eventList.remove(event);
            return event;
        }
        else{
            return null;
        }
    }

    public void removeEventWithObjectId(int id){
        
        List<Event> matchingEventList = eventList.stream().filter(event -> event.getObjectid() == id).collect(Collectors.toList()); 

        eventList.removeAll(matchingEventList); //Ideally we expect only one match because a ObjectId(BusId) won't be repeated in the eventQueue

    }

    @Override
    public String toString(){

        List<Event> sortedList = eventList.stream()
            .sorted(Comparator.comparing(Event::getTime))
            .collect(Collectors.toList()); 

        StringBuffer sb = new StringBuffer();
        
        for (Event event : sortedList) {
            sb.append(String.format("%s , %s , %d%n", event.getTime() , event.getType() , event.getObjectid()));
        }

        return sb.toString();
    }

}
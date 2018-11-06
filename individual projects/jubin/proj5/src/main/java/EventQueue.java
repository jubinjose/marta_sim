import java.util.Comparator;
import java.util.*;
import java.util.stream.Collectors;

public class EventQueue {

    private HashSet<Event> eventSet = new HashSet<Event>() {};

    public void addEvent(Event event){
        eventSet.add(event);
    }

    public Event popEventWithLowestTime(){
        
        // Break a tie (same logical time) with lowest bus number (for consistency across multiple runs)
        Optional<Event> lowestTimeOptionalEvent = eventSet.stream().min(Comparator.comparing(Event::getTime)
                                                                                .thenComparing(Event::getObjectid)); 
        
        if (lowestTimeOptionalEvent.isPresent()){
            Event event = lowestTimeOptionalEvent.get();
            eventSet.remove(event);
            return event;
        }
        else{
            return null;
        }
    }

    @Override
    public String toString(){

        List<Event> sortedList = eventSet.stream()
            .sorted(Comparator.comparing(Event::getTime))
            .collect(Collectors.toList()); 

        StringBuffer sb = new StringBuffer();
        
        for (Event event : sortedList) {
            sb.append(String.format("%s , %s , %d%n", event.getTime() , event.getType() , event.getObjectid()));
        }

        return sb.toString();
    }

}
package edu.MTSSimulation;

import java.util.Comparator;

public class EventComparator implements Comparator<Event>
{
    @Override
    public int compare(Event x, Event y)
    {
        if (x.getEventRank() < y.getEventRank())
        {
            return -1;
        }
        if (x.getEventRank() > y.getEventRank())
        {
            return 1;
        }
        return 0;
    }
}

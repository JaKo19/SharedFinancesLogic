package main.java.com.sharedfinances.logic;

import java.util.ArrayList;
import java.util.List;

public class Management {
    private List<Event> events;

    public Management() {
        this.events = new ArrayList<>();
    }

    public List<Event> getEvents() {
        return events;
    }

    public void addEvent(Event event){
        events.add(event);
    }

    public void removeEvent(Event event){
        events.remove(event);
    }
}

package com.calendar.service;

import com.calendar.model.Event;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// In-memory storage for events, keeps them sorted by start time
public class InMemoryEventStorage implements EventStorage {

    private final List<Event> events;

    public InMemoryEventStorage() {
        this.events = new ArrayList<>();
    }

    @Override
    public void save(Event event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        events.add(event);
        events.sort(Event::compareTo);
    }

    @Override
    public List<Event> findAll() {
        return new ArrayList<>(events);
    }

    @Override
    public List<Event> findByDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        return events.stream()
            .filter(event -> event.isOnDate(date.atStartOfDay()))
            .sorted()
            .collect(Collectors.toList());
    }

    @Override
    public boolean delete(String eventId) {
        return events.removeIf(event -> event.getId().equals(eventId));
    }

    @Override
    public void clear() {
        events.clear();
    }

    @Override
    public int count() {
        return events.size();
    }
}

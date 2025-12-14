package com.calendar.service;

import com.calendar.model.Event;
import java.time.LocalDate;
import java.util.List;

// Interface for event storage operations
public interface EventStorage {

    void save(Event event);

    List<Event> findAll();

    List<Event> findByDate(LocalDate date);

    boolean delete(String eventId);

    void clear();

    int count();
}

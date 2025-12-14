package com.calendar.service;

import com.calendar.exception.EventOverlapException;
import com.calendar.model.Event;
import com.calendar.model.TimeSlot;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// Main service for calendar operations - validates events and coordinates storage
public class CalendarServiceImpl {

    private final EventStorage storage;
    private final SlotFinder slotFinder;

    public CalendarServiceImpl(EventStorage storage, SlotFinder slotFinder) {
        if (storage == null) {
            throw new IllegalArgumentException("EventStorage cannot be null");
        }
        if (slotFinder == null) {
            throw new IllegalArgumentException("SlotFinder cannot be null");
        }
        this.storage = storage;
        this.slotFinder = slotFinder;
    }

    public void addEvent(Event event) {
        if (hasOverlap(event)) {
            throw new EventOverlapException(
                "Event overlaps with existing event(s). Cannot add overlapping events.");
        }
        storage.save(event);
    }

    private boolean hasOverlap(Event newEvent) {
        return storage.findAll().stream()
            .anyMatch(existingEvent -> existingEvent.overlapsWith(newEvent));
    }

    public List<Event> listEventsForToday() {
        return listEventsForDate(LocalDateTime.now());
    }

    public List<Event> listRemainingEventsForToday() {
        LocalDateTime now = LocalDateTime.now();
        return storage.findByDate(now.toLocalDate()).stream()
            .filter(event -> event.getEndTime().isAfter(now))
            .sorted()
            .toList();
    }

    public List<Event> listEventsForDate(LocalDateTime date) {
        return storage.findByDate(date.toLocalDate());
    }

    public Optional<TimeSlot> findNextAvailableSlot(int durationInMinutes) {
        return findNextAvailableSlot(durationInMinutes, LocalDateTime.now());
    }

    public Optional<TimeSlot> findNextAvailableSlot(int durationInMinutes, LocalDateTime date) {
        return slotFinder.findNextAvailableSlot(durationInMinutes, date.toLocalDate());
    }

    public List<TimeSlot> findAllAvailableSlots(int durationInMinutes) {
        return slotFinder.findAllAvailableSlots(durationInMinutes, LocalDate.now());
    }

    public List<TimeSlot> findAllAvailableSlots(int durationInMinutes, LocalDateTime date) {
        return slotFinder.findAllAvailableSlots(durationInMinutes, date.toLocalDate());
    }

    public List<Event> getAllEvents() {
        return storage.findAll();
    }

    public int getEventCount() {
        return storage.count();
    }

    public boolean deleteEvent(String eventId) {
        return storage.delete(eventId);
    }

    public void clearAllEvents() {
        storage.clear();
    }
}

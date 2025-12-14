package com.calendar.service;

import com.calendar.exception.EventOverlapException;
import com.calendar.model.Event;
import com.calendar.model.TimeSlot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CalendarServiceImplTest {

    private CalendarServiceImpl service;

    @BeforeEach
    void setUp() {
        EventStorage storage = new InMemoryEventStorage();
        SlotFinder slotFinder = new StandardSlotFinder(storage);
        service = new CalendarServiceImpl(storage, slotFinder);
    }

    @Test
    void testAddEvent_Success() {
        Event event = Event.create("Meeting",
            LocalDateTime.of(2025, 12, 15, 10, 0),
            LocalDateTime.of(2025, 12, 15, 11, 0));

        service.addEvent(event);

        assertEquals(1, service.getEventCount());
        assertTrue(service.getAllEvents().contains(event));
    }

    @Test
    void testAddEvent_Overlap() {
        Event existing = Event.create("Existing",
            LocalDateTime.of(2025, 12, 15, 10, 0),
            LocalDateTime.of(2025, 12, 15, 11, 0));

        Event overlapping = Event.create("Overlapping",
            LocalDateTime.of(2025, 12, 15, 10, 30),
            LocalDateTime.of(2025, 12, 15, 11, 30));

        service.addEvent(existing);

        assertThrows(EventOverlapException.class, () -> service.addEvent(overlapping));
        assertEquals(1, service.getEventCount());
    }

    @Test
    void testAddEvent_BackToBack() {
        Event first = Event.create("First",
            LocalDateTime.of(2025, 12, 15, 10, 0),
            LocalDateTime.of(2025, 12, 15, 11, 0));

        Event second = Event.create("Second",
            LocalDateTime.of(2025, 12, 15, 11, 0),
            LocalDateTime.of(2025, 12, 15, 12, 0));

        service.addEvent(first);
        service.addEvent(second);

        assertEquals(2, service.getEventCount());
    }

    @Test
    void testListEventsForToday() {
        LocalDate today = LocalDate.now();
        Event event = Event.create("Today's Event",
            today.atTime(10, 0),
            today.atTime(11, 0));

        service.addEvent(event);

        List<Event> events = service.listEventsForToday();
        assertEquals(1, events.size());
        assertTrue(events.contains(event));
    }

    @Test
    void testListRemainingEventsForToday() {
        LocalDateTime now = LocalDateTime.now();

        Event past = Event.create("Past",
            now.minusHours(2),
            now.minusHours(1));

        Event future = Event.create("Future",
            now.plusHours(1),
            now.plusHours(2));

        service.addEvent(past);
        service.addEvent(future);

        List<Event> remaining = service.listRemainingEventsForToday();
        assertEquals(1, remaining.size());
        assertEquals(future, remaining.get(0));
    }

    @Test
    void testListEventsForDate() {
        LocalDateTime date = LocalDateTime.of(2025, 12, 15, 0, 0);
        Event event = Event.create("Meeting",
            LocalDateTime.of(2025, 12, 15, 10, 0),
            LocalDateTime.of(2025, 12, 15, 11, 0));

        service.addEvent(event);

        List<Event> events = service.listEventsForDate(date);
        assertEquals(1, events.size());
        assertEquals(event, events.get(0));
    }

    @Test
    void testFindAvailableSlots() {
        LocalDateTime date = LocalDateTime.of(2025, 12, 15, 0, 0);
        List<TimeSlot> slots = service.findAllAvailableSlots(60, date);

        assertFalse(slots.isEmpty());
        assertEquals(60, slots.get(0).getDurationInMinutes());
    }

    @Test
    void testClearAllEvents() {
        service.addEvent(Event.create("Event 1",
            LocalDateTime.of(2025, 12, 15, 10, 0),
            LocalDateTime.of(2025, 12, 15, 11, 0)));

        service.clearAllEvents();

        assertEquals(0, service.getEventCount());
        assertTrue(service.getAllEvents().isEmpty());
    }
}

package com.calendar.service;

import com.calendar.model.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryEventStorageTest {

    private InMemoryEventStorage storage;

    @BeforeEach
    void setUp() {
        storage = new InMemoryEventStorage();
    }

    @Test
    void testSave_SingleEvent() {
        Event event = Event.create("Meeting",
            LocalDateTime.of(2025, 12, 15, 10, 0),
            LocalDateTime.of(2025, 12, 15, 11, 0));

        storage.save(event);

        assertEquals(1, storage.count());
        assertTrue(storage.findAll().contains(event));
    }

    @Test
    void testSave_MaintainsSortedOrder() {
        Event late = Event.create("Late",
            LocalDateTime.of(2025, 12, 15, 14, 0),
            LocalDateTime.of(2025, 12, 15, 15, 0));

        Event early = Event.create("Early",
            LocalDateTime.of(2025, 12, 15, 10, 0),
            LocalDateTime.of(2025, 12, 15, 11, 0));

        storage.save(late);
        storage.save(early);

        List<Event> events = storage.findAll();
        assertEquals(early, events.get(0));
        assertEquals(late, events.get(1));
    }

    @Test
    void testFindByDate() {
        Event event1 = Event.create("Morning",
            LocalDateTime.of(2025, 12, 15, 9, 0),
            LocalDateTime.of(2025, 12, 15, 10, 0));

        Event event2 = Event.create("Next Day",
            LocalDateTime.of(2025, 12, 16, 10, 0),
            LocalDateTime.of(2025, 12, 16, 11, 0));

        storage.save(event1);
        storage.save(event2);

        List<Event> eventsOn15th = storage.findByDate(LocalDate.of(2025, 12, 15));

        assertEquals(1, eventsOn15th.size());
        assertTrue(eventsOn15th.contains(event1));
        assertFalse(eventsOn15th.contains(event2));
    }

    @Test
    void testClear() {
        storage.save(Event.create("Event",
            LocalDateTime.of(2025, 12, 15, 10, 0),
            LocalDateTime.of(2025, 12, 15, 11, 0)));

        storage.clear();

        assertEquals(0, storage.count());
        assertTrue(storage.findAll().isEmpty());
    }

    @Test
    void testDelete() {
        Event event1 = Event.create("Meeting",
            LocalDateTime.of(2025, 12, 15, 10, 0),
            LocalDateTime.of(2025, 12, 15, 11, 0));

        Event event2 = Event.create("Lunch",
            LocalDateTime.of(2025, 12, 15, 12, 0),
            LocalDateTime.of(2025, 12, 15, 13, 0));

        storage.save(event1);
        storage.save(event2);

        assertEquals(2, storage.count());

        boolean deleted = storage.delete(event1.getId());

        assertTrue(deleted);
        assertEquals(1, storage.count());
        assertFalse(storage.findAll().contains(event1));
        assertTrue(storage.findAll().contains(event2));
    }
}

package com.calendar.service;

import com.calendar.model.Event;
import com.calendar.model.TimeSlot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class StandardSlotFinderTest {

    private EventStorage storage;
    private StandardSlotFinder slotFinder;

    @BeforeEach
    void setUp() {
        storage = new InMemoryEventStorage();
        slotFinder = new StandardSlotFinder(storage);
    }

    @Test
    void testFindNextAvailableSlot_NoEvents() {
        LocalDate date = LocalDate.of(2025, 12, 15);
        Optional<TimeSlot> slot = slotFinder.findNextAvailableSlot(60, date);

        assertTrue(slot.isPresent());
        assertEquals(LocalDateTime.of(2025, 12, 15, 9, 0), slot.get().getStartTime());
        assertEquals(60, slot.get().getDurationInMinutes());
    }

    @Test
    void testFindNextAvailableSlot_WithExistingEvents() {
        storage.save(Event.create("Meeting",
            LocalDateTime.of(2025, 12, 15, 10, 0),
            LocalDateTime.of(2025, 12, 15, 11, 0)));

        LocalDate date = LocalDate.of(2025, 12, 15);
        Optional<TimeSlot> slot = slotFinder.findNextAvailableSlot(60, date);

        assertTrue(slot.isPresent());
        assertEquals(LocalDateTime.of(2025, 12, 15, 9, 0), slot.get().getStartTime());
    }

    @Test
    void testFindNextAvailableSlot_FullDay() {
        // Fill entire work day (9-18)
        for (int hour = 9; hour < 18; hour++) {
            storage.save(Event.create("Meeting " + hour,
                LocalDateTime.of(2025, 12, 15, hour, 0),
                LocalDateTime.of(2025, 12, 15, hour + 1, 0)));
        }

        LocalDate date = LocalDate.of(2025, 12, 15);
        Optional<TimeSlot> slot = slotFinder.findNextAvailableSlot(60, date);

        assertFalse(slot.isPresent());
    }

    @Test
    void testFindNextAvailableSlot_InvalidDuration() {
        LocalDate date = LocalDate.of(2025, 12, 15);

        assertThrows(IllegalArgumentException.class, () ->
            slotFinder.findNextAvailableSlot(0, date)
        );
    }
}

package com.calendar.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for TimeSlot class.
 */
class TimeSlotTest {

    @Test
    void testTimeSlotCreation_Success() {
        LocalDateTime start = LocalDateTime.of(2025, 12, 15, 10, 0);
        LocalDateTime end = LocalDateTime.of(2025, 12, 15, 11, 0);

        TimeSlot slot = new TimeSlot(start, end);

        assertEquals(start, slot.getStartTime());
        assertEquals(end, slot.getEndTime());
        assertEquals(60, slot.getDurationInMinutes());
    }

    @Test
    void testTimeSlotCreation_NullStartTime() {
        LocalDateTime end = LocalDateTime.of(2025, 12, 15, 11, 0);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            new TimeSlot(null, end)
        );
        assertTrue(exception.getMessage().contains("cannot be null"));
    }

    @Test
    void testTimeSlotCreation_NullEndTime() {
        LocalDateTime start = LocalDateTime.of(2025, 12, 15, 10, 0);

        assertThrows(IllegalArgumentException.class, () ->
            new TimeSlot(start, null)
        );
    }

    @Test
    void testTimeSlotCreation_EndBeforeStart() {
        LocalDateTime start = LocalDateTime.of(2025, 12, 15, 11, 0);
        LocalDateTime end = LocalDateTime.of(2025, 12, 15, 10, 0);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            new TimeSlot(start, end)
        );
        assertTrue(exception.getMessage().contains("after start time"));
    }

    @Test
    void testTimeSlotCreation_EqualStartAndEnd() {
        LocalDateTime time = LocalDateTime.of(2025, 12, 15, 10, 0);

        assertThrows(IllegalArgumentException.class, () ->
            new TimeSlot(time, time)
        );
    }

    @Test
    void testGetDurationInMinutes_ShortSlot() {
        TimeSlot slot = new TimeSlot(
            LocalDateTime.of(2025, 12, 15, 10, 0),
            LocalDateTime.of(2025, 12, 15, 10, 15)
        );

        assertEquals(15, slot.getDurationInMinutes());
    }

    @Test
    void testGetDurationInMinutes_MultiHourSlot() {
        TimeSlot slot = new TimeSlot(
            LocalDateTime.of(2025, 12, 15, 9, 0),
            LocalDateTime.of(2025, 12, 15, 17, 0)
        );

        assertEquals(480, slot.getDurationInMinutes());
    }

    @Test
    void testToString_ContainsTimeAndDuration() {
        TimeSlot slot = new TimeSlot(
            LocalDateTime.of(2025, 12, 15, 10, 30),
            LocalDateTime.of(2025, 12, 15, 12, 0)
        );

        String str = slot.toString();
        assertTrue(str.contains("10:30"));
        assertTrue(str.contains("12:00"));
        assertTrue(str.contains("90"));
    }
}

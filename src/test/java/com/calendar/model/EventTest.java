package com.calendar.model;

import com.calendar.exception.InvalidEventException;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class EventTest {

    @Test
    void testEventCreation_Success() {
        LocalDateTime start = LocalDateTime.of(2025, 12, 15, 10, 0);
        LocalDateTime end = LocalDateTime.of(2025, 12, 15, 11, 0);
        Event event = new Event("Meeting", start, end);

        assertNotNull(event.getId());
        assertEquals("Meeting", event.getTitle());
        assertEquals(start, event.getStartTime());
        assertEquals(end, event.getEndTime());
        assertEquals(60, event.getDurationInMinutes());
    }

    @Test
    void testEventCreation_WithDescription() {
        LocalDateTime start = LocalDateTime.of(2025, 12, 15, 10, 0);
        LocalDateTime end = LocalDateTime.of(2025, 12, 15, 11, 0);
        Event event = Event.create("Meeting", "Project discussion", start, end);

        assertEquals("Meeting", event.getTitle());
        assertEquals("Project discussion", event.getDescription());
    }

    @Test
    void testEventCreation_EmptyTitle() {
        LocalDateTime start = LocalDateTime.of(2025, 12, 15, 10, 0);
        LocalDateTime end = LocalDateTime.of(2025, 12, 15, 11, 0);

        assertThrows(InvalidEventException.class, () ->
            new Event("", start, end)
        );
    }

    @Test
    void testEventCreation_NullStartTime() {
        LocalDateTime end = LocalDateTime.of(2025, 12, 15, 11, 0);

        assertThrows(InvalidEventException.class, () ->
            new Event("Meeting", null, end)
        );
    }

    @Test
    void testEventCreation_EndBeforeStart() {
        LocalDateTime start = LocalDateTime.of(2025, 12, 15, 11, 0);
        LocalDateTime end = LocalDateTime.of(2025, 12, 15, 10, 0);

        assertThrows(InvalidEventException.class, () ->
            new Event("Meeting", start, end)
        );
    }

    @Test
    void testOverlapsWith_Overlapping() {
        Event event1 = Event.create("Event 1",
            LocalDateTime.of(2025, 12, 15, 10, 0),
            LocalDateTime.of(2025, 12, 15, 11, 0));

        Event event2 = Event.create("Event 2",
            LocalDateTime.of(2025, 12, 15, 10, 30),
            LocalDateTime.of(2025, 12, 15, 11, 30));

        assertTrue(event1.overlapsWith(event2));
        assertTrue(event2.overlapsWith(event1));
    }

    @Test
    void testOverlapsWith_NotOverlapping() {
        Event event1 = Event.create("Event 1",
            LocalDateTime.of(2025, 12, 15, 10, 0),
            LocalDateTime.of(2025, 12, 15, 11, 0));

        Event event2 = Event.create("Event 2",
            LocalDateTime.of(2025, 12, 15, 14, 0),
            LocalDateTime.of(2025, 12, 15, 15, 0));

        assertFalse(event1.overlapsWith(event2));
        assertFalse(event2.overlapsWith(event1));
    }

    @Test
    void testOverlapsWith_BackToBack() {
        Event event1 = Event.create("Event 1",
            LocalDateTime.of(2025, 12, 15, 10, 0),
            LocalDateTime.of(2025, 12, 15, 11, 0));

        Event event2 = Event.create("Event 2",
            LocalDateTime.of(2025, 12, 15, 11, 0),
            LocalDateTime.of(2025, 12, 15, 12, 0));

        assertFalse(event1.overlapsWith(event2));
    }

    @Test
    void testIsOnDate() {
        Event event = Event.create("Meeting",
            LocalDateTime.of(2025, 12, 15, 10, 0),
            LocalDateTime.of(2025, 12, 15, 11, 0));

        assertTrue(event.isOnDate(LocalDateTime.of(2025, 12, 15, 0, 0)));
        assertFalse(event.isOnDate(LocalDateTime.of(2025, 12, 16, 0, 0)));
    }

    @Test
    void testIsAfter() {
        Event event = Event.create("Meeting",
            LocalDateTime.of(2025, 12, 15, 10, 0),
            LocalDateTime.of(2025, 12, 15, 11, 0));

        assertTrue(event.isAfter(LocalDateTime.of(2025, 12, 15, 9, 0)));
        assertFalse(event.isAfter(LocalDateTime.of(2025, 12, 15, 11, 0)));
    }

    @Test
    void testCompareTo_SortsByStartTime() {
        Event early = Event.create("Early",
            LocalDateTime.of(2025, 12, 15, 9, 0),
            LocalDateTime.of(2025, 12, 15, 10, 0));

        Event late = Event.create("Late",
            LocalDateTime.of(2025, 12, 15, 14, 0),
            LocalDateTime.of(2025, 12, 15, 15, 0));

        assertTrue(early.compareTo(late) < 0);
        assertTrue(late.compareTo(early) > 0);
    }

    @Test
    void testEquals_SameId() {
        Event event1 = new Event("id1", "Meeting", null,
            LocalDateTime.of(2025, 12, 15, 10, 0),
            LocalDateTime.of(2025, 12, 15, 11, 0));

        Event event2 = new Event("id1", "Different Title", null,
            LocalDateTime.of(2025, 12, 15, 14, 0),
            LocalDateTime.of(2025, 12, 15, 15, 0));

        assertEquals(event1, event2);
    }

    @Test
    void testToString_ContainsTitleAndTime() {
        Event event = Event.create("Team Meeting",
            LocalDateTime.of(2025, 12, 15, 10, 30),
            LocalDateTime.of(2025, 12, 15, 11, 30));

        String str = event.toString();
        assertTrue(str.contains("Team Meeting"));
        assertTrue(str.contains("10:30"));
        assertTrue(str.contains("11:30"));
    }
}

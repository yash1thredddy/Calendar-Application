package com.calendar.model;

import com.calendar.exception.InvalidEventException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

// Represents a calendar event with title and time
public class Event implements Comparable<Event> {
    private final String id;
    private final String title;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm");

    // Constructor with all fields
    public Event(String id, String title, LocalDateTime startTime, LocalDateTime endTime) {
        validateEvent(id, title, startTime, endTime);
        this.id = id;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Constructor with auto-generated ID
    public Event(String title, LocalDateTime startTime, LocalDateTime endTime) {
        this(UUID.randomUUID().toString(), title, startTime, endTime);
    }

    // Create a new event (Static Factory Method)
    public static Event create(String title, LocalDateTime startTime, LocalDateTime endTime) {
        return new Event(title, startTime, endTime);
    }

    private void validateEvent(String id, String title, LocalDateTime startTime, LocalDateTime endTime) {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidEventException("Event ID cannot be null or empty");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new InvalidEventException("Event title cannot be null or empty");
        }
        if (startTime == null) {
            throw new InvalidEventException("Start time cannot be null");
        }
        if (endTime == null) {
            throw new InvalidEventException("End time cannot be null");
        }
        if (endTime.isBefore(startTime) || endTime.isEqual(startTime)) {
            throw new InvalidEventException("End time must be after start time");
        }
    }

    // Check if this event overlaps with another
    public boolean overlapsWith(Event other) {
        return this.startTime.isBefore(other.endTime) && other.startTime.isBefore(this.endTime);
    }

    // Check if this event is on the given date
    public boolean isOnDate(LocalDateTime date) {
        return startTime.toLocalDate().isEqual(date.toLocalDate());
    }

    // Check if this event starts after the given time
    public boolean isAfter(LocalDateTime time) {
        return startTime.isAfter(time);
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public long getDurationInMinutes() {
        return java.time.Duration.between(startTime, endTime).toMinutes();
    }

    // Sort by start time, then by end time
    @Override
    public int compareTo(Event other) {
        int startComparison = this.startTime.compareTo(other.startTime);
        if (startComparison != 0) {
            return startComparison;
        }
        return this.endTime.compareTo(other.endTime);
    }

    // Events are equal if they have the same ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(id, event.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("[%s - %s] %s",
            startTime.format(FORMATTER),
            endTime.format(FORMATTER),
            title);
    }
}

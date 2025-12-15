package com.calendar.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Represents an available time slot
public class TimeSlot {
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm");

    public TimeSlot(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Start and end times cannot be null");
        }
        if (endTime.isBefore(startTime) || endTime.isEqual(startTime)) {
            throw new IllegalArgumentException("End time must be after start time");
        }
        this.startTime = startTime;
        this.endTime = endTime;
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

    @Override
    public String toString() {
        return String.format("Available: %s - %s (Duration: %d minutes)",
            startTime.format(FORMATTER),
            endTime.format(FORMATTER),
            getDurationInMinutes());
    }
}

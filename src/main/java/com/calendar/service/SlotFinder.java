package com.calendar.service;

import com.calendar.model.TimeSlot;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// Interface for finding available time slots
public interface SlotFinder {

    // Find the next available slot of specified duration
    Optional<TimeSlot> findNextAvailableSlot(int durationMinutes, LocalDate date);

    // Find all available slots of specified duration for the day
    List<TimeSlot> findAllAvailableSlots(int durationMinutes, LocalDate date);
}

package com.calendar.service;

import com.calendar.model.Event;
import com.calendar.model.TimeSlot;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Finds available time slots using 9 AM - 6 PM working hours
public class StandardSlotFinder implements SlotFinder {

    private final EventStorage storage;

    // Working hours: 9 AM to 6 PM
    private static final int DEFAULT_START_HOUR = 9;
    private static final int DEFAULT_END_HOUR = 18;

    public StandardSlotFinder(EventStorage storage) {
        if (storage == null) {
            throw new IllegalArgumentException("EventStorage cannot be null");
        }
        this.storage = storage;
    }

    @Override
    public Optional<TimeSlot> findNextAvailableSlot(int durationMinutes, LocalDate date) {
        List<TimeSlot> allSlots = findAllAvailableSlots(durationMinutes, date);
        return allSlots.isEmpty() ? Optional.empty() : Optional.of(allSlots.get(0));
    }

    @Override
    public List<TimeSlot> findAllAvailableSlots(int durationMinutes, LocalDate date) {
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("Duration must be positive");
        }
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }

        List<TimeSlot> availableSlots = new ArrayList<>();
        LocalDateTime searchStart = calculateSearchStart(date);
        LocalDateTime dayEnd = date.atTime(DEFAULT_END_HOUR, 0);

        List<Event> dayEvents = storage.findByDate(date);

        // No events - return all possible slots that fit
        if (dayEvents.isEmpty()) {
            addSlotsInRange(availableSlots, searchStart, dayEnd, durationMinutes);
            return availableSlots;
        }

        // Check gap before first event
        Event firstEvent = dayEvents.get(0);
        addSlotsInRange(availableSlots, searchStart, firstEvent.getStartTime(), durationMinutes);

        // Check gaps between consecutive events
        for (int i = 0; i < dayEvents.size() - 1; i++) {
            LocalDateTime gapStart = dayEvents.get(i).getEndTime();
            LocalDateTime gapEnd = dayEvents.get(i + 1).getStartTime();

            // Skip if gap is in the past
            if (gapStart.isBefore(searchStart)) {
                gapStart = searchStart;
            }

            addSlotsInRange(availableSlots, gapStart, gapEnd, durationMinutes);
        }

        // Check gap after last event
        Event lastEvent = dayEvents.get(dayEvents.size() - 1);
        LocalDateTime lastGapStart = lastEvent.getEndTime();
        if (lastGapStart.isBefore(searchStart)) {
            lastGapStart = searchStart;
        }
        addSlotsInRange(availableSlots, lastGapStart, dayEnd, durationMinutes);

        return availableSlots;
    }

    // Add all possible slots that fit in the given time range
    private void addSlotsInRange(List<TimeSlot> slots, LocalDateTime start, LocalDateTime end, int durationMinutes) {
        if (start.isAfter(end) || start.isEqual(end)) {
            return;
        }

        LocalDateTime slotStart = start;
        while (canFitSlot(slotStart, end, durationMinutes)) {
            LocalDateTime slotEnd = slotStart.plusMinutes(durationMinutes);
            slots.add(new TimeSlot(slotStart, slotEnd));
            slotStart = slotStart.plusMinutes(30); // Move forward in 30-min increments
        }
    }

    // Determine where to start searching
    private LocalDateTime calculateSearchStart(LocalDate date) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dayStart = date.atTime(DEFAULT_START_HOUR, 0);

        if (date.isEqual(now.toLocalDate())) {
            return now.isAfter(dayStart) ? now : dayStart;
        } else {
            return dayStart;
        }
    }

    // Check if a slot can fit in the time window
    private boolean canFitSlot(LocalDateTime start, LocalDateTime end, int durationMinutes) {
        if (start.isAfter(end) || start.isEqual(end)) {
            return false;
        }
        long availableMinutes = java.time.Duration.between(start, end).toMinutes();
        return availableMinutes >= durationMinutes;
    }
}

package com.calendar.factory;

import com.calendar.service.*;

// Creates and wires up the calendar service with all its dependencies
public class ServiceFactory {

    // Creates a calendar service with in-memory storage
    public static CalendarServiceImpl createService() {
        EventStorage storage = new InMemoryEventStorage();
        SlotFinder slotFinder = new StandardSlotFinder(storage);
        return new CalendarServiceImpl(storage, slotFinder);
    }
}

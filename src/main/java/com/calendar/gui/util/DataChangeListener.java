package com.calendar.gui.util;

// Interface for observing data changes in the calendar
// Controllers implement this to refresh their views when events are added/deleted
public interface DataChangeListener {
    void onDataChanged();
}

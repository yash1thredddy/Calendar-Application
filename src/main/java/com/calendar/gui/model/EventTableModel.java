package com.calendar.gui.model;

import com.calendar.gui.util.DateTimeUtil;
import com.calendar.model.Event;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

// JavaFX wrapper for Event model to use with TableView
// Provides StringProperty objects for automatic UI binding
public class EventTableModel {
    private final Event event;
    private final StringProperty title;
    private final StringProperty startDate;
    private final StringProperty startTime;
    private final StringProperty endTime;
    private final StringProperty duration;

    public EventTableModel(Event event) {
        this.event = event;
        this.title = new SimpleStringProperty(event.getTitle());
        this.startDate = new SimpleStringProperty(DateTimeUtil.formatDate(event.getStartTime()));
        this.startTime = new SimpleStringProperty(DateTimeUtil.formatTime(event.getStartTime()));
        this.endTime = new SimpleStringProperty(DateTimeUtil.formatTime(event.getEndTime()));
        this.duration = new SimpleStringProperty(DateTimeUtil.formatDuration(event.getDurationInMinutes()));
    }

    public Event getEvent() {
        return event;
    }

    public StringProperty titleProperty() {
        return title;
    }

    public StringProperty startDateProperty() {
        return startDate;
    }

    public StringProperty startTimeProperty() {
        return startTime;
    }

    public StringProperty endTimeProperty() {
        return endTime;
    }

    public StringProperty durationProperty() {
        return duration;
    }
}

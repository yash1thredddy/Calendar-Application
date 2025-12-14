package com.calendar.exception;

// Thrown when trying to add an event that overlaps with an existing one
public class EventOverlapException extends RuntimeException {

    public EventOverlapException(String message) {
        super(message);
    }

    public EventOverlapException(String message, Throwable cause) {
        super(message, cause);
    }
}

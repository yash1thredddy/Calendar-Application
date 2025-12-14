package com.calendar.exception;

// Thrown when event data is invalid (empty title, bad times, etc.)
public class InvalidEventException extends RuntimeException {

    public InvalidEventException(String message) {
        super(message);
    }

    public InvalidEventException(String message, Throwable cause) {
        super(message, cause);
    }
}

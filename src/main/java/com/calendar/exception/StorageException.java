package com.calendar.exception;

// Thrown when storage operations fail (file I/O, serialization, etc.)
public class StorageException extends RuntimeException {

    public StorageException(String message) {
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}

package com.calendar.gui.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Utility class for consistent date/time formatting across the GUI
public class DateTimeUtil {

    public static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MM-dd-yyyy");

    public static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm");

    public static String formatDate(LocalDateTime dateTime) {
        return dateTime.format(DATE_FORMATTER);
    }

    public static String formatTime(LocalDateTime dateTime) {
        return dateTime.format(TIME_FORMATTER);
    }

    public static String formatDuration(long minutes) {
        long hours = minutes / 60;
        long remainingMinutes = minutes % 60;

        if (hours == 0) {
            return minutes + " min";
        } else if (remainingMinutes == 0) {
            return hours + " hr";
        } else {
            return hours + " hr " + remainingMinutes + " min";
        }
    }
}

package com.calendar;

import com.calendar.exception.EventOverlapException;
import com.calendar.exception.InvalidEventException;
import com.calendar.factory.ServiceFactory;
import com.calendar.model.Event;
import com.calendar.model.TimeSlot;
import com.calendar.service.CalendarServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

// Main application class - handles the CLI menu and user interaction
public class CalendarApp {
    private final CalendarServiceImpl calendarService;
    private final Scanner scanner;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM-dd-yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public CalendarApp() {
        this.calendarService = ServiceFactory.createService();
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        CalendarApp app = new CalendarApp();
        app.run();
    }

    public void run() {
        printWelcome();

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        createEvent();
                        break;
                    case "2":
                        listEventsForToday();
                        break;
                    case "3":
                        listRemainingEventsForToday();
                        break;
                    case "4":
                        listEventsForSpecificDay();
                        break;
                    case "5":
                        findNextAvailableSlot();
                        break;
                    case "6":
                        listAllEvents();
                        break;
                    case "7":
                        deleteEvent();
                        break;
                    case "8":
                        running = false;
                        System.out.println("\n" + "=".repeat(50));
                        System.out.println("Thank you for using Calendar Application!");
                        System.out.println("=".repeat(50));
                        break;
                    default:
                        System.out.println("\nInvalid choice. Please enter a number from 1-8.");
                }
            } catch (EventOverlapException e) {
                System.out.println("\nOverlap Error: " + e.getMessage());
                System.out.println("Please choose a different time for your event.");
            } catch (InvalidEventException e) {
                System.out.println("\nInvalid Event: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("\nError: " + e.getMessage());
            }

            // Removed the "Press Enter to continue" for smoother flow
        }

        scanner.close();
    }

    private void printWelcome() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("              CALENDAR APPLICATION");
        System.out.println("=".repeat(50));
        System.out.println("Total Events: " + calendarService.getEventCount());
        System.out.println("=".repeat(50));
    }

    private void printMenu() {
        System.out.println("\n" + "-".repeat(50));
        System.out.println("MAIN MENU");
        System.out.println("-".repeat(50));
        System.out.println("1. Create New Event");
        System.out.println("2. List All Events for Today");
        System.out.println("3. List Remaining Events for Today");
        System.out.println("4. List Events for Specific Day");
        System.out.println("5. Find Next Available Slot");
        System.out.println("6. List All Events");
        System.out.println("7. Delete Event");
        System.out.println("8. Exit");
        System.out.println("-".repeat(50));
        System.out.print("Enter your choice (1-8): ");
    }

    private void createEvent() {
        System.out.println("\n--- Create New Event ---");

        System.out.print("Event Title: ");
        String title = scanner.nextLine().trim();

        if (title.isEmpty()) {
            System.out.println("Title cannot be empty!");
            return;
        }

        LocalDateTime startTime = promptForDateTime("Start Time (MM-dd-yyyy HH:mm): ");
        LocalDateTime endTime = promptForDateTime("End Time   (MM-dd-yyyy HH:mm): ");

        // Check if event is in the past and re-validate until valid future time or user cancels
        while (startTime.isBefore(LocalDateTime.now())) {
            System.out.println("\nWarning: This event is in the past!");
            System.out.print("Do you want to enter a new future time? (y/n): ");
            String choice = scanner.nextLine().trim().toLowerCase();

            if (choice.equals("y")) {
                System.out.println("\nPlease enter future times:");
                startTime = promptForDateTime("Start Time (MM-dd-yyyy HH:mm): ");
                endTime = promptForDateTime("End Time   (MM-dd-yyyy HH:mm): ");
                // Loop continues to re-validate the new times
            } else {
                // User chose 'n' or anything else - keep the past time
                break;
            }
        }

        Event event = Event.create(title, startTime, endTime);
        calendarService.addEvent(event);

        System.out.println("\nEvent created successfully!");
        printEventDetails(event);
        printDaySummary(startTime.toLocalDate());

        // Quick actions after creating event
        showQuickActions();
    }

    private void showQuickActions() {
        System.out.println("\nQuick Actions:");
        System.out.println("  1. Create another event");
        System.out.println("  2. View today's events");
        System.out.println("  3. Find available slots");
        System.out.println("  4. Back to main menu");
        System.out.print("Choose (1-4) or press Enter for main menu: ");

        String choice = scanner.nextLine().trim();

        try {
            switch (choice) {
                case "1":
                    createEvent();
                    break;
                case "2":
                    listEventsForToday();
                    break;
                case "3":
                    findNextAvailableSlot();
                    break;
                case "4":
                case "":
                    // Go back to main menu
                    break;
                default:
                    System.out.println("Invalid choice, returning to main menu.");
            }
        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

    private void showQuickActionsForToday() {
        System.out.println("\nQuick Actions:");
        System.out.println("  1. Create new event for today");
        System.out.println("  2. Find available slots today");
        System.out.println("  3. View events for specific day");
        System.out.println("  4. Back to main menu");
        System.out.print("Choose (1-4) or press Enter for main menu: ");

        String choice = scanner.nextLine().trim();

        try {
            switch (choice) {
                case "1":
                    createEvent();
                    break;
                case "2":
                    findNextAvailableSlot();
                    break;
                case "3":
                    listEventsForSpecificDay();
                    break;
                case "4":
                case "":
                    break;
                default:
                    System.out.println("Invalid choice, returning to main menu.");
            }
        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

    private void showQuickActionsForSpecificDay(LocalDate date) {
        System.out.println("\nQuick Actions:");
        System.out.println("  1. Create event on " + date.format(DATE_FORMATTER));
        System.out.println("  2. Find available slots on " + date.format(DATE_FORMATTER));
        System.out.println("  3. View another day");
        System.out.println("  4. Back to main menu");
        System.out.print("Choose (1-4) or press Enter for main menu: ");

        String choice = scanner.nextLine().trim();

        try {
            switch (choice) {
                case "1":
                    createEventOnDate(date);
                    break;
                case "2":
                    findSlotsOnDate(date);
                    break;
                case "3":
                    listEventsForSpecificDay();
                    break;
                case "4":
                case "":
                    break;
                default:
                    System.out.println("Invalid choice, returning to main menu.");
            }
        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

    private void showQuickActionsGeneral() {
        System.out.println("\nQuick Actions:");
        System.out.println("  1. Create new event");
        System.out.println("  2. Find available slots");
        System.out.println("  3. View today's events");
        System.out.println("  4. Back to main menu");
        System.out.print("Choose (1-4) or press Enter for main menu: ");

        String choice = scanner.nextLine().trim();

        try {
            switch (choice) {
                case "1":
                    createEvent();
                    break;
                case "2":
                    findNextAvailableSlot();
                    break;
                case "3":
                    listEventsForToday();
                    break;
                case "4":
                case "":
                    break;
                default:
                    System.out.println("Invalid choice, returning to main menu.");
            }
        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

    private void createEventOnDate(LocalDate date) {
        System.out.println("\n--- Create Event on " + date.format(DATE_FORMATTER) + " ---");

        System.out.print("Event Title: ");
        String title = scanner.nextLine().trim();

        if (title.isEmpty()) {
            System.out.println("Title cannot be empty!");
            return;
        }

        boolean eventCreated = false;
        while (!eventCreated) {
            System.out.print("Start Time (HH:mm): ");
            String startTimeStr = scanner.nextLine().trim();

            if (startTimeStr.equalsIgnoreCase("cancel")) {
                System.out.println("Event creation cancelled.");
                return;
            }

            LocalDateTime startTime = date.atTime(parseTime(startTimeStr));

            System.out.print("End Time (HH:mm): ");
            String endTimeStr = scanner.nextLine().trim();

            if (endTimeStr.equalsIgnoreCase("cancel")) {
                System.out.println("Event creation cancelled.");
                return;
            }

            LocalDateTime endTime = date.atTime(parseTime(endTimeStr));

            // Check if event is in the past
            if (startTime.isBefore(LocalDateTime.now())) {
                System.out.println("\nWarning: This event is in the past!");
                System.out.print("Do you want to enter a new future time? (y/n): ");
                String choice = scanner.nextLine().trim().toLowerCase();

                if (!choice.equals("y")) {
                    System.out.println("Event creation cancelled.");
                    return;
                }
                continue; // Ask for times again
            }

            // Try to create and add the event
            try {
                Event event = Event.create(title, startTime, endTime);
                calendarService.addEvent(event);

                System.out.println("\nEvent created successfully!");
                printEventDetails(event);
                eventCreated = true;

            } catch (EventOverlapException e) {
                System.out.println("\n" + e.getMessage());
                System.out.println("Please choose a different time for your event.");
                System.out.print("Do you want to try again with a different time? (y/n): ");
                String retry = scanner.nextLine().trim().toLowerCase();

                if (!retry.equals("y")) {
                    System.out.println("Event creation cancelled.");
                    return;
                }
                // Loop continues to ask for new times
            }
        }

        showQuickActions();
    }

    private void findSlotsOnDate(LocalDate date) {
        System.out.println("\n--- Find Slots on " + date.format(DATE_FORMATTER) + " ---");

        System.out.println("Select duration:");
        System.out.println("1. 30 minutes");
        System.out.println("2. 1 hour (60 minutes)");
        System.out.println("3. 2 hours (120 minutes)");
        System.out.println("4. Custom");
        System.out.print("Your choice (1-4): ");

        String durationChoice = scanner.nextLine().trim();
        int duration;

        switch (durationChoice) {
            case "1":
                duration = 30;
                break;
            case "2":
                duration = 60;
                break;
            case "3":
                duration = 120;
                break;
            case "4":
                duration = promptForDuration();
                break;
            default:
                System.out.println("Invalid choice. Using 60 minutes.");
                duration = 60;
        }

        List<TimeSlot> availableSlots = calendarService.findAllAvailableSlots(duration, date.atStartOfDay());

        System.out.println();
        if (availableSlots.isEmpty()) {
            System.out.println("No available slots found for " + duration + " minutes on " +
                date.format(DATE_FORMATTER));
        } else {
            System.out.println("Available Slots on " + date.format(DATE_FORMATTER) + ":");
            System.out.println("  Duration: " + duration + " minutes");
            System.out.println();

            int displayLimit = Math.min(availableSlots.size(), 10);
            for (int i = 0; i < displayLimit; i++) {
                TimeSlot slot = availableSlots.get(i);
                System.out.println("  " + (i + 1) + ". " +
                    slot.getStartTime().format(TIME_FORMATTER) + " - " +
                    slot.getEndTime().format(TIME_FORMATTER));
            }

            if (availableSlots.size() > 10) {
                System.out.println("\n  (Showing first 10 of " + availableSlots.size() + " available slots)");
            } else {
                System.out.println("\n  Total: " + availableSlots.size() + " slot(s) available");
            }

            System.out.print("\nWould you like to book one of these slots? (y/n): ");
            String wantsToBook = scanner.nextLine().trim().toLowerCase();

            if (wantsToBook.equals("y")) {
                bookSlot(availableSlots, displayLimit);
            }
        }
    }

    private java.time.LocalTime parseTime(String timeStr) {
        try {
            return java.time.LocalTime.parse(timeStr, TIME_FORMATTER);
        } catch (Exception e) {
             throw new IllegalArgumentException("Invalid time format. Use HH:mm (e.g., 14:30). Got: '" + timeStr + "' - " + e.getMessage());
        }
    }

    private void printEventDetails(Event event) {
        System.out.println("\nEvent Details:");
        System.out.println("  Title:    " + event.getTitle());
        System.out.println("  Date:     " + event.getStartTime().format(DATE_FORMATTER));
        System.out.println("  Time:     " +
            event.getStartTime().format(TIME_FORMATTER) + " - " +
            event.getEndTime().format(TIME_FORMATTER));
        System.out.println("  Duration: " + event.getDurationInMinutes() + " minutes");
    }

    private void printDaySummary(LocalDate date) {
        List<Event> dayEvents = calendarService.listEventsForDate(date.atStartOfDay());
        Optional<TimeSlot> nextSlot = calendarService.findNextAvailableSlot(60, date.atStartOfDay());

        System.out.println("\nSummary for " + date.format(DATE_FORMATTER) + ":");
        System.out.println("  Total events: " + dayEvents.size());

        long totalMinutes = dayEvents.stream()
            .mapToLong(Event::getDurationInMinutes)
            .sum();
        System.out.println("  Total scheduled time: " + formatDuration(totalMinutes));

        if (nextSlot.isPresent()) {
            System.out.println("  Next 60-min slot: " +
                nextSlot.get().getStartTime().format(TIME_FORMATTER) + " - " +
                nextSlot.get().getEndTime().format(TIME_FORMATTER));
        } else {
            System.out.println("  Next 60-min slot: No available slots");
        }
    }

    private void listEventsForToday() {
        System.out.println("\n--- Events for Today ---");
        List<Event> events = calendarService.listEventsForToday();
        displayEventsTable(events);

        // Quick actions for today's events
        showQuickActionsForToday();
    }

    private void listRemainingEventsForToday() {
        System.out.println("\n--- Remaining Events for Today ---");
        List<Event> events = calendarService.listRemainingEventsForToday();
        displayEventsTable(events);

        // Quick actions for remaining events
        showQuickActionsForToday();
    }

    private void listEventsForSpecificDay() {
        System.out.println("\n--- List Events for Specific Day ---");
        LocalDate date = promptForDate();
        LocalDateTime dateTime = date.atStartOfDay();

        System.out.println("\nEvents for " + date.format(DATE_FORMATTER) + ":");
        List<Event> events = calendarService.listEventsForDate(dateTime);
        displayEventsTable(events);

        // Quick actions for specific day
        showQuickActionsForSpecificDay(date);
    }

    private void findNextAvailableSlot() {
        System.out.println("\n--- Find Next Available Slot ---");

        System.out.println("Select duration:");
        System.out.println("1. 30 minutes");
        System.out.println("2. 1 hour (60 minutes)");
        System.out.println("3. 2 hours (120 minutes)");
        System.out.println("4. Custom");
        System.out.print("Your choice (1-4): ");

        String durationChoice = scanner.nextLine().trim();
        int duration;

        switch (durationChoice) {
            case "1":
                duration = 30;
                break;
            case "2":
                duration = 60;
                break;
            case "3":
                duration = 120;
                break;
            case "4":
                duration = promptForDuration();
                break;
            default:
                System.out.println("Invalid choice. Using 60 minutes.");
                duration = 60;
        }

        System.out.print("\nSearch for today? (y/n): ");
        String forToday = scanner.nextLine().trim().toLowerCase();

        List<TimeSlot> availableSlots;
        LocalDate searchDate;

        if (forToday.equals("y")) {
            availableSlots = calendarService.findAllAvailableSlots(duration);
            searchDate = LocalDate.now();
        } else {
            searchDate = promptForDate();
            availableSlots = calendarService.findAllAvailableSlots(duration, searchDate.atStartOfDay());
        }

        System.out.println();
        if (availableSlots.isEmpty()) {
            System.out.println("No available slots found for " + duration + " minutes on " +
                searchDate.format(DATE_FORMATTER));
        } else {
            System.out.println("Available Slots on " + searchDate.format(DATE_FORMATTER) + ":");
            System.out.println("  Duration: " + duration + " minutes");
            System.out.println();

            int displayLimit = Math.min(availableSlots.size(), 10);
            for (int i = 0; i < displayLimit; i++) {
                TimeSlot slot = availableSlots.get(i);
                System.out.println("  " + (i + 1) + ". " +
                    slot.getStartTime().format(TIME_FORMATTER) + " - " +
                    slot.getEndTime().format(TIME_FORMATTER));
            }

            if (availableSlots.size() > 10) {
                System.out.println("\n  (Showing first 10 of " + availableSlots.size() + " available slots)");
            } else {
                System.out.println("\n  Total: " + availableSlots.size() + " slot(s) available");
            }

            // Ask if user wants to book one of these slots
            System.out.print("\nWould you like to book one of these slots? (y/n): ");
            String wantsToBook = scanner.nextLine().trim().toLowerCase();

            if (wantsToBook.equals("y")) {
                bookSlot(availableSlots, displayLimit);
            }
        }
    }

    private void bookSlot(List<TimeSlot> availableSlots, int displayLimit) {
        System.out.print("Enter slot number (1-" + displayLimit + "): ");
        String choice = scanner.nextLine().trim();

        try {
            int slotIndex = Integer.parseInt(choice) - 1;

            if (slotIndex < 0 || slotIndex >= displayLimit) {
                System.out.println("Invalid slot number!");
                return;
            }

            TimeSlot selectedSlot = availableSlots.get(slotIndex);

            System.out.print("Event Title: ");
            String title = scanner.nextLine().trim();

            if (title.isEmpty()) {
                System.out.println("Title cannot be empty!");
                return;
            }

            Event event = Event.create(title, selectedSlot.getStartTime(), selectedSlot.getEndTime());
            calendarService.addEvent(event);

            System.out.println("\nEvent booked successfully!");
            printEventDetails(event);

            // Quick actions after booking
            showQuickActions();

        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter a number.");
        }
    }

    private void listAllEvents() {
        System.out.println("\n--- All Events ---");
        List<Event> events = calendarService.getAllEvents();
        displayEventsTable(events);

        // Quick actions for all events
        showQuickActionsGeneral();
    }

    private void deleteEvent() {
        System.out.println("\n--- Delete Event ---");
        List<Event> events = calendarService.getAllEvents();

        if (events.isEmpty()) {
            System.out.println("No events to delete.");
            return;
        }

        System.out.println("Select event to delete:\n");
        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            System.out.println("  " + (i + 1) + ". " + event);
        }

        System.out.print("\nEnter event number (or 'cancel' to go back): ");
        String input = scanner.nextLine().trim();

        if (input.equalsIgnoreCase("cancel")) {
            System.out.println("Deletion cancelled.");
            return;
        }

        try {
            int eventIndex = Integer.parseInt(input) - 1;

            if (eventIndex < 0 || eventIndex >= events.size()) {
                System.out.println("Invalid event number!");
                return;
            }

            Event eventToDelete = events.get(eventIndex);

            System.out.print("\nConfirm deletion of \"" + eventToDelete.getTitle() + "\"? (y/n): ");
            String confirm = scanner.nextLine().trim().toLowerCase();

            if (confirm.equals("y")) {
                boolean deleted = calendarService.deleteEvent(eventToDelete.getId());
                if (deleted) {
                    System.out.println("\nEvent deleted successfully!");
                    System.out.println("Total events remaining: " + calendarService.getEventCount());
                } else {
                    System.out.println("\nFailed to delete event.");
                }
            } else {
                System.out.println("\nDeletion cancelled.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter a number.");
        }
    }

    private void displayEventsTable(List<Event> events) {
        if (events.isEmpty()) {
            System.out.println("  No events found.");
            return;
        }

        System.out.println("  Total: " + events.size() + " event(s)\n");
        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            System.out.println("  " + (i + 1) + ". " + event);
        }
    }

    private LocalDateTime promptForDateTime(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("cancel")) {
                throw new IllegalArgumentException("Operation cancelled");
            }

            try {
                return LocalDateTime.parse(input, DATETIME_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid format! Please use: MM-dd-yyyy HH:mm (e.g., 12-15-2025 14:30)");
                System.out.println("Or type 'cancel' to go back.\n");
            }
        }
    }

    private LocalDate promptForDate() {
        while (true) {
            System.out.print("Enter date (MM-dd-yyyy): ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("cancel")) {
                throw new IllegalArgumentException("Operation cancelled");
            }

            try {
                return LocalDate.parse(input, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid format! Please use: MM-dd-yyyy (e.g., 12-15-2025)");
                System.out.println("Or type 'cancel' to go back.\n");
            }
        }
    }

    private int promptForDuration() {
        while (true) {
            System.out.print("Enter duration in minutes: ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("cancel")) {
                throw new IllegalArgumentException("Operation cancelled");
            }

            try {
                int duration = Integer.parseInt(input);
                if (duration <= 0) {
                    System.out.println("Duration must be greater than 0!");
                    System.out.println("Or type 'cancel' to go back.\n");
                    continue;
                }
                return duration;
            } catch (NumberFormatException e) {
                System.out.println("Invalid number! Please enter a valid duration in minutes.");
                System.out.println("Or type 'cancel' to go back.\n");
            }
        }
    }

    private String formatDuration(long minutes) {
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

# Calendar Management System

A simple command-line application for managing calendar events and appointments. Built in Java.

## What It Does

This application helps you manage your daily schedule by:
- Creating events with titles and time slots
- Preventing double-booking (no overlapping events)
- Viewing your schedule for today or any specific day
- Finding available time slots for new appointments
- Booking available slots directly
- Deleting events you no longer need

## Features

### Basic Features
- Create events with title, start time, and end time
- Automatic overlap detection and prevention
- View all events for today
- View remaining events (upcoming only)
- View events for any specific date
- List all events across all dates
- Delete events

### Nice-to-Have Features
- **Smart slot finding**: Shows all available time slots, not just one
- **Direct booking**: Book slots directly from search results
- **Quick actions**: Chain operations without going back to menu
- **Input validation**: Helpful error messages with retry (no crashes)
- **Event summaries**: See your day's schedule after creating events

## Project Structure

```
src/
├── main/java/com/calendar/
│   ├── CalendarApp.java              # Main application
│   ├── model/                         # Data models
│   │   ├── Event.java
│   │   └── TimeSlot.java
│   ├── service/                       # Core logic
│   │   ├── CalendarServiceImpl.java
│   │   ├── EventStorage.java
│   │   ├── InMemoryEventStorage.java
│   │   ├── SlotFinder.java
│   │   └── StandardSlotFinder.java
│   ├── factory/
│   │   └── ServiceFactory.java
│   └── exception/                     # Custom exceptions
│       ├── EventOverlapException.java
│       ├── InvalidEventException.java
│       └── StorageException.java
└── test/java/com/calendar/           # Test files
```

## Approach and Design

### Overview
I built this as an in-memory event management system using Java. The main challenge was implementing overlap detection and smart time slot finding while keeping the interface simple.

### Design Decisions

**Interface-Based Architecture**

I separated storage and slot-finding into interfaces (EventStorage, SlotFinder) so I can swap implementations later. For example, could add file storage or database storage without changing the main service code.

```
CalendarApp (UI)
     ↓
CalendarServiceImpl (Logic)
     ↓              ↓
EventStorage    SlotFinder
     ↓              ↓
InMemoryStorage  StandardSlotFinder
```

**Overlap Detection Algorithm**

Events overlap if: `eventA.start < eventB.end && eventB.start < eventA.end`

This simple formula checks all possible overlap scenarios without complex if-statements.

**Slot Finding Approach**

Instead of just showing the first available slot, I show ALL available slots in 30-minute increments. The algorithm:
1. Checks gaps before the first event
2. Checks gaps between consecutive events
3. Checks gaps after the last event
4. Returns all slots that fit the requested duration

**Component Separation**
- Event: Data model with validation
- EventStorage: Handles save/load/delete
- CalendarServiceImpl: Business logic and overlap checking
- SlotFinder: Scheduling algorithm (9 AM - 6 PM working hours)
- ServiceFactory: Wires everything together

## Getting Started

### Requirements
- Java 11 or higher

### Running the Application

**Option 1: Command Line (No Build Tools Required)**

```bash
# Compile
javac -d out -sourcepath src/main/java src/main/java/com/calendar/CalendarApp.java

# Run
java -cp out com.calendar.CalendarApp
```

**Option 2: Using IntelliJ/Eclipse**
1. Open the project in your IDE
2. Find `src/main/java/com/calendar/CalendarApp.java`
3. Right-click → Run

**Option 3: Using Maven (Optional)**
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.calendar.CalendarApp"
```

## How to Use

When you run the app, you'll see a menu with these options:

```
1. Create New Event
2. List All Events for Today
3. List Remaining Events for Today
4. List Events for Specific Day
5. Find Next Available Slot
6. List All Events
7. Delete Event
8. Exit
```

### Example Session

```
Enter your choice (1-8): 1

--- Create New Event ---
Event Title: Morning Standup
Start Time (MM-dd-yyyy HH:mm): 12-15-2025 09:30
End Time   (MM-dd-yyyy HH:mm): 12-15-2025 10:00

Event created successfully!

Event Details:
  Title:    Morning Standup
  Date:     12-15-2025
  Time:     09:30 - 10:00
  Duration: 30 minutes

Quick Actions:
  1. Create another event
  2. View today's events
  3. Find available slots
  4. Back to main menu
```

## Error Handling

The app handles invalid input gracefully:

**Wrong Date Format:**
```
Start Time (MM-dd-yyyy HH:mm): 2025-12-15 10:00
Invalid format! Please use: MM-dd-yyyy HH:mm (e.g., 12-15-2025 14:30)
Or type 'cancel' to go back.

Start Time (MM-dd-yyyy HH:mm): 12-15-2025 10:00
```

**Overlapping Event:**
```
Event overlaps with existing event(s). Cannot add overlapping events.
Please choose a different time for your event.
```

**Empty Title:**
```
Event Title:
Title cannot be empty!
```

**Invalid Event Data:**
```
End time must be after start time
```

All errors are handled without crashing - you either get a retry prompt or return to the main menu.

## Test Data / Seed Data

The application starts with an **empty calendar**. When you first run it:

```
==================================================
              CALENDAR APPLICATION
==================================================
Total Events: 0
==================================================
```

**Suggested Test Flow:**

1. Create a few events for today:
   ```
   Morning Standup: 09:30 - 10:00
   Team Meeting: 10:00 - 11:00
   Lunch Break: 12:00 - 13:00
   ```

2. Try to create an overlapping event:
   ```
   Client Call: 10:30 - 11:30  (will fail - overlaps with Team Meeting)
   ```

3. Find available slots:
   ```
   Will show: 08:00-09:00, 11:00-12:00, 13:00-14:00, etc.
   ```

4. Book a slot directly from the search results

5. View your schedule and delete an event

Since events are stored in memory, they're lost when you exit the app.

## Running Tests

### Using IntelliJ IDEA

1. Right-click on `src/test/java` folder
2. Select **"Run 'All Tests'"**
3. View results in the Run panel (bottom of screen)
4. All tests should show green checkmarks

**Expected:** All 41 tests pass with 0 failures

### Using Eclipse

1. Right-click on `src/test/java` folder
2. Select **Run As → JUnit Test**
3. View results in JUnit panel
4. All tests should pass

### Using Maven (Optional)

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=EventTest
```

**Expected output:**
```
[INFO] Tests run: 41, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### What the Tests Verify

The test suite includes 41 focused test cases covering:

**Model Tests (21 tests):**
- Event creation and validation
- Overlap detection logic
- TimeSlot validation and duration calculations
- Edge cases (empty titles, invalid times)

**Service Tests (20 tests):**
- Event storage operations (save, find, delete, clear, sort)
- Slot finding algorithm with various scenarios
- Calendar service business logic
- Service factory dependency wiring

All tests use real implementations (no mocking) to ensure components work together correctly.

## Sample Test Scenarios

### Test 1: Basic Event Creation
```
Create event: "Lunch" from 12:00 to 13:00
Expected: Event created successfully
```

### Test 2: Overlap Prevention
```
Create event: "Meeting" from 10:00 to 11:00
Try to create: "Call" from 10:30 to 11:30
Expected: Error - events overlap
```

### Test 3: Finding Slots
```
Create events at 10:00-11:00 and 14:00-15:00
Find 1-hour slots
Expected: Shows slots at 09:00, 11:00, 12:00, 13:00, 15:00, etc.
```

### Test 4: View Remaining Events
```
Current time: 11:00
Events: 09:00-10:00 (past), 14:00-15:00 (future)
View remaining events
Expected: Only shows 14:00-15:00
```

## Technical Details

- **Language**: Java 11+
- **Build Tool**: Maven
- **Testing**: JUnit 5
- **Data Storage**: In-memory (no database required)
- **Working Hours**: 9 AM to 6 PM (configurable)

## Notes

- Events are stored in memory, so they're lost when you exit
- Time slots are suggested in 30-minute intervals
- Working hours default to 9 AM - 6 PM
- All events are automatically sorted by start time

## Future Ideas

If I had more time, I'd add:
- Build UI
- Integrate it with Calenders (Google Calender or Outlook etc)
- Save/load events from file
- Edit existing events
- Recurring events (daily, weekly)
- Search events by title
- Color coding for event types

## Built With

Java, Maven, JUnit.

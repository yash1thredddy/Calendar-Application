package com.calendar.gui.controller;

import com.calendar.gui.util.DataChangeListener;
import com.calendar.gui.util.DateTimeUtil;
import com.calendar.model.Event;
import com.calendar.service.CalendarServiceImpl;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

// Controller for Calendar Grid View - displays month grid with events
public class CalendarViewController implements DataChangeListener {
    private CalendarServiceImpl calendarService;
    private MainWindowController mainController;
    private YearMonth currentMonth;

    @FXML
    private GridPane calendarGrid;

    @FXML
    private Label monthYearLabel;

    @FXML
    public void initialize() {
        currentMonth = YearMonth.now();
    }

    public void initData(MainWindowController mainController, CalendarServiceImpl calendarService) {
        this.mainController = mainController;
        this.calendarService = calendarService;
        mainController.registerListener(this);
        buildCalendar();
    }

    @FXML
    private void handlePreviousMonth() {
        currentMonth = currentMonth.minusMonths(1);
        buildCalendar();
    }

    @FXML
    private void handleNextMonth() {
        currentMonth = currentMonth.plusMonths(1);
        buildCalendar();
    }

    @FXML
    private void handleToday() {
        currentMonth = YearMonth.now();
        buildCalendar();
    }

    private void buildCalendar() {
        calendarGrid.getChildren().clear();

        // Update month/year label
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        monthYearLabel.setText(currentMonth.format(formatter));

        // Add day headers (Sun, Mon, Tue, etc.)
        String[] dayNames = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (int col = 0; col < 7; col++) {
            Label dayHeader = new Label(dayNames[col]);
            dayHeader.setStyle("-fx-font-weight: bold; -fx-padding: 5;");
            dayHeader.setMaxWidth(Double.MAX_VALUE);
            dayHeader.setAlignment(Pos.CENTER);
            calendarGrid.add(dayHeader, col, 0);
        }

        // Calculate start date (Monday of first week)
        LocalDate firstOfMonth = currentMonth.atDay(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue(); // 1 = Monday, 7 = Sunday
        LocalDate startDate = firstOfMonth.minusDays(dayOfWeek - 1);

        // Build 6 rows of 7 days
        LocalDate today = LocalDate.now();
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                LocalDate date = startDate.plusDays(row * 7 + col);
                boolean inCurrentMonth = date.getMonth() == currentMonth.getMonth();
                boolean isToday = date.equals(today);

                VBox dayCell = createDayCell(date, inCurrentMonth, isToday);
                GridPane.setVgrow(dayCell, Priority.ALWAYS);
                GridPane.setHgrow(dayCell, Priority.ALWAYS);
                calendarGrid.add(dayCell, col, row + 1); // +1 for header row
            }
        }
    }

    private VBox createDayCell(LocalDate date, boolean inCurrentMonth, boolean isToday) {
        VBox cell = new VBox(5);
        cell.setStyle(getCellStyle(inCurrentMonth, isToday));
        cell.setAlignment(Pos.TOP_LEFT);
        cell.setMinHeight(80);

        // Day number label
        Label dayLabel = new Label(String.valueOf(date.getDayOfMonth()));
        dayLabel.setStyle("-fx-font-weight: bold; -fx-padding: 3;");
        if (isToday) {
            dayLabel.setStyle("-fx-font-weight: bold; -fx-padding: 3; -fx-text-fill: #2196F3;");
        }

        // Get events for this date
        List<Event> dayEvents = calendarService.getAllEvents().stream()
                .filter(e -> e.getStartTime().toLocalDate().equals(date))
                .sorted()
                .limit(5) // Show max 5 events
                .collect(Collectors.toList());

        // Event list
        ListView<Event> eventList = new ListView<>();
        eventList.getItems().addAll(dayEvents);
        eventList.setMaxHeight(60);
        eventList.setStyle("-fx-background-color: transparent;");

        // Custom cell factory for compact event display
        eventList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Event event, boolean empty) {
                super.updateItem(event, empty);
                if (empty || event == null) {
                    setText(null);
                    setStyle("");
                } else {
                    String timeText = DateTimeUtil.formatTime(event.getStartTime());
                    setText(timeText + " " + event.getTitle());
                    setStyle("-fx-font-size: 10px; -fx-padding: 1; -fx-background-color: #e3f2fd; " +
                            "-fx-background-radius: 3; -fx-border-radius: 3;");
                }
            }
        });

        // Click handler to create event on this date
        cell.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double-click
                mainController.openCreateEventDialog(
                        date.atTime(9, 0), // Default 9:00 AM
                        date.atTime(10, 0)  // Default 10:00 AM
                );
            }
        });

        cell.getChildren().addAll(dayLabel, eventList);
        return cell;
    }

    private String getCellStyle(boolean inCurrentMonth, boolean isToday) {
        StringBuilder style = new StringBuilder();
        style.append("-fx-border-color: #dddddd; -fx-border-width: 1; -fx-padding: 3;");

        if (isToday) {
            style.append(" -fx-background-color: #e3f2fd;");
        } else if (!inCurrentMonth) {
            style.append(" -fx-background-color: #f5f5f5; -fx-opacity: 0.6;");
        } else {
            style.append(" -fx-background-color: white;");
        }

        return style.toString();
    }

    @Override
    public void onDataChanged() {
        buildCalendar();
    }
}

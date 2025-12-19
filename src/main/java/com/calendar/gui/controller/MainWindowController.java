package com.calendar.gui.controller;

import com.calendar.exception.EventOverlapException;
import com.calendar.gui.util.AlertUtil;
import com.calendar.gui.util.DataChangeListener;
import com.calendar.model.Event;
import com.calendar.service.CalendarServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Main window controller - manages tabs, toolbar, and coordinates all child controllers
public class MainWindowController {
    private final CalendarServiceImpl calendarService;
    private final List<DataChangeListener> listeners = new ArrayList<>();

    @FXML
    private TabPane tabPane;

    @FXML
    private Label eventCountLabel;

    @FXML
    private Label statusLabel;

    public MainWindowController(CalendarServiceImpl calendarService) {
        this.calendarService = calendarService;
    }

    @FXML
    public void initialize() {
        try {
            loadTabs();
            updateEventCount();
            setStatus("Ready");
        } catch (Exception e) {
            System.err.println("Failed to load views: " + e.getMessage());
            AlertUtil.showError("Initialization Error", "Failed to load views: " + e.getMessage());
        }
    }

    private void loadTabs() throws Exception {
        // Load Event List View
        javafx.fxml.FXMLLoader eventListLoader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/com/calendar/gui/view/EventListView.fxml"));
        javafx.scene.layout.BorderPane eventListView = eventListLoader.load();
        EventListController eventListController = eventListLoader.getController();
        eventListController.initData(this, calendarService);
        javafx.scene.control.Tab eventListTab = new javafx.scene.control.Tab("Events", eventListView);
        tabPane.getTabs().add(eventListTab);

        // Load Calendar View
        javafx.fxml.FXMLLoader calendarLoader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/com/calendar/gui/view/CalendarView.fxml"));
        javafx.scene.layout.VBox calendarView = calendarLoader.load();
        CalendarViewController calendarController = calendarLoader.getController();
        calendarController.initData(this, calendarService);
        javafx.scene.control.Tab calendarTab = new javafx.scene.control.Tab("Calendar", calendarView);
        tabPane.getTabs().add(0, calendarTab); // Add as first tab

        // Load Available Slots View
        javafx.fxml.FXMLLoader slotsLoader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/com/calendar/gui/view/AvailableSlotsView.fxml"));
        javafx.scene.layout.BorderPane slotsView = slotsLoader.load();
        AvailableSlotsController slotsController = slotsLoader.getController();
        slotsController.initData(this, calendarService);
        javafx.scene.control.Tab slotsTab = new javafx.scene.control.Tab("Available Slots", slotsView);
        tabPane.getTabs().add(slotsTab);
    }

    // Register a listener for data changes
    public void registerListener(DataChangeListener listener) {
        listeners.add(listener);
    }

    // Notify all listeners that data has changed
    public void notifyDataChanged() {
        for (DataChangeListener listener : listeners) {
            listener.onDataChanged();
        }
        updateEventCount();
    }

    @FXML
    private void handleCreateEvent() {
        CreateEventController controller = new CreateEventController(null, null);
        Optional<Event> result = controller.showDialog();

        result.ifPresent(event -> {
            try {
                calendarService.addEvent(event);
                AlertUtil.showSuccess("Event created successfully!");
                notifyDataChanged();
                setStatus("Event created: " + event.getTitle());
            } catch (EventOverlapException e) {
                AlertUtil.showError("Event Overlap", e.getMessage());
            } catch (Exception e) {
                AlertUtil.showError("Error", "Failed to create event: " + e.getMessage());
            }
        });
    }

    // Public method to open create event dialog with pre-filled date/times
    public void openCreateEventDialog(LocalDateTime startTime, LocalDateTime endTime) {
        CreateEventController controller = new CreateEventController(startTime, endTime);
        Optional<Event> result = controller.showDialog();

        result.ifPresent(event -> {
            try {
                calendarService.addEvent(event);
                AlertUtil.showSuccess("Event created successfully!");
                notifyDataChanged();
                setStatus("Event created: " + event.getTitle());
            } catch (EventOverlapException e) {
                AlertUtil.showError("Event Overlap", e.getMessage());
            } catch (Exception e) {
                AlertUtil.showError("Error", "Failed to create event: " + e.getMessage());
            }
        });
    }

    private void updateEventCount() {
        int count = calendarService.getEventCount();
        eventCountLabel.setText("Total Events: " + count);
    }

    public void setStatus(String message) {
        statusLabel.setText(message);
    }
}

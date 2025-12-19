package com.calendar.gui.controller;

import com.calendar.gui.model.EventTableModel;
import com.calendar.gui.util.AlertUtil;
import com.calendar.gui.util.DataChangeListener;
import com.calendar.model.Event;
import com.calendar.service.CalendarServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

// Controller for Event List View - displays all events in a table
public class EventListController implements DataChangeListener {
    private CalendarServiceImpl calendarService;
    private MainWindowController mainController;

    @FXML
    private TableView<EventTableModel> eventsTable;

    @FXML
    private TableColumn<EventTableModel, String> titleColumn;

    @FXML
    private TableColumn<EventTableModel, String> startDateColumn;

    @FXML
    private TableColumn<EventTableModel, String> startTimeColumn;

    @FXML
    private TableColumn<EventTableModel, String> endTimeColumn;

    @FXML
    private TableColumn<EventTableModel, String> durationColumn;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Label eventCountLabel;

    private final ObservableList<EventTableModel> eventData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // This will be called later after service is injected
    }

    // Called by MainWindow to inject dependencies
    public void initData(MainWindowController mainController, CalendarServiceImpl calendarService) {
        this.mainController = mainController;
        this.calendarService = calendarService;

        // Register as listener for data changes
        mainController.registerListener(this);

        // Set up table columns
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        startDateColumn.setCellValueFactory(cellData -> cellData.getValue().startDateProperty());
        startTimeColumn.setCellValueFactory(cellData -> cellData.getValue().startTimeProperty());
        endTimeColumn.setCellValueFactory(cellData -> cellData.getValue().endTimeProperty());
        durationColumn.setCellValueFactory(cellData -> cellData.getValue().durationProperty());

        // Bind table to data
        eventsTable.setItems(eventData);

        // Add date picker listener
        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                filterByDate(newVal);
            }
        });

        // Load initial data
        loadAllEvents();
    }

    @FXML
    private void handleDelete() {
        EventTableModel selected = eventsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("No Selection", "Please select an event to delete.");
            return;
        }

        Event event = selected.getEvent();
        boolean confirmed = AlertUtil.showConfirmation(
                "Confirm Deletion",
                "Are you sure you want to delete the event:\n\"" + event.getTitle() + "\"?"
        );

        if (confirmed) {
            boolean deleted = calendarService.deleteEvent(event.getId());
            if (deleted) {
                AlertUtil.showSuccess("Event deleted successfully!");
                mainController.notifyDataChanged();
                mainController.setStatus("Event deleted: " + event.getTitle());
            } else {
                AlertUtil.showError("Error", "Failed to delete event.");
            }
        }
    }

    @FXML
    private void handleRefresh() {
        if (datePicker.getValue() != null) {
            filterByDate(datePicker.getValue());
        } else {
            loadAllEvents();
        }
        mainController.setStatus("Event list refreshed");
    }

    @FXML
    private void handleClearFilter() {
        datePicker.setValue(null);
        loadAllEvents();
        mainController.setStatus("Filter cleared");
    }

    private void loadAllEvents() {
        List<Event> events = calendarService.getAllEvents();
        updateTable(events);
    }

    private void filterByDate(LocalDate date) {
        List<Event> events = calendarService.listEventsForDate(date.atStartOfDay());
        updateTable(events);
    }

    private void updateTable(List<Event> events) {
        eventData.clear();
        List<EventTableModel> models = events.stream()
                .map(EventTableModel::new)
                .collect(Collectors.toList());
        eventData.addAll(models);
        eventCountLabel.setText("Showing " + events.size() + " event(s)");
    }

    @Override
    public void onDataChanged() {
        handleRefresh();
    }
}

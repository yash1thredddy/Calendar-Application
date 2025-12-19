package com.calendar.gui.controller;

import com.calendar.gui.util.AlertUtil;
import com.calendar.gui.util.DataChangeListener;
import com.calendar.gui.util.DateTimeUtil;
import com.calendar.model.TimeSlot;
import com.calendar.service.CalendarServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.List;

// Controller for Available Slots View - finds and displays available time slots
public class AvailableSlotsController implements DataChangeListener {
    private CalendarServiceImpl calendarService;
    private MainWindowController mainController;

    @FXML
    private ComboBox<String> durationComboBox;

    @FXML
    private TextField customDurationField;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ListView<TimeSlot> slotsListView;

    @FXML
    private Button bookSlotButton;

    @FXML
    private Label statusLabel;

    private final ObservableList<TimeSlot> slotsData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Initialize duration combo box
        durationComboBox.setItems(FXCollections.observableArrayList(
                "30 minutes",
                "1 hour",
                "2 hours",
                "Custom"
        ));
        durationComboBox.setValue("1 hour");

        // Set default date to today
        datePicker.setValue(LocalDate.now());

        // Enable/disable custom duration field based on selection
        durationComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean isCustom = "Custom".equals(newVal);
            customDurationField.setDisable(!isCustom);
            if (!isCustom) {
                customDurationField.clear();
            }
        });

        // Bind list view to data
        slotsListView.setItems(slotsData);

        // Custom cell factory for displaying time slots
        slotsListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(TimeSlot slot, boolean empty) {
                super.updateItem(slot, empty);
                if (empty || slot == null) {
                    setText(null);
                } else {
                    String text = String.format("%s - %s (%d minutes)",
                            DateTimeUtil.formatTime(slot.getStartTime()),
                            DateTimeUtil.formatTime(slot.getEndTime()),
                            slot.getDurationInMinutes());
                    setText(text);
                }
            }
        });

        // Enable book button only when a slot is selected
        slotsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) ->
            bookSlotButton.setDisable(newVal == null)
        );
    }

    public void initData(MainWindowController mainController, CalendarServiceImpl calendarService) {
        this.mainController = mainController;
        this.calendarService = calendarService;
        mainController.registerListener(this);
    }

    @FXML
    private void handleFindSlots() {
        try {
            int duration = getDurationFromInput();
            LocalDate date = datePicker.getValue();

            if (date == null) {
                AlertUtil.showWarning("No Date Selected", "Please select a date to search for available slots.");
                return;
            }

            List<TimeSlot> slots = calendarService.findAllAvailableSlots(duration, date.atStartOfDay());

            slotsData.clear();
            slotsData.addAll(slots);

            if (slots.isEmpty()) {
                statusLabel.setText("No available slots found for " + duration + " minutes on " +
                        date.format(DateTimeUtil.DATE_FORMATTER));
            } else {
                statusLabel.setText("Found " + slots.size() + " available slot(s)");
            }

        } catch (NumberFormatException e) {
            AlertUtil.showError("Invalid Duration", "Please enter a valid number for custom duration.");
        } catch (Exception e) {
            AlertUtil.showError("Error", "Failed to find slots: " + e.getMessage());
        }
    }

    @FXML
    private void handleBookSlot() {
        TimeSlot selectedSlot = slotsListView.getSelectionModel().getSelectedItem();
        if (selectedSlot == null) {
            AlertUtil.showWarning("No Selection", "Please select a time slot to book.");
            return;
        }

        // Open create event dialog with pre-filled times
        mainController.openCreateEventDialog(
                selectedSlot.getStartTime(),
                selectedSlot.getEndTime()
        );
    }

    private int getDurationFromInput() {
        String selected = durationComboBox.getValue();
        if (selected == null) {
            return 60; // default
        }

        switch (selected) {
            case "30 minutes":
                return 30;
            case "1 hour":
                return 60;
            case "2 hours":
                return 120;
            case "Custom":
                String customText = customDurationField.getText().trim();
                if (customText.isEmpty()) {
                    throw new NumberFormatException("Custom duration is empty");
                }
                int customDuration = Integer.parseInt(customText);
                if (customDuration <= 0) {
                    throw new NumberFormatException("Duration must be greater than 0");
                }
                return customDuration;
            default:
                return 60;
        }
    }

    @Override
    public void onDataChanged() {
        // Refresh slots if we had a search
        if (!slotsData.isEmpty() && datePicker.getValue() != null) {
            handleFindSlots();
        }
    }
}

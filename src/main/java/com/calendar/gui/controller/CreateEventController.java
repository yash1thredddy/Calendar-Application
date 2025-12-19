package com.calendar.gui.controller;

import com.calendar.exception.InvalidEventException;
import com.calendar.model.Event;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

// Controller for Create Event dialog - builds UI programmatically
public class CreateEventController {
    private final LocalDateTime prefilledStart;
    private final LocalDateTime prefilledEnd;

    public CreateEventController(LocalDateTime prefilledStart,
                                   LocalDateTime prefilledEnd) {
        this.prefilledStart = prefilledStart;
        this.prefilledEnd = prefilledEnd;
    }

    public Optional<Event> showDialog() {
        Dialog<Event> dialog = new Dialog<>();
        dialog.setTitle("Create New Event");
        dialog.setHeaderText("Enter event details");

        // Set button types
        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        // Create form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField titleField = new TextField();
        titleField.setPromptText("Event title");

        DatePicker datePicker = new DatePicker();
        datePicker.setValue(prefilledStart != null ? prefilledStart.toLocalDate() : LocalDate.now());

        // Time fields
        TextField startTimeField = new TextField();
        startTimeField.setPromptText("HH:mm (e.g., 09:00)");
        if (prefilledStart != null) {
            startTimeField.setText(String.format("%02d:%02d",
                    prefilledStart.getHour(), prefilledStart.getMinute()));
        }

        TextField endTimeField = new TextField();
        endTimeField.setPromptText("HH:mm (e.g., 10:00)");
        if (prefilledEnd != null) {
            endTimeField.setText(String.format("%02d:%02d",
                    prefilledEnd.getHour(), prefilledEnd.getMinute()));
        }

        Label durationLabel = new Label("Duration: --");

        // Add fields to grid
        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Date:"), 0, 1);
        grid.add(datePicker, 1, 1);
        grid.add(new Label("Start Time:"), 0, 2);
        grid.add(startTimeField, 1, 2);
        grid.add(new Label("End Time:"), 0, 3);
        grid.add(endTimeField, 1, 3);
        grid.add(new Label(""), 0, 4);
        grid.add(durationLabel, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // Update duration label when times change
        Runnable updateDuration = () -> {
            try {
                LocalTime start = parseTime(startTimeField.getText());
                LocalTime end = parseTime(endTimeField.getText());
                long minutes = java.time.Duration.between(start, end).toMinutes();
                if (minutes > 0) {
                    durationLabel.setText("Duration: " + minutes + " minutes");
                } else {
                    durationLabel.setText("Duration: Invalid");
                }
            } catch (Exception e) {
                durationLabel.setText("Duration: --");
            }
        };

        startTimeField.textProperty().addListener((obs, old, newVal) -> updateDuration.run());
        endTimeField.textProperty().addListener((obs, old, newVal) -> updateDuration.run());

        // Initial duration calculation
        updateDuration.run();

        // Request focus on title field
        titleField.requestFocus();

        // Convert result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                try {
                    String title = titleField.getText().trim();
                    LocalDate date = datePicker.getValue();
                    LocalTime startTime = parseTime(startTimeField.getText());
                    LocalTime endTime = parseTime(endTimeField.getText());

                    LocalDateTime start = LocalDateTime.of(date, startTime);
                    LocalDateTime end = LocalDateTime.of(date, endTime);

                    return Event.create(title, start, end);
                } catch (InvalidEventException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Event");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                    return null;
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Input");
                    alert.setContentText("Please check all fields: " + e.getMessage());
                    alert.showAndWait();
                    return null;
                }
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private LocalTime parseTime(String timeStr) {
        String[] parts = timeStr.trim().split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Time must be in HH:mm format");
        }
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        return LocalTime.of(hour, minute);
    }
}

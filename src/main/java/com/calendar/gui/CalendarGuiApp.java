package com.calendar.gui;

import com.calendar.factory.ServiceFactory;
import com.calendar.gui.controller.MainWindowController;
import com.calendar.service.CalendarServiceImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

// Main JavaFX Application class - entry point for the GUI
public class CalendarGuiApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Create single shared calendar service instance
            CalendarServiceImpl calendarService = ServiceFactory.createService();

            // Load Main Window FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/calendar/gui/view/MainWindow.fxml"));

            // Set controller factory to inject the calendar service
            loader.setControllerFactory(controllerClass -> {
                if (controllerClass == MainWindowController.class) {
                    return new MainWindowController(calendarService);
                }
                try {
                    return controllerClass.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException("Failed to create controller: " + controllerClass, e);
                }
            });

            Parent root = loader.load();

            // Create scene and set up stage
            Scene scene = new Scene(root, 1000, 700);

            // Apply CSS stylesheet
            var cssResource = getClass().getResource("/com/calendar/gui/css/calendar-style.css");
            if (cssResource != null) {
                scene.getStylesheets().add(cssResource.toExternalForm());
            }

            primaryStage.setTitle("Calendar Application");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(600);
            primaryStage.show();

        } catch (Exception e) {
            System.err.println("Failed to start Calendar GUI: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

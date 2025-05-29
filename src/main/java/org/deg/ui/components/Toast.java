package org.deg.ui.components;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Toast {
    public static void show(Stage stage, String message, int durationMillis, ToastMode toastMode) {
        Label toastLabel = new Label(message);
        toastLabel.getStyleClass().add("toast");
        switch (toastMode) {
            case ERROR:
                toastLabel.getStyleClass().add("error");
                break;
            case WARNING:
                toastLabel.getStyleClass().add("warning");
                break;
            case INFO:
                toastLabel.getStyleClass().add("info");
                break;
            case SUCCESS:
                toastLabel.getStyleClass().add("success");
                break;
        }
        toastLabel.setOpacity(0);

        Pane root = (Pane) stage.getScene().getRoot();
        StackPane.setMargin(toastLabel, new Insets(20, 20, 0, 0));
        root.getChildren().add(toastLabel);
        StackPane.setAlignment(toastLabel, Pos.TOP_RIGHT);

        // Fade in
        FadeTransition fadeIn = getFadeTransition(durationMillis, toastLabel, root);
        fadeIn.play();
    }

    private static FadeTransition getFadeTransition(int durationMillis, Label toastLabel, Pane root) {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), toastLabel);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(0.9);
        fadeIn.setOnFinished(e -> {
            // Fade out after delay
            PauseTransition delay = new PauseTransition(Duration.millis(durationMillis));
            delay.setOnFinished(ev -> {
                FadeTransition fadeOut = new FadeTransition(Duration.millis(300), toastLabel);
                fadeOut.setFromValue(0.9);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(event -> root.getChildren().remove(toastLabel));
                fadeOut.play();
            });
            delay.play();
        });
        return fadeIn;
    }
}

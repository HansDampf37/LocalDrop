package org.deg.ui.components;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class NavButton extends Button {
    private final StackPane iconWrapper;

    public NavButton(String text, ImageView icon) {
        super(" " + text);

        if (icon != null) {
            icon.setFitWidth(16);
            icon.setFitHeight(16);

            iconWrapper = new StackPane(icon);
            iconWrapper.getStyleClass().add("icon-wrapper");

            setGraphic(iconWrapper);
            setContentDisplay(ContentDisplay.LEFT);
        } else {
            iconWrapper = null;
        }

        setAlignment(Pos.CENTER_LEFT);
        getStyleClass().add("nav-button");
    }

    public void activate() {
        if (iconWrapper != null && !iconWrapper.getStyleClass().contains("active")) {
            iconWrapper.getStyleClass().add("active");
        }
    }

    public void deactivate() {
        if (iconWrapper != null) {
            iconWrapper.getStyleClass().remove("active");
        }
    }
}

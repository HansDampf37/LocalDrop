package org.deg.ui.components;

import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.ImageView;

public class NavButton extends Button {
    public NavButton(String text, ImageView icon) {
        super(text);
        getStyleClass().add("nav-button");
        if (icon != null) {
            icon.setFitWidth(16);
            icon.setFitHeight(16);
            setGraphic(icon);
            setContentDisplay(ContentDisplay.LEFT);
        }
    }
}

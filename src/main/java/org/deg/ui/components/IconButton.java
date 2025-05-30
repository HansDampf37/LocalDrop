package org.deg.ui.components;

import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.ImageView;

public class IconButton extends Button {
    public IconButton(String text, ImageView icon) {
        super(text);

        if (icon != null) {
            icon.setFitWidth(40);
            icon.setFitHeight(40);

            setGraphic(icon);
            setContentDisplay(ContentDisplay.TOP);
        }
        getStyleClass().add("btn");
    }
}

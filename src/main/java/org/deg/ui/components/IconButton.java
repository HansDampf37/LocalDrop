package org.deg.ui.components;

import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.ImageView;

public class IconButton extends Button {
    public IconButton(String text, ImageView icon, int width, int height) {
        super(text);

        if (icon != null) {
            icon.setFitWidth(width);
            icon.setFitHeight(height);

            setGraphic(icon);
            if (text != null) {
                setContentDisplay(ContentDisplay.TOP);
            } else {
                setContentDisplay(ContentDisplay.CENTER);
            }
        }
        getStyleClass().add("btn");
    }

    public IconButton(ImageView icon, int width, int height) {
        this(null, icon, width, height);
    }

    public IconButton(ImageView icon) {
        this(icon, 20, 20);
    }

    public IconButton(String text, ImageView icon) {
        this(text, icon, 40, 40);
    }
}

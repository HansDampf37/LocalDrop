package org.deg.ui.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.deg.ui.components.TextFieldWithName;

import static org.deg.backend.UserConfigurations.DEFAULT_SAFE_PATH;
import static org.deg.backend.UserConfigurations.USERNAME;

public class SettingsView extends Pane {
    public SettingsView() {
        VBox vbox = new VBox(15);
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10));
        vbox.setAlignment(Pos.TOP_CENTER);

        TextFieldWithName nameInput = new TextFieldWithName("Your Name");
        nameInput.inputField.setText(USERNAME);

        TextFieldWithName savePathInput = new TextFieldWithName("Save files to:");
        savePathInput.inputField.setText(DEFAULT_SAFE_PATH.getAbsolutePath());

        vbox.getChildren().addAll(nameInput, savePathInput);

        getChildren().add(vbox);
    }
}

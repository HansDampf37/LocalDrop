package org.deg.ui.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.deg.ui.components.TextFieldWithName;
import org.deg.ui.components.TextFieldWithNameAndFileChooser;
import org.deg.ui.components.Toast;
import org.deg.ui.components.ToastMode;

import java.io.File;

import static org.deg.backend.UserConfigurations.*;

public class SettingsView extends Pane {
    public SettingsView() {
        VBox vbox = new VBox(15);
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10));
        vbox.setAlignment(Pos.TOP_CENTER);

        TextFieldWithName nameInput = new TextFieldWithName("Your Name");
        nameInput.inputField.setText(USERNAME);

        TextFieldWithName savePathInput = new TextFieldWithNameAndFileChooser("Save files to:", true);
        savePathInput.inputField.setText(DEFAULT_SAFE_PATH.getAbsolutePath());

        HBox buttonContainer = new HBox(15);
        Button revertButton = new Button("Revert Changes");
        Button saveButton = new Button("Save");
        revertButton.setOnAction(event -> {
            nameInput.setText(USERNAME);
            savePathInput.setText(DEFAULT_SAFE_PATH.getAbsolutePath());
            Toast.show((Stage) getScene().getWindow(), "Changes have been reverted", 3000, ToastMode.SUCCESS);
        });
        saveButton.setOnAction(event -> {
            boolean success = true;
            if (!usernameValid(nameInput.getText())) {
                nameInput.inputField.getStyleClass().add("wrongInput");
                success = false;
            } else {
                nameInput.inputField.getStyleClass().remove("wrongInput");
            }
            if (!defaultSavePathValid(savePathInput.getText())) {
                savePathInput.inputField.getStyleClass().add("wrongInput");
                success = false;
            } else {
                savePathInput.inputField.getStyleClass().remove("wrongInput");
            }
            if (success) {
                USERNAME = nameInput.getText().trim();
                DEFAULT_SAFE_PATH = new File(savePathInput.getText().trim());
                Toast.show((Stage) getScene().getWindow(), "Changes have been saved. They will be visible after a restart.", 3000, ToastMode.SUCCESS);
                saveConfigurations();
            }
        });

        buttonContainer.getChildren().addAll(revertButton, saveButton);
        vbox.getChildren().addAll(nameInput, savePathInput, buttonContainer);
        getChildren().add(vbox);
    }

    private boolean usernameValid(String username) {
        return username != null && !username.isEmpty() && username.matches("[a-zA-Z][a-zA-Z0-9_äöü ]*");
    }

    private boolean defaultSavePathValid(String defaultSavePath) {
        try {
            File file = new File(defaultSavePath);
            return file.exists();
        } catch (Exception e) {
            return false;
        }
    }
}

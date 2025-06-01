package org.deg.ui.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.deg.ui.components.*;

import java.io.File;
import java.util.Objects;

import static org.deg.backend.UserConfigurations.DEFAULT_SAFE_PATH;
import static org.deg.backend.UserConfigurations.saveConfigurations;

public class SettingsView extends VBox {

    private final HBox buttonContainer = new HBox(15);;

    public SettingsView() {
        super(15);
        setSpacing(10);
        setPadding(new Insets(10));
        setAlignment(Pos.TOP_CENTER);

        TextFieldWithName savePathInput = new TextFieldWithNameAndFileChooser("Save files to:", true);
        savePathInput.inputField.setText(DEFAULT_SAFE_PATH.getAbsolutePath());
        savePathInput.inputField.textProperty().addListener(event -> {
            buttonContainer.setVisible(true);
        });

        buttonContainer.setVisible(false);
        buttonContainer.setAlignment(Pos.CENTER_RIGHT);
        ImageView reloadIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/icons/revert.png")).toExternalForm()));
        IconButton revertButton = new IconButton("Revert Changes", reloadIcon, 20, 20);
        revertButton.setContentDisplay(ContentDisplay.LEFT);

        ImageView saveIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/icons/save.png")).toExternalForm()));
        IconButton saveButton = new IconButton("Save", saveIcon, 20, 20);
        saveButton.setContentDisplay(ContentDisplay.LEFT);
        revertButton.setOnAction(event -> {
            savePathInput.setText(DEFAULT_SAFE_PATH.getAbsolutePath());
            buttonContainer.setVisible(false);
            Toast.show((Stage) getScene().getWindow(), "Changes have been reverted", 3000, ToastMode.SUCCESS);
        });
        saveButton.setOnAction(event -> {
            boolean success = true;
            if (!defaultSavePathValid(savePathInput.getText())) {
                savePathInput.inputField.getStyleClass().add("wrongInput");
                success = false;
            } else {
                savePathInput.inputField.getStyleClass().remove("wrongInput");
            }
            if (success) {
                DEFAULT_SAFE_PATH = new File(savePathInput.getText().trim());
                Toast.show((Stage) getScene().getWindow(), "Changes have been saved. They will be visible after a restart.", 3000, ToastMode.SUCCESS);
                saveConfigurations();
                buttonContainer.setVisible(false);
            }
        });
        VBox gap = new VBox();
        VBox.setVgrow(gap, Priority.ALWAYS);
        buttonContainer.getChildren().addAll(revertButton, saveButton);
        getChildren().addAll(savePathInput, gap, buttonContainer);
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

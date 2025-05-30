package org.deg.ui.components;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Objects;

public class TextFieldWithNameAndFileChooser extends TextFieldWithName {
    public File selectedFile = null;

    public TextFieldWithNameAndFileChooser(String name, boolean choseDirectories) {
        super(name);
        HBox inputBox = new HBox(5);
        inputBox.setAlignment(Pos.CENTER);
        getChildren().clear();

        inputField.setDisable(true);
        ImageView fileIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/icons/file-explorer.png")).toExternalForm()));
        IconButton chooseSaveToFolder = new IconButton(fileIcon);
        chooseSaveToFolder.setOnAction(e -> {
            if (choseDirectories) {
                DirectoryChooser directoryChooser = new DirectoryChooser();
                directoryChooser.setTitle(name);
                File selectedDir = directoryChooser.showDialog(chooseSaveToFolder.getScene().getWindow());
                if (selectedDir != null) {
                    selectedFile = selectedDir;
                    inputField.setText(selectedFile.getAbsolutePath());
                }
            } else {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle(name);
                File selectedDir = fileChooser.showOpenDialog(chooseSaveToFolder.getScene().getWindow());
                if (selectedDir != null) {
                    selectedFile = selectedDir;
                }
            }

        });
        inputBox.getChildren().addAll(inputField, chooseSaveToFolder);
        getChildren().addAll(new Label(name), inputBox);
    }
}
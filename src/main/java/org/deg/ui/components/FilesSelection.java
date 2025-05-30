package org.deg.ui.components;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.deg.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class FilesSelection extends VBox {
    public final ObservableList<File> filesToSend = FXCollections.observableArrayList();

    public FilesSelection() {
        super(15);
        HBox dataTitleBar = getDataTitleBar();

        HBox innerBox = new HBox(15);
        ListView<File> fileList = getFileList();
        VBox fileButtons = new VBox(10);
        fileButtons.setAlignment(Pos.CENTER);
        ImageView fileIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/icons/document.png")).toExternalForm()));
        ImageView folderIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/icons/folder.png")).toExternalForm()));
        ImageView clearIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/icons/clear.png")).toExternalForm()));
        Button btnAddFile = new IconButton("Add File", fileIcon);
        Button btnAddFolder = new IconButton("Add Folder", folderIcon);
        Button btnClear = new IconButton("Clear Selection", clearIcon);
        btnClear.disableProperty().bind(Bindings.isEmpty(filesToSend));
        btnAddFolder.setPrefWidth(130);
        btnAddFile.setPrefWidth(130);
        btnClear.setPrefWidth(130);
        btnAddFile.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select File(s)");
            List<File> selectedFiles = fileChooser.showOpenMultipleDialog(btnAddFile.getScene().getWindow());
            if (selectedFiles != null) {
                filesToSend.addAll(selectedFiles);
            }
        });
        btnAddFolder.setOnAction(e -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select Folder");
            File selectedDir = directoryChooser.showDialog(btnAddFolder.getScene().getWindow());
            if (selectedDir != null) {
                filesToSend.add(selectedDir);
            }
        });
        btnClear.setOnAction(e -> filesToSend.clear());

        fileButtons.getChildren().addAll(btnAddFile, btnAddFolder, btnClear);
        innerBox.getChildren().addAll(fileList, fileButtons);
        this.getChildren().addAll(dataTitleBar, innerBox);
    }

    private HBox getDataTitleBar() {
        HBox dataTitleBar = new HBox();
        dataTitleBar.setAlignment(Pos.CENTER);

        Label dataLabel = new Label("Data");
        dataLabel.getStyleClass().add("h1");

        HBox gap = new HBox();
        HBox.setHgrow(gap, Priority.ALWAYS);

        Label infoLabel = new Label();
        StringBinding formattedSizeBinding = Bindings.createStringBinding(
                () -> {
                    if (!filesToSend.isEmpty()) {
                        long bytesInFiles = filesToSend.stream().filter(File::isFile).mapToLong(File::length).sum();
                        long bytesInDirs = filesToSend.stream().filter(File::isDirectory).mapToLong(dir -> {
                            try {
                                return Utils.getDirSize(dir);
                            } catch (IOException e) {
                                return 0L;
                            }
                        }).sum();
                        long totalBytes = bytesInFiles + bytesInDirs;
                        return "Total: " + filesToSend.size() + " files, " + Utils.bytesToReadableString(totalBytes);
                    } else {
                        return "";
                    }

                },
                filesToSend
        );
        infoLabel.textProperty().bind(formattedSizeBinding);
        dataTitleBar.getChildren().addAll(dataLabel, gap, infoLabel);
        return dataTitleBar;
    }

    private ListView<File> getFileList() {
        ListView<File> fileList = new ListView<>();
        fileList.setItems(filesToSend);
        fileList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(File item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty && item != null) {
                    long fileSize = 0;
                    try {
                        fileSize = item.isFile() ? item.length() : Utils.getDirSize(item);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    FileCell fileCell = new FileCell(item.getName(), fileSize);
                    fileCell.prefWidthProperty().bind(lv.widthProperty().subtract(20));
                    setGraphic(fileCell);
                    setText(null);
                } else {
                    setGraphic(null);
                    setText(null);
                }
            }
        });
        HBox.setHgrow(fileList, Priority.ALWAYS);
        fileList.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE) {
                File selectedFile = fileList.getSelectionModel().getSelectedItem();
                if (selectedFile != null) {
                    filesToSend.remove(selectedFile);
                }
            }
        });
        return fileList;
    }
}

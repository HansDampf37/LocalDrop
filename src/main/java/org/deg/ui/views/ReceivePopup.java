package org.deg.ui.views;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.deg.backend.UserConfigurations;
import org.deg.core.FileWithMetadata;
import org.deg.core.Peer;
import org.deg.core.callbacks.Progress;
import org.deg.ui.components.FileCell;
import org.deg.ui.components.FileSendingProgressBar;
import org.deg.utils.Utils;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class ReceivePopup extends Stage {
    private final FileSendingProgressBar progressBar = new FileSendingProgressBar(false);

    public ReceivePopup(List<FileWithMetadata> files, Peer sender, Consumer<Boolean> onDecision) {
        super();
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Data received by " + sender.name());

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER);

        Label title = new Label("Incoming Data by");
        Label nameBox = new Label(sender.name());
        nameBox.getStyleClass().add("nameLabel");

        ImageView profilePic = new ImageView(new Image("https://picsum.photos/200"));
        profilePic.setFitWidth(200);
        profilePic.setFitHeight(200);
        Circle clip = new Circle(100, 100, 100);
        profilePic.setClip(clip);

        ListView<FileWithMetadata> receivedFiles = new ListView<>();
        receivedFiles.setItems(FXCollections.observableList(files));
        receivedFiles.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(FileWithMetadata item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty && item != null && item.file() != null) {
                    FileCell fileCell = new FileCell(item.file().getName(), item.sizeInBytes());
                    fileCell.prefWidthProperty().bind(lv.widthProperty().subtract(40));
                    setGraphic(fileCell);
                    setText(null);
                } else {
                    setGraphic(null);
                    setText(null);
                }
            }
        });
        int numberOfFiles = files.size();
        long numberOfBytes = files.stream().mapToLong(FileWithMetadata::sizeInBytes).sum();
        Label details = new Label(numberOfFiles + " files, " + Utils.bytesToReadableString(numberOfBytes));

        HBox.setHgrow(progressBar, Priority.ALWAYS);
        progressBar.setVisible(false);

        HBox buttons = new HBox(10);
        Button abort = new Button("Abort");
        Button save = new Button("Save");
        save.setTooltip(new Tooltip("Your current data will be saved to '" + UserConfigurations.DEFAULT_SAFE_PATH + "'"));
        save.setOnMouseClicked(event -> {
            progressBar.onTransmissionStart();
            onDecision.accept(true);
            save.setDisable(true);
            abort.setDisable(true);
        });
        abort.setOnMouseClicked(event -> onDecision.accept(false));

        abort.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        save.setStyle("-fx-background-color: lightgreen;");

        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.getChildren().addAll(progressBar, abort, save);

        layout.getChildren().addAll(title, nameBox, profilePic, receivedFiles, details, buttons);

        StackPane root = new StackPane();
        root.getChildren().add(layout);
        Scene scene = new Scene(root, 600, 600);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());

        setScene(scene);
    }

    public void onReceivingProgress(Progress progress) {
        progressBar.setProgress(progress);
    }
}

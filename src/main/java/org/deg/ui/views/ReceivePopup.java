package org.deg.ui.views;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.deg.core.FileWithRelativePath;
import org.deg.core.Peer;
import org.deg.ui.components.Toast;
import org.deg.ui.components.ToastMode;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ReceivePopup extends Stage {
    private final ProgressBar progressBar = new ProgressBar();
    private boolean receivingFilesOngoing = false;

    public ReceivePopup(List<FileWithRelativePath> files, Peer sender, Consumer<Boolean> onDecision) {
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

        ListView<String> receivedFiles = new ListView<>();
        receivedFiles.setItems(FXCollections.observableList(files.stream().map((FileWithRelativePath f) -> f.relativePath).collect(Collectors.toList())));
        int numberOfFiles = files.size();
        long numberOfBytes = files.stream().mapToLong((FileWithRelativePath f) -> f.file.length()).sum();
        Label details = new Label(numberOfFiles + " files, " + numberOfBytes + " bytes");

        HBox buttons = new HBox(10);
        Button test = new Button("Click Me");
        Button abort = new Button("Abort");
        Button save = new Button("Save to Downloads");
        test.setOnMouseClicked(event -> {
            Toast.show(this, "Test", 1000, ToastMode.WARNING);
        });
        save.setOnMouseClicked(event -> {
            receivingFilesOngoing = true;
            progressBar.setVisible(true);
            onDecision.accept(true);
        });
        abort.setOnMouseClicked(event -> onDecision.accept(false));

        abort.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        save.setStyle("-fx-background-color: lightgreen;");

        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.getChildren().addAll(test, abort, save);

        layout.getChildren().addAll(title, nameBox, profilePic, receivedFiles, details, progressBar, buttons);

        StackPane root = new StackPane();
        root.getChildren().add(layout);
        Scene scene = new Scene(root, 600, 600);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());

        progressBar.setProgress(0);
        progressBar.setVisible(false);
        progressBar.setPrefWidth(200);

        setScene(scene);
    }

    public void onReceivingProgress(float progress) {
        if (receivingFilesOngoing) {
            progressBar.setVisible(true);
            progressBar.setProgress(progress);
        }
    }
}

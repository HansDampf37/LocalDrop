package org.deg.ui.views;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.deg.core.Peer;

import java.io.File;

public class ReceivePopup extends Stage {
    public ReceivePopup(File file, Peer sender) {
        super();
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Data received by " + sender.name());

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER);

        Label title = new Label("Data received by");
        Label nameBox = new Label(sender.name());
        nameBox.getStyleClass().add("nameLabel");
        ImageView profilePic = new ImageView(new Image("https://picsum.photos/200"));
        profilePic.setFitWidth(200);
        profilePic.setFitHeight(200);
        Circle clip = new Circle(100, 100, 100);
        profilePic.setClip(clip);
        ListView<String> receivedFiles = new ListView<>();
        receivedFiles.getItems().add(file.getName());

        HBox buttons = new HBox(10);
        Button abort = new Button("Abort");
        Button save = new Button("Save to Downloads");

        abort.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        save.setStyle("-fx-background-color: lightgreen;");

        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(abort, save);

        layout.getChildren().addAll(title, profilePic, receivedFiles, buttons);

        Scene scene = new Scene(layout, 300, 400);
        setScene(scene);
        showAndWait();
    }

    public void onReceivingProgress(float progress) {
        // TODO
    }
}

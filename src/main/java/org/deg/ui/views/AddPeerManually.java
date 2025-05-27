package org.deg.ui.views;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.deg.core.Peer;

import java.util.List;

public class AddPeerManually extends Stage {
    private final List<Peer> listToAddTo;

    public AddPeerManually(List<Peer> addToThisList) {
        super();
        listToAddTo = addToThisList;
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Add a peer manually");

        Scene scene = new Scene(getLayout(), 300, 200);
        setScene(scene);
    }

    private VBox getLayout() {
        VBox layout = new VBox();
        layout.setPadding(new Insets(15));
        layout.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label("Name:");
        TextField nameFieldName = new TextField();
        Label ipLabel = new Label("IP:");
        TextField nameFieldIP = new TextField();
        Label portLabel = new Label("Port:");
        TextField nameFieldPort = new TextField();

        HBox confirmCancelBox = new HBox(15);
        confirmCancelBox.setAlignment(Pos.CENTER_RIGHT);
        Button cancelButton = new Button("Cancel");
        Button confirmButton = new Button("Confirm");
        cancelButton.setOnAction(e -> {
            Platform.runLater(this::close);
        });
        confirmButton.setOnMouseClicked(event -> {
            Platform.runLater(() -> {
                String name = nameFieldName.getText();
                String ip = nameFieldIP.getText();
                String port = nameFieldPort.getText();

                try {
                    listToAddTo.add(new Peer(name, ip, Integer.parseInt(port)));
                    this.close();
                } catch (NumberFormatException e) {
                    nameFieldPort.setStyle("-fx-background-color: #ee7777");
                }
            });
        });
        confirmCancelBox.getChildren().addAll(cancelButton, confirmButton);

        layout.getChildren().addAll(nameLabel, nameFieldName, ipLabel, nameFieldIP, portLabel, nameFieldPort, confirmCancelBox);
        return layout;
    }
}

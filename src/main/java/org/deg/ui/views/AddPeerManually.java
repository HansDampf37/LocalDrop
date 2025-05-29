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

class TextFieldWithName extends VBox {

    final TextField inputField;

    public TextFieldWithName(String name) {
        inputField = new TextField();
        getChildren().addAll(new Label(name), inputField);
    }

    public String getText() {
        return inputField.getText();
    }
}

public class AddPeerManually extends Stage {
    private final List<Peer> listToAddTo;

    public AddPeerManually(List<Peer> addToThisList) {
        super();
        listToAddTo = addToThisList;
        initModality(Modality.APPLICATION_MODAL);
        setTitle("Add a peer manually");

        Scene scene = new Scene(getLayout(), 320, 235);
        setScene(scene);
    }

    private VBox getLayout() {
        VBox layout = new VBox();
        layout.setSpacing(15.0);
        layout.setPadding(new Insets(15));
        layout.setAlignment(Pos.CENTER_LEFT);

        TextFieldWithName nameInput = new TextFieldWithName("Name:");
        TextFieldWithName ipInput = new TextFieldWithName("IP:");
        TextFieldWithName portInput = new TextFieldWithName("Port:");

        HBox confirmCancelBox = new HBox(15);
        confirmCancelBox.setAlignment(Pos.CENTER_RIGHT);
        Button cancelButton = new Button("Cancel");
        Button confirmButton = new Button("Confirm");
        cancelButton.setOnAction(e -> Platform.runLater(this::close));
        confirmButton.setOnMouseClicked(event -> Platform.runLater(() -> {
            String name = nameInput.getText();
            String ip = ipInput.getText();
            String port = portInput.getText();

            try {
                listToAddTo.add(new Peer(name, ip, Integer.parseInt(port)));
                this.close();
            } catch (NumberFormatException e) {
                portInput.inputField.setStyle("-fx-background-color: #ee7777");
            }
        }));
        confirmCancelBox.getChildren().addAll(cancelButton, confirmButton);

        layout.getChildren().addAll(nameInput, ipInput, portInput, confirmCancelBox);
        return layout;
    }
}

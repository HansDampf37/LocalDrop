package org.deg.ui.views;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import org.deg.core.Peer;

public class ReceiveView extends VBox {
    public ReceiveView(Peer localPeer) {
        super(10);
        this.setAlignment(Pos.CENTER);

        Label visibleAsLabel = new Label("You are visible as:");

        ImageView profilePic = new ImageView(new Image("https://picsum.photos/200"));
        profilePic.setFitWidth(200);
        profilePic.setFitHeight(200);
        Circle clip = new Circle(100, 100, 100);
        profilePic.setClip(clip);

        Label nameLabel = new Label(localPeer.name());
        nameLabel.getStyleClass().add("nameLabel");

        Label ipLabel = new Label("IP: " + localPeer.ip());
        Label portLabel = new Label("Port: " + localPeer.fileTransferPort());

        this.getChildren().addAll(visibleAsLabel, profilePic, nameLabel, ipLabel, portLabel);
    }
}

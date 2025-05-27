package org.deg.ui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import org.deg.core.Peer;
import javafx.scene.control.ProgressBar;

public class PeerView extends HBox {
    private boolean transmittingFilesOngoing = false;
    private final ProgressBar progressBar = new ProgressBar();

    public PeerView(Peer peer) {
        setPadding(new Insets(10));
        setSpacing(10);
        setAlignment(Pos.CENTER_LEFT);

        ImageView avatar = new ImageView(new Image("https://picsum.photos/200"));
        avatar.setFitWidth(40);
        avatar.setFitHeight(40);
        Circle clip = new Circle(20, 20, 20);
        avatar.setClip(clip);
        Label nameLabel = new Label(peer.name());
        nameLabel.getStyleClass().add("nameLabel");

        Label addressLabel = new Label(peer.ip() + ":" + peer.fileTransferPort());
        VBox textBox = new VBox(nameLabel, addressLabel);
        textBox.setAlignment(Pos.CENTER_LEFT);

        VBox gap = new VBox();
        HBox.setHgrow(gap, Priority.ALWAYS);
        progressBar.setProgress(0);
        progressBar.setVisible(false);
        progressBar.setPrefWidth(200);


        getChildren().addAll(avatar, textBox, gap, progressBar);
        getStyleClass().add("peerView");
    }

    public void setProgress(float progress) {
        if (transmittingFilesOngoing) {
            progressBar.setProgress(progress);
        }
    }

    public void onTransmissionStart() {
        transmittingFilesOngoing = true;
        progressBar.setProgress(0);
        progressBar.setVisible(true);
    }

    public void onTransmissionStop() {
        transmittingFilesOngoing = false;
        progressBar.setProgress(1);
        progressBar.setVisible(false);
    }

}

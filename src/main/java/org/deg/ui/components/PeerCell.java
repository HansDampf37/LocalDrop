package org.deg.ui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import org.deg.core.Peer;
import org.deg.core.callbacks.Progress;


/**
 * View of a peer in the peer-list. Can be configured to display progress regarding the sending progress.
 */
public class PeerCell extends HBox {
    private final FileSendingProgressBar progressBar = new FileSendingProgressBar(false);
    private final ProgressIndicator spinner = new ProgressIndicator();


    public PeerCell(Peer peer) {
        setPadding(new Insets(10));
        setSpacing(20);
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

        HBox.setHgrow(progressBar, Priority.ALWAYS);
        spinner.setVisible(false);

        getChildren().addAll(avatar, textBox, progressBar, spinner);
        getStyleClass().add("peerView");
    }

    public void setProgress(Progress progress) {
        progressBar.setProgress(progress);
    }

    public void onTransmissionRequested() {
        spinner.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        spinner.setVisible(true);
    }

    public void onTransmissionRejected() {
        spinner.setVisible(false);
    }

    public void onTransmissionStart() {
        progressBar.onTransmissionStart();
        spinner.setVisible(false);
    }

    public void onTransmissionStop() {
        progressBar.onTransmissionStop();
        spinner.setVisible(false);
    }
}

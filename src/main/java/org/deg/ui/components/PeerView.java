package org.deg.ui.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import org.deg.core.Peer;
import org.deg.core.callbacks.Progress;
import org.deg.utils.Utils;


/**
 * Each PeerView has a state normally the default one that determines what is rendered.
 *  <br>
 * - <span style="font-weight: bold">Waiting</span>: We are waiting for this peer to accept our transmission request
 * <br>
 * - <span style="font-weight: bold">Sending</span>: We are currently sending data to this peer
 * <br>
 * - <span style="font-weight: bold">Default</span>: No sending, no waiting
 */
enum PeerState {
    WAITING,
    SENDING,
    DEFAULT
}

/**
 * View of a peer in the peer-list. Can be configured to display progress regarding the sending progress.
 */
public class PeerView extends HBox {
    private final Label filesSentCounterLabel;
    private final Label transmissionRateLabel;
    private final ProgressBar progressBar = new ProgressBar();
    private final ProgressIndicator spinner = new ProgressIndicator();;
    private PeerState state = PeerState.DEFAULT;

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
        spinner.setVisible(false);
        HBox.setHgrow(gap, Priority.ALWAYS);
        filesSentCounterLabel = new Label("-");
        filesSentCounterLabel.setVisible(false);
        progressBar.setProgress(0);
        progressBar.setVisible(false);
        progressBar.setPrefWidth(200);
        transmissionRateLabel = new Label("%");
        transmissionRateLabel.setVisible(false);


        getChildren().addAll(avatar, textBox, gap, filesSentCounterLabel, progressBar, transmissionRateLabel, spinner);
        getStyleClass().add("peerView");
    }

    public void setProgress(Progress progress) {
        if (state == PeerState.SENDING) {
            progressBar.setProgress(progress.totalProgress());
            filesSentCounterLabel.setText(progress.filesTransmitted + "/" + progress.totalFiles);
            transmissionRateLabel.setText(Utils.bitsPerSecondToReadableString(progress.bitsPerSecondEstimation));
        }
    }

    public void onTransmissionRequested() {
        state = PeerState.WAITING;
        spinner.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        spinner.setVisible(true);
    }

    public void onTransmissionRejected() {
        state = PeerState.DEFAULT;
        spinner.setVisible(false);
    }

    public void onTransmissionStart() {
        state = PeerState.SENDING;
        progressBar.setProgress(0);
        progressBar.setVisible(true);
        filesSentCounterLabel.setText("0");
        filesSentCounterLabel.setVisible(true);
        transmissionRateLabel.setVisible(true);
        spinner.setVisible(false);
    }

    public void onTransmissionStop() {
        state = PeerState.DEFAULT;
        progressBar.setVisible(false);
        filesSentCounterLabel.setVisible(false);
        transmissionRateLabel.setVisible(false);
        spinner.setVisible(false);
    }
}

package org.deg.ui.views;

import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.deg.backend.Backend;
import org.deg.core.Peer;
import org.deg.core.callbacks.FileSendingEventHandler;
import org.deg.core.callbacks.Progress;
import org.deg.ui.components.IconButton;
import org.deg.ui.components.PeerCell;
import org.deg.ui.components.Toast;
import org.deg.ui.components.ToastMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PeersSelection extends VBox {
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private final ObservableList<Peer> peers = FXCollections.observableArrayList();
    private final List<Peer> manuallyAddedPeers = new ArrayList<>();
    private final Backend backend;
    private final RotateTransition rotate;
    private final ImageView reloadIcon;
    private final ObservableList<File> filesToSend;

    public PeersSelection(Backend backend, ObservableList<File> selectedFiles) {
        super(15);
        this.backend = backend;
        this.filesToSend = selectedFiles;

        // configure backend
        backend.setOnNewPeerCallback((Peer peer) -> Platform.runLater(() -> peers.add(peer)));
        backend.setOnPeerDisconnectedCallback((Peer peer) -> Platform.runLater(() -> peers.remove(peer)));

        HBox titleBox = new HBox(15);
        Label peersLabel = new Label("Peers");
        peersLabel.getStyleClass().add("h1");

        ImageView plusIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/icons/plus.png")).toExternalForm()));
        IconButton btnManualSend = new IconButton(plusIcon);
        btnManualSend.setTooltip(new Tooltip("Can't find the peer? Send you Data to a manually configured Peer!"));
        btnManualSend.setOnAction(e -> {
            new AddPeerManually(manuallyAddedPeers).showAndWait();
            for (Peer peer : manuallyAddedPeers) {
                if (!peers.contains(peer)) {
                    peers.add(peer);
                }
            }
        });

        reloadIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/icons/reload.png")).toExternalForm()));
        IconButton btnReloadDiscovery = new IconButton(reloadIcon);
        btnReloadDiscovery.setTooltip(new Tooltip("Discover peers in the same network"));
        rotate = new RotateTransition(Duration.seconds(10), reloadIcon);
        rotate.setByAngle(3600);
        rotate.setCycleCount(RotateTransition.INDEFINITE);
        rotate.setInterpolator(javafx.animation.Interpolator.LINEAR);
        btnReloadDiscovery.setOnAction(e -> discoverPeers());

        titleBox.getChildren().addAll(peersLabel, btnManualSend, btnReloadDiscovery);

        ListView<Peer> peerList = getPeerListView();

        getChildren().addAll(titleBox, peerList);

        discoverPeers();
    }

    private void discoverPeers() {
        executor.submit(() -> {
            Platform.runLater(rotate::play);
            List<Peer> discoveredPeers = backend.discoverPeers();
            Platform.runLater(() -> {
                peers.clear();
                peers.addAll(discoveredPeers);
                peers.addAll(manuallyAddedPeers);
                rotate.stop();
                reloadIcon.setRotate(0);
            });
        });
    }

    private ListView<Peer> getPeerListView() {
        ListView<Peer> peerList = new ListView<>();
        peerList.setItems(peers);
        peerList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Peer peer, boolean empty) {
                super.updateItem(peer, empty);
                if (empty || peer == null) {
                    setGraphic(null);
                } else {
                    PeerCell peerView = new PeerCell(peer);
                    FileSendingEventHandler callback = new FileSendingEventHandler() {
                        @Override
                        public void onSendingProgress(Progress progress) {
                            Platform.runLater(() -> peerView.setProgress(progress));
                        }

                        @Override
                        public void onFinished(File file, Peer receiver) {
                        }

                        @Override
                        public void onFinished(Peer receiver) {
                            Platform.runLater(() -> {
                                String message = "Transmission to " + receiver.name() + " is complete";
                                Stage stage = (Stage) getScene().getWindow();
                                Toast.show(stage, message, 3000, ToastMode.SUCCESS);
                                peerView.onTransmissionStop();
                            });
                        }

                        @Override
                        public void onDenied(Peer receiver) {
                            Platform.runLater(() -> {
                                String message = "Transmission was denied by " + receiver.name();
                                Stage stage = (Stage) getScene().getWindow();
                                Toast.show(stage, message, 3000, ToastMode.INFO);
                                peerView.onTransmissionRejected();
                            });
                        }

                        @Override
                        public void onAccepted(Peer receiver) {
                            Platform.runLater(peerView::onTransmissionStart);
                        }

                        @Override
                        public void onSendingFailed(Exception e) {
                            Platform.runLater(() -> {
                                peerView.onTransmissionStop();
                                Stage stage = (Stage) getScene().getWindow();
                                Toast.show(stage, e.getMessage(), 3000, ToastMode.ERROR);
                            });
                        }
                    };
                    peerView.setOnMouseClicked(e -> {
                        peerView.onTransmissionRequested();
                        backend.startFilesTransfer(backend.localPeer, peer, filesToSend, callback);
                    });
                    setGraphic(peerView);
                }
            }
        });
        peerList.setSelectionModel(null); // make elements non-clickable
        peerList.disableProperty().bind(Bindings.isEmpty(filesToSend));
        return peerList;
    }
}

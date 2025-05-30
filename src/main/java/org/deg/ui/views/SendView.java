package org.deg.ui.views;

import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.deg.backend.Backend;
import org.deg.core.callbacks.FileSendingEventHandler;
import org.deg.core.callbacks.Progress;
import org.deg.ui.components.FilesSelection;
import org.deg.ui.components.PeerCell;
import org.deg.ui.components.Toast;
import org.deg.ui.components.ToastMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SendView extends VBox {
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private final FilesSelection filesSelection = new FilesSelection();
    private final ObservableList<org.deg.core.Peer> peers = FXCollections.observableArrayList();
    private final List<org.deg.core.Peer> manuallyAddedPeers = new ArrayList<>();
    private final Backend backend;
    private final RotateTransition rotate;
    private final ImageView reloadIcon;
    private final Stage mainStage;

    public SendView(Backend backend, Stage mainStage) {
        super(15);
        this.backend = backend;
        backend.setOnNewPeerCallback((org.deg.core.Peer peer) -> Platform.runLater(() -> peers.add(peer)));
        backend.setOnPeerDisconnectedCallback((org.deg.core.Peer peer) -> Platform.runLater(() -> peers.remove(peer)));
        discoverPeers();

        this.mainStage = mainStage;
        setPadding(new Insets(15));

        HBox titleBox = new HBox(15);
        Label peersLabel = new Label("Peers");
        peersLabel.getStyleClass().add("h1");

        Button btnManualSend = new Button();
        btnManualSend.setTooltip(new Tooltip("Can't find the peer? Send you Data to a manually configured Peer!"));
        btnManualSend.getStyleClass().add("btn");
        ImageView plusIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/icons/plus.png")).toExternalForm()));
        plusIcon.setFitWidth(20);
        plusIcon.setFitHeight(20);
        btnManualSend.setGraphic(plusIcon);
        btnManualSend.setContentDisplay(ContentDisplay.CENTER);
        btnManualSend.setOnAction(e -> {
            new AddPeerManually(manuallyAddedPeers).showAndWait();
            for (org.deg.core.Peer peer : manuallyAddedPeers) {
                if (!peers.contains(peer)) {
                    peers.add(peer);
                }
            }
        });

        Button btnReloadDiscovery = new Button();
        btnReloadDiscovery.setTooltip(new Tooltip("Discover peers in the same network"));
        btnReloadDiscovery.getStyleClass().add("btn");
        reloadIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/icons/reload.png")).toExternalForm()));
        reloadIcon.setFitWidth(20);
        reloadIcon.setFitHeight(20);
        rotate = new RotateTransition(Duration.seconds(10), reloadIcon);
        rotate.setByAngle(3600);
        rotate.setCycleCount(RotateTransition.INDEFINITE);
        rotate.setInterpolator(javafx.animation.Interpolator.LINEAR);
        btnReloadDiscovery.setGraphic(reloadIcon);
        btnReloadDiscovery.setContentDisplay(ContentDisplay.CENTER);
        btnReloadDiscovery.setOnAction(e -> discoverPeers());

        titleBox.getChildren().addAll(peersLabel, btnManualSend, btnReloadDiscovery);

        ListView<org.deg.core.Peer> peerList = getPeerListView();

        getChildren().addAll(filesSelection, titleBox, peerList);
    }

    private ListView<org.deg.core.Peer> getPeerListView() {
        ListView<org.deg.core.Peer> peerList = new ListView<>();
        peerList.setItems(peers);
        peerList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(org.deg.core.Peer peer, boolean empty) {
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
                        public void onFinished(java.io.File file, org.deg.core.Peer receiver) {
                        }

                        @Override
                        public void onFinished(org.deg.core.Peer receiver) {
                            Platform.runLater(() -> {
                                String message = "Transmission to " + receiver.name() + " is complete";
                                Toast.show(mainStage, message, 3000, ToastMode.SUCCESS);
                                peerView.onTransmissionStop();
                            });
                        }

                        @Override
                        public void onDenied(org.deg.core.Peer receiver) {
                            Platform.runLater(() -> {
                                String message = "Transmission was denied by " + receiver.name();
                                Toast.show(mainStage, message, 3000, ToastMode.INFO);
                                peerView.onTransmissionRejected();
                            });
                        }

                        @Override
                        public void onAccepted(org.deg.core.Peer receiver) {
                            Platform.runLater(peerView::onTransmissionStart);
                        }

                        @Override
                        public void onSendingFailed(Exception e) {
                            Platform.runLater(() -> {
                                peerView.onTransmissionStop();
                                Toast.show(mainStage, e.getMessage(), 3000, ToastMode.ERROR);
                            });
                        }
                    };
                    peerView.setOnMouseClicked(e -> {
                        peerView.onTransmissionRequested();
                        backend.startFilesTransfer(backend.localPeer, peer, filesSelection.filesToSend, callback);
                    });
                    setGraphic(peerView);
                }
            }
        });
        peerList.setSelectionModel(null); // make elements non-clickable
        peerList.disableProperty().bind(Bindings.isEmpty(filesSelection.filesToSend));
        return peerList;
    }

    private void discoverPeers() {
        executor.submit(() -> {
            Platform.runLater(rotate::play);
            List<org.deg.core.Peer> discoveredPeers = backend.discoverPeers();
            Platform.runLater(() -> {
                peers.clear();
                peers.addAll(discoveredPeers);
                peers.addAll(manuallyAddedPeers);
                rotate.stop();
                reloadIcon.setRotate(0);
            });
        });
    }
}

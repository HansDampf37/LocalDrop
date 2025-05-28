package org.deg.ui.views;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.deg.backend.Backend;
import org.deg.core.Peer;
import org.deg.core.callbacks.FileSendingEventHandler;
import org.deg.core.callbacks.Progress;
import org.deg.ui.components.PeerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SendView extends VBox {
    private final ObservableList<File> filesToSend = FXCollections.observableArrayList();
    private final ObservableList<Peer> peers = FXCollections.observableArrayList();
    private final List<Peer> manuallyAddedPeers = new ArrayList<>();
    private final Backend backend;

    public SendView(Backend backend) {
        super(15);
        this.backend = backend;

        setPadding(new Insets(15));

        Label dataLabel = new Label("Data");
        dataLabel.getStyleClass().add("h1");

        HBox innerBox = new HBox(15);
        innerBox.setPadding(new Insets(0));

        ListView<File> fileList = new ListView<>();
        fileList.setItems(filesToSend);
        fileList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(File item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        HBox.setHgrow(fileList, Priority.ALWAYS);

        VBox fileButtons = new VBox(10);
        Button btnAddFile = new Button("Add File");
        Button btnAddFolder = new Button("Add Folder");
        Button btnClear = new Button("Clear Selection");
        btnClear.disableProperty().bind(Bindings.isEmpty(filesToSend));
        btnAddFolder.setPrefWidth(130);
        btnAddFile.setPrefWidth(130);
        btnClear.setPrefWidth(130);
        btnAddFile.getStyleClass().add("btn");
        btnAddFolder.getStyleClass().add("btn");
        btnClear.getStyleClass().add("btn");
        ImageView fileIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/icons/document.png")).toExternalForm()));
        fileIcon.setFitWidth(40);
        fileIcon.setFitHeight(40);
        btnAddFile.setGraphic(fileIcon);
        btnAddFile.setContentDisplay(ContentDisplay.TOP);
        ImageView folderIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/icons/folder.png")).toExternalForm()));
        folderIcon.setFitWidth(40);
        folderIcon.setFitHeight(40);
        btnAddFolder.setGraphic(folderIcon);
        btnAddFolder.setContentDisplay(ContentDisplay.TOP);
        ImageView clearIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/icons/clear.png")).toExternalForm()));
        clearIcon.setFitWidth(40);
        clearIcon.setFitHeight(40);
        btnClear.setGraphic(clearIcon);
        btnClear.setContentDisplay(ContentDisplay.TOP);
        btnAddFile.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select File(s)");
            List<File> selectedFiles = fileChooser.showOpenMultipleDialog(btnAddFile.getScene().getWindow());
            if (selectedFiles != null) {
                filesToSend.addAll(selectedFiles);
            }
        });
        btnAddFolder.setOnAction(e -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select Folder");
            File selectedDir = directoryChooser.showDialog(btnAddFolder.getScene().getWindow());
            if (selectedDir != null) {
                filesToSend.add(selectedDir);
            }
        });
        btnClear.setOnAction(e -> {
            filesToSend.clear();
        });

        fileButtons.getChildren().addAll(btnAddFile, btnAddFolder, btnClear);

        innerBox.getChildren().addAll(fileList, fileButtons);

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
            for (Peer peer : manuallyAddedPeers) {
                if (!peers.contains(peer)) {
                    peers.add(peer);
                }
            }
        });

        Button btnReloadDiscovery = new Button();
        btnReloadDiscovery.setTooltip(new Tooltip("Discover peers in the same network"));
        btnReloadDiscovery.getStyleClass().add("btn");
        ImageView reloadIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/icons/reload.png")).toExternalForm()));
        reloadIcon.setFitWidth(20);
        reloadIcon.setFitHeight(20);
        btnReloadDiscovery.setGraphic(reloadIcon);
        btnReloadDiscovery.setContentDisplay(ContentDisplay.CENTER);
        btnReloadDiscovery.setOnAction(e -> discoverPeers());

        titleBox.getChildren().addAll(peersLabel, btnManualSend, btnReloadDiscovery);

        ListView<Peer> peerList = getPeerListView();

        getChildren().addAll(dataLabel, innerBox, titleBox, peerList);
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
                    PeerView peerView = new PeerView(peer);
                    FileSendingEventHandler callback = new FileSendingEventHandler() {
                        @Override
                        public void onSendingProgress(Progress progress) {
                            Platform.runLater(() -> peerView.setProgress(progress));
                        }

                        @Override
                        public void onFinished(File file, Peer receiver) {}

                        @Override
                        public void onFinished(Peer receiver) {
                            Platform.runLater(peerView::onTransmissionStop);
                        }

                        @Override
                        public void onDenied(Peer receiver) {
                            System.out.println("Denied"); // TODO
                        }

                        @Override
                        public void onAccepted(Peer receiver) {
                            Platform.runLater(peerView::onTransmissionStart);
                            // TODO
                        }

                        @Override
                        public void onSendingFailed(Exception e) {
                            e.printStackTrace();
                        }
                    };
                    peerView.setOnMouseClicked(e -> {
                        backend.startFilesTransfer(backend.localPeer, peer, filesToSend, callback);
                    });
                    setGraphic(peerView);
                }
            }
        });
        peerList.setSelectionModel(null); // make elements non-clickable
        discoverPeers();
        peerList.disableProperty().bind(Bindings.isEmpty(filesToSend));
        return peerList;
    }

    private void discoverPeers() {
        peers.clear();
        peers.addAll(backend.discoverPeers());
        peers.addAll(manuallyAddedPeers);
    }
}

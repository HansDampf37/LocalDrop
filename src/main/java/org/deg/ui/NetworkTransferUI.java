package org.deg.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.*;
import org.deg.backend.Backend;
import org.deg.core.Peer;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class NetworkTransferUI extends Application {

    private StackPane mainContent;
    private Pane receiveView;
    private Pane sendView;
    private final Backend backend = new Backend();
    private Button btnReceive;
    private Button btnSend;

    private final ObservableList<File> filesToSend = FXCollections.observableArrayList();
    private final ObservableList<Peer> peers = FXCollections.observableArrayList();

    public NetworkTransferUI() throws IOException {
        backend.start();
        backend.onFileReceived(this::showReceivePopup);
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Network Transfer UI");
        primaryStage.setOnCloseRequest((WindowEvent event) -> backend.stop());

        BorderPane root = new BorderPane();
        VBox navBar = createNavBar();
        root.setLeft(navBar);

        mainContent = new StackPane();
        receiveView = createReceiveView();
        sendView = createSendView();
        loadSendPage();

        root.setCenter(mainContent);

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();

        discoverPeers();
    }

    private void discoverPeers() {
        peers.clear();
        peers.addAll(backend.discoverPeers());
        peers.add(new Peer("Alice", "192.168.178.49", (int)(Math.random() * 64000)));
    }

    private VBox createNavBar() {
        VBox navBar = new VBox();
        navBar.setPadding(new Insets(10));
        navBar.setSpacing(10);
        navBar.setStyle("-fx-background-color: #d4edc9;");
        navBar.setPrefWidth(200);

        btnReceive = new Button("Receive");
        btnSend = new Button("Send");

        btnReceive.getStyleClass().add("nav-button");
        btnSend.getStyleClass().add("nav-button");

        btnReceive.setMaxWidth(Double.MAX_VALUE);
        btnSend.setMaxWidth(Double.MAX_VALUE);
        btnReceive.setMinWidth(150.0);
        btnReceive.setMinHeight(75.0);
        btnSend.setMinWidth(150.0);
        btnSend.setMinHeight(75.0);


        ImageView receiveIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/icons/user.png")).toExternalForm()));
        receiveIcon.setFitWidth(16);
        receiveIcon.setFitHeight(16);

        ImageView sendIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/icons/send.png")).toExternalForm()));
        sendIcon.setFitWidth(16);
        sendIcon.setFitHeight(16);

        // Set icons on buttons
        btnReceive.setGraphic(receiveIcon);
        btnSend.setGraphic(sendIcon);

        btnReceive.setContentDisplay(ContentDisplay.LEFT);
        btnSend.setContentDisplay(ContentDisplay.LEFT);

        btnReceive.setOnAction(e -> loadReceivePage());
        btnSend.setOnAction(e -> loadSendPage());

        navBar.getChildren().addAll(btnReceive, btnSend);
        return navBar;
    }

    private void loadReceivePage() {
        btnReceive.getStyleClass().add("active");
        btnSend.getStyleClass().remove("active");
        mainContent.getChildren().setAll(receiveView);
    }

    private void loadSendPage() {
        btnReceive.getStyleClass().remove("active");
        btnSend.getStyleClass().add("active");
        mainContent.getChildren().setAll(sendView);
    }

    private Pane createReceiveView() {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);

        Label visibleAsLabel = new Label("You are visible as:");

        ImageView profilePic = new ImageView(new Image("https://picsum.photos/200"));
        profilePic.setFitWidth(200);
        profilePic.setFitHeight(200);
        Circle clip = new Circle(100, 100, 100);
        profilePic.setClip(clip);

        Label nameLabel = new Label(backend.localPeer.name());
        nameLabel.getStyleClass().add("nameLabel");

        Label ipLabel = new Label("IP: " + backend.localPeer.ip());
        Label portLabel = new Label("Port: " + backend.localPeer.fileTransferPort());

        box.getChildren().addAll(visibleAsLabel, profilePic, nameLabel, ipLabel, portLabel);
        return box;
    }

    private Pane createSendView() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(15));

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
        btnAddFolder.setPrefWidth(100);
        btnAddFile.setPrefWidth(100);
        btnAddFile.getStyleClass().add("btn");
        btnAddFolder.getStyleClass().add("btn");
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

        fileButtons.getChildren().addAll(btnAddFile, btnAddFolder);

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

        box.getChildren().addAll(dataLabel, innerBox, titleBox, peerList);
        return box;
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
                    peerView.setOnMouseClicked(e -> {
                        peerView.onTransmissionStart();
                        backend.startFilesTransfer(backend.localPeer, peer, filesToSend, peerView::setProgress);
                    });
                    setGraphic(peerView);
                }
            }
        });
        peerList.setSelectionModel(null); // make elements unclickable
        return peerList;
    }

    private void showReceivePopup(File file, Peer sender) {
        Platform.runLater(() -> {
            Stage popup = new Stage();
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setTitle("Data received by " + sender.name());

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
            popup.setScene(scene);
            popup.showAndWait();
        });
    }
}

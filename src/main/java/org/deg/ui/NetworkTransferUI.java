package org.deg.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.deg.backend.Backend;

import java.io.IOException;

public class NetworkTransferUI extends Application {

    private StackPane mainContent;
    private Pane receiveView;
    private Pane sendView;
    private final Backend backend = new Backend();
    private Button btnReceive;
    private Button btnSend;

    public NetworkTransferUI() throws IOException {}

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Network Transfer UI");

        BorderPane root = new BorderPane();
        VBox navBar = createNavBar();
        root.setLeft(navBar);

        mainContent = new StackPane();
        receiveView = createReceiveView();
        sendView = createSendView();
        loadSendPage();

        root.setCenter(mainContent);

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
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


        ImageView receiveIcon = new ImageView(new Image(getClass().getResource("/icons/user.png").toExternalForm()));
        receiveIcon.setFitWidth(16);
        receiveIcon.setFitHeight(16);

        ImageView sendIcon = new ImageView(new Image(getClass().getResource("/icons/send.png").toExternalForm()));
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
        Label portLabel = new Label("PORT: " + backend.localPeer.fileTransferPort());

        box.getChildren().addAll(visibleAsLabel, profilePic, nameLabel, ipLabel, portLabel);
        return box;
    }

    private Pane createSendView() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(15));

        Label dataLabel = new Label("Data");
        ListView<String> fileList = new ListView<>();
        fileList.getItems().addAll("Selected_file_1.txt", "Selected_file_2.txt");

        HBox fileButtons = new HBox(10);
        Button btnAddFile = new Button("Add File");
        Button btnAddFolder = new Button("Add Folder");
        Button btnText = new Button("Text");
        btnAddFile.getStyleClass().add("btn");
        btnAddFolder.getStyleClass().add("btn");
        btnText.getStyleClass().add("btn");
        fileButtons.getChildren().addAll(btnAddFile, btnAddFolder, btnText);

        Label peersLabel = new Label("Peers");
        ListView<String> peerList = new ListView<>();
        peerList.getItems().addAll("Eve (192.168.178.3:5003)", "Bob (192.168.178.4:5003)");

        HBox inputFields = new HBox(5);
        inputFields.getChildren().addAll(
                new Button("Insert Name"),
                new Button("Insert IP"),
                new Button("Insert Port")
        );

        Button btnShowPopup = new Button("Simulate Incoming Transfer");
        btnShowPopup.setOnAction(e -> showReceivePopup());

        box.getChildren().addAll(dataLabel, fileList, fileButtons, peersLabel, peerList, inputFields, btnShowPopup);
        return box;
    }

    private void showReceivePopup() {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Data received by Alice");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER);

        Label title = new Label("Data received by Alice");
        ImageView profilePic = new ImageView(new Image("https://picsum.photos/200"));
        profilePic.setFitWidth(200);
        profilePic.setFitHeight(200);
        Circle clip = new Circle(100, 100, 100);
        profilePic.setClip(clip);
        ListView<String> receivedFiles = new ListView<>();
        receivedFiles.getItems().addAll("Selected_file_1.txt", "Selected_file_2.txt");

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
    }
}

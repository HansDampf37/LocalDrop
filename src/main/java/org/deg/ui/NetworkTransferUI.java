package org.deg.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    private Backend backend = new Backend();

    public NetworkTransferUI() throws IOException {
    }

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
        mainContent.getChildren().add(receiveView);

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
        navBar.setPrefWidth(100);

        Button btnReceive = new Button("Receive");
        Button btnSend = new Button("Send");

        btnReceive.setMaxWidth(Double.MAX_VALUE);
        btnSend.setMaxWidth(Double.MAX_VALUE);

        btnReceive.setOnAction(e -> mainContent.getChildren().setAll(receiveView));
        btnSend.setOnAction(e -> mainContent.getChildren().setAll(sendView));

        navBar.getChildren().addAll(btnReceive, btnSend);
        return navBar;
    }

    private Pane createReceiveView() {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);

        Label visibleAsLabel = new Label("You are visible as:");

        ImageView profile_pic = new ImageView(new Image("https://picsum.photos/200"));
        profile_pic.setFitWidth(200);
        profile_pic.setFitHeight(200);
        Circle clip = new Circle(100, 100, 100);
        profile_pic.setClip(clip);

        Label nameLabel = new Label(backend.localPeer.name());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 20px;");

        Label ipLabel = new Label("IP: " + backend.localPeer.ip());
        Label portLabel = new Label("PORT: " + backend.localPeer.fileTransferPort());

        box.getChildren().addAll(visibleAsLabel, profile_pic, nameLabel, ipLabel, portLabel);
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
        ImageView owlImage = new ImageView(new Image("https://picsum.photos/200"));
        ListView<String> receivedFiles = new ListView<>();
        receivedFiles.getItems().addAll("Selected_file_1.txt", "Selected_file_2.txt");

        HBox buttons = new HBox(10);
        Button abort = new Button("Abort");
        Button save = new Button("Save to Downloads");

        abort.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        save.setStyle("-fx-background-color: lightgreen;");

        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(abort, save);

        layout.getChildren().addAll(title, owlImage, receivedFiles, buttons);

        Scene scene = new Scene(layout, 300, 400);
        popup.setScene(scene);
        popup.showAndWait();
    }
}

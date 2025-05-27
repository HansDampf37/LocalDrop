package org.deg.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.deg.backend.Backend;
import org.deg.ui.views.LogView;
import org.deg.ui.views.ReceiveView;
import org.deg.ui.views.SendView;

import java.io.IOException;
import java.util.Objects;

public class NetworkTransferUI extends Application {

    private StackPane mainContent;
    private Pane receiveView;
    private Pane sendView;
    private final Backend backend = new Backend();
    private Button btnReceive;
    private Button btnSend;
    private Button btnLogs;

    public NetworkTransferUI() throws IOException {
        backend.start();
        backend.setFileReceivedHandler(new FileReceivingHandler());
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Network Transfer UI");
        primaryStage.setOnCloseRequest((WindowEvent event) -> {
            backend.stop();
            System.exit(0);
        });

        BorderPane root = new BorderPane();
        VBox navBar = createNavBar();
        root.setLeft(navBar);

        mainContent = new StackPane();
        receiveView = new ReceiveView(backend.localPeer);
        sendView = new SendView(backend);
        loadSendPage();

        root.setCenter(mainContent);

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createNavBar() {
        VBox navBar = new VBox();
        navBar.setPadding(new Insets(10));
        navBar.setSpacing(10);
        navBar.getStyleClass().add("navBar");
        navBar.setPrefWidth(200);

        btnReceive = new Button("Receive");
        btnSend = new Button("Send");
        btnLogs = new Button("Logs");

        btnReceive.getStyleClass().add("nav-button");
        btnSend.getStyleClass().add("nav-button");
        btnLogs.getStyleClass().add("nav-button");

        btnReceive.setMaxWidth(Double.MAX_VALUE);
        btnSend.setMaxWidth(Double.MAX_VALUE);
        btnLogs.setMaxWidth(Double.MAX_VALUE);
        btnReceive.setMinWidth(150.0);
        btnReceive.setMinHeight(75.0);
        btnLogs.setMinHeight(75.0);
        btnSend.setMinWidth(150.0);
        btnSend.setMinHeight(75.0);
        btnLogs.setMinHeight(75.0);

        ImageView receiveIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/icons/user.png")).toExternalForm()));
        receiveIcon.setFitWidth(16);
        receiveIcon.setFitHeight(16);

        ImageView sendIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/icons/send.png")).toExternalForm()));
        sendIcon.setFitWidth(16);
        sendIcon.setFitHeight(16);

        ImageView logsIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/icons/logs.png")).toExternalForm()));
        logsIcon.setFitWidth(16);
        logsIcon.setFitHeight(16);

        // Set icons on buttons
        btnReceive.setGraphic(receiveIcon);
        btnSend.setGraphic(sendIcon);
        btnLogs.setGraphic(logsIcon);

        btnReceive.setContentDisplay(ContentDisplay.LEFT);
        btnSend.setContentDisplay(ContentDisplay.LEFT);
        btnLogs.setContentDisplay(ContentDisplay.LEFT);

        btnReceive.setOnAction(e -> loadReceivePage());
        btnSend.setOnAction(e -> loadSendPage());
        btnLogs.setOnAction(e -> loadLogsPage());

        navBar.getChildren().addAll(btnReceive, btnSend, btnLogs);
        return navBar;
    }

    private void loadReceivePage() {
        btnReceive.getStyleClass().add("active");
        btnLogs.getStyleClass().remove("active");
        btnSend.getStyleClass().remove("active");
        mainContent.getChildren().setAll(receiveView);
    }

    private void loadSendPage() {
        btnReceive.getStyleClass().remove("active");
        btnLogs.getStyleClass().remove("active");
        btnSend.getStyleClass().add("active");
        mainContent.getChildren().setAll(sendView);
    }

    private void loadLogsPage() {
        btnReceive.getStyleClass().remove("active");
        btnSend.getStyleClass().remove("active");
        btnLogs.getStyleClass().add("active");
        mainContent.getChildren().setAll(new LogView(backend));
    }
}

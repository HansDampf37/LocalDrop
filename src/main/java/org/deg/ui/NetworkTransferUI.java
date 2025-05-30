package org.deg.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.deg.backend.Backend;
import org.deg.ui.components.NavButton;
import org.deg.ui.views.LogView;
import org.deg.ui.views.ReceiveView;
import org.deg.ui.views.SendView;
import org.deg.ui.views.SettingsView;

import java.io.IOException;
import java.util.Objects;

public class NetworkTransferUI extends Application {
    private final Backend backend = new Backend();

    private StackPane mainContent;
    private Pane receiveView;
    private Pane sendView;

    private NavButton btnReceive;
    private NavButton btnSend;
    private NavButton btnLogs;

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

        BorderPane borderPane = new BorderPane();
        VBox navBar = createNavBar();
        borderPane.setLeft(navBar);

        mainContent = new StackPane();
        receiveView = new ReceiveView(backend.localPeer);
        sendView = new SendView(backend);
        loadSendPage();

        borderPane.setCenter(mainContent);
        StackPane root = new StackPane(borderPane);

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles.css")).toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createNavBar() {
        VBox navBar = new VBox();
        navBar.setAlignment(Pos.CENTER);
        navBar.setPadding(new Insets(20));
        navBar.setSpacing(20);
        navBar.getStyleClass().add("navBar");
        navBar.setPrefWidth(200);

        ImageView receiveIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/icons/user.png")).toExternalForm()));
        ImageView sendIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/icons/send.png")).toExternalForm()));
        ImageView logsIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/icons/logs.png")).toExternalForm()));
        ImageView settingsIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/icons/setting.png")).toExternalForm()));

        btnReceive = new NavButton("Receive", receiveIcon);
        btnSend = new NavButton("Send", sendIcon);
        btnLogs = new NavButton("Logs", logsIcon);
        Button btnSettings = new Button("");
        btnSettings.getStyleClass().add("nav-button-small");
        settingsIcon.setFitWidth(16);
        settingsIcon.setFitHeight(16);
        btnSettings.setGraphic(settingsIcon);
        btnSettings.setContentDisplay(ContentDisplay.LEFT);

        btnReceive.setOnAction(e -> loadReceivePage());
        btnSend.setOnAction(e -> loadSendPage());
        btnLogs.setOnAction(e -> loadLogsPage());
        btnSettings.setOnAction(e -> loadSettingsPage());

        VBox gap = new VBox();
        VBox.setVgrow(gap, Priority.ALWAYS);

        navBar.getChildren().addAll(btnReceive, btnSend, btnLogs, gap, btnSettings);
        return navBar;
    }

    private void loadReceivePage() {
        btnReceive.activate();
        btnLogs.deactivate();
        btnSend.deactivate();
        mainContent.getChildren().setAll(receiveView);
    }

    private void loadSendPage() {
        btnReceive.deactivate();
        btnSend.activate();
        btnLogs.deactivate();
        mainContent.getChildren().setAll(sendView);
    }

    private void loadLogsPage() {
        btnReceive.deactivate();
        btnSend.deactivate();
        btnLogs.activate();
        mainContent.getChildren().setAll(new LogView(backend));
    }

    private void loadSettingsPage() {
        btnReceive.deactivate();
        btnSend.deactivate();
        btnLogs.deactivate();
        mainContent.getChildren().setAll(new SettingsView());
    }
}

package org.deg.ui;

import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.deg.backend.Backend;
import org.deg.ui.components.IconButton;
import org.deg.ui.components.Logo;
import org.deg.ui.components.NavButton;
import org.deg.ui.views.LogView;
import org.deg.ui.views.ReceiveView;
import org.deg.ui.views.SendView;
import org.deg.ui.views.SettingsView;

import java.io.IOException;
import java.util.Objects;

public class NetworkTransferUI extends Application {
    private Backend backend;
    private StackPane mainContent;
    private Pane receiveView;
    private Pane sendView;

    private NavButton btnReceive;
    private NavButton btnSend;
    private NavButton btnLogs;
    private RotateTransition rotate;

    public NetworkTransferUI() {
        Backend backend1;
        try {
            backend1 = new Backend();
        } catch (IOException e) {
            backend1 = null;
        }
        backend = backend1;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.getIcons().add(new Image(
                Objects.requireNonNull(getClass().getResource("/logo.png")).toExternalForm(),
                512.0,
                512.0,
                true,
                true
        ));
        primaryStage.setTitle("Network Transfer UI");
        primaryStage.setOnCloseRequest((WindowEvent event) -> {
            if (backend != null) backend.stop();
            System.exit(0);
        });
        if (backend != null) {
            backend.start();
            backend.setFileReceivedHandler(new FileReceivingHandler(primaryStage));
        }

        BorderPane borderPane = new BorderPane();
        VBox navBar = createNavBar();
        borderPane.setLeft(navBar);
        if (backend == null) borderPane.setTop(getDisconnectedLabel(primaryStage));

        mainContent = new StackPane();
        receiveView = new ReceiveView(backend == null ? null : backend.getLocalPeer());
        sendView = new SendView(backend);
        loadReceivePage();

        borderPane.setCenter(mainContent);
        StackPane root = new StackPane(borderPane);

        Scene scene = new Scene(root, 800, 600);
        Font.loadFont(getClass().getResourceAsStream("/fonts/NotoNaskhArabic[wght].ttf"), 30);
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

        navBar.getChildren().addAll(new Logo(), btnReceive, btnSend, btnLogs, gap, btnSettings);
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

    private HBox getDisconnectedLabel(Stage stage) {
        Label label = new Label("You are not connected to any network. Please connect to a wifi and try again.");
        HBox box = new HBox();
        box.getStyleClass().add("error");
        box.setPadding(new Insets(10));
        HBox.setHgrow(box, Priority.ALWAYS);
        box.setAlignment(Pos.CENTER_LEFT);

        label.setStyle("-fx-font-weight: bold;");
        label.getStyleClass().add("error");

        HBox gap = new HBox();
        HBox.setHgrow(gap, Priority.ALWAYS);

        ImageView reloadIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/icons/reload.png")).toExternalForm()));
        IconButton reloadButton = new IconButton(reloadIcon);
        rotate = new RotateTransition(Duration.seconds(10), reloadIcon);
        rotate.setByAngle(3600);
        rotate.setCycleCount(RotateTransition.INDEFINITE);
        rotate.setInterpolator(javafx.animation.Interpolator.LINEAR);
        reloadButton.setOnAction(e -> reloadBackend(stage));

        box.getChildren().addAll(label, gap, reloadButton);
        return box;
    }

    private void reloadBackend(Stage stage) {
        rotate.play();
        try {
            backend = new Backend();
            start(stage);
        } catch (IOException ignored) {
        } finally {
            rotate.stop();
        }
    }
}

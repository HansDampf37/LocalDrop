package org.deg.ui.views;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import org.deg.backend.Backend;
import org.deg.core.Peer;
import org.deg.ui.components.FileLog;

import java.io.File;

import static org.deg.utils.Utils.openFileExplorer;

public class LogView extends VBox {
    public LogView(Backend backend) {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(15));

        // First row
        VBox vbox1 = new VBox(15);
        Label labelSentLogs = new Label("Sent:");
        labelSentLogs.getStyleClass().add("h1");
        ListView<Pair<Peer, File>> sentLogs = new ListView<>();
        ObservableList<Pair<Peer, File>> sentLogsList = FXCollections.observableArrayList(backend.getSentLog());
        sentLogs.setItems(sentLogsList);
        sentLogs.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Pair<Peer, File> item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setGraphic(null);
                else {
                    FileLog fileLog = new FileLog(item.getValue(), item.getKey(), true);
                    fileLog.prefWidthProperty().bind(lv.widthProperty().subtract(40));
                    setGraphic(fileLog);
                }
            }
        });
        VBox.setVgrow(sentLogs, Priority.ALWAYS);
        vbox1.getChildren().addAll(labelSentLogs, sentLogs);

        // Second column
        VBox vbox2 = new VBox(5);
        Label labelReceivedLog = new Label("Received:");
        labelReceivedLog.getStyleClass().add("h1");

        ListView<Pair<Peer, File>> receivedLogs = new ListView<>();
        ObservableList<Pair<Peer, File>> receivedLogsList = FXCollections.observableArrayList(backend.getReceivedLog());
        receivedLogs.setItems(receivedLogsList);
        receivedLogs.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Pair<Peer, File> item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setGraphic(null);
                else {
                    FileLog fileLog = new FileLog(item.getValue(), item.getKey(), false);
                    fileLog.prefWidthProperty().bind(lv.widthProperty().subtract(40));
                    setGraphic(fileLog);
                }
            }
        });

        receivedLogs.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Use double-clicks to trigger
                Pair<Peer, File> selected = receivedLogs.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    File file = selected.getValue();
                    openFileExplorer(file.getParentFile());
                }
            }
        });

        VBox.setVgrow(receivedLogs, Priority.ALWAYS);
        vbox2.getChildren().addAll(labelReceivedLog, receivedLogs);

        HBox.setHgrow(vbox1, Priority.ALWAYS);
        HBox.setHgrow(vbox2, Priority.ALWAYS);

        vbox.getChildren().addAll(vbox1, vbox2);

        // Allow hbox to grow
        VBox.setVgrow(vbox, Priority.ALWAYS);
        getChildren().add(vbox);
    }
}

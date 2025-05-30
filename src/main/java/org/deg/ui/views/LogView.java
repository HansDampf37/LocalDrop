package org.deg.ui.views;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.util.Pair;
import org.deg.backend.Backend;
import org.deg.core.Peer;

import java.io.File;

public class LogView extends VBox {
    public LogView(Backend backend) {
        HBox hbox = new HBox(15);
        hbox.setPadding(new Insets(15));

        // First column
        VBox vbox1 = new VBox(5);
        Label labelSentLogs = new Label("Sent:");
        labelSentLogs.getStyleClass().add("h1");
        ListView<Pair<Peer, File>> sentLogs = new ListView<>();
        ObservableList<Pair<Peer, File>> sentLogsList = FXCollections.observableArrayList(backend.getSentLog());
        sentLogs.setItems(sentLogsList);
        sentLogs.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Pair<Peer, File> item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getValue().getName() + " to " + item.getKey().name());
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
                setText(empty || item == null ? null : item.getValue().getName() + " from " + item.getKey().name());
            }
        });
        VBox.setVgrow(receivedLogs, Priority.ALWAYS);
        vbox2.getChildren().addAll(labelReceivedLog, receivedLogs);

        HBox.setHgrow(vbox1, Priority.ALWAYS);
        HBox.setHgrow(vbox2, Priority.ALWAYS);

        hbox.getChildren().addAll(vbox1, vbox2);

        // Allow hbox to grow
        VBox.setVgrow(hbox, Priority.ALWAYS);
        getChildren().add(hbox);
    }
}

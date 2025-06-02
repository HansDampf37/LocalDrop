package org.deg.ui.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import org.deg.core.Peer;
import org.deg.utils.Utils;

import java.io.File;
import java.util.Objects;

public class FileLog extends HBox {
    public FileLog(File file, Peer peer, boolean sent) {
        super();
        setAlignment(Pos.CENTER_LEFT);
        setMaxWidth(Double.MAX_VALUE);

        Label peerLabel = new Label((sent ? " to " : " from ") + peer.name());
        peerLabel.setStyle("-fx-text-fill: gray;");
        HBox stringBox = new HBox(5,
                new Label(file.getName()),
                peerLabel
        );
        stringBox.setAlignment(Pos.CENTER_LEFT);

        Region gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);

        ImageView exportIcon = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/icons/export.png")).toExternalForm()));
        IconButton openInExplorer = getOpenButton(file, exportIcon);
        openInExplorer.visibleProperty().bind(this.hoverProperty());

        getChildren().addAll(stringBox, gap, openInExplorer);
    }


    private IconButton getOpenButton(File file, ImageView exportIcon) {
        IconButton openInExplorer = new IconButton(exportIcon);
        openInExplorer.setTooltip(new Tooltip("Open in Explorer"));
        openInExplorer.setOnMouseClicked(event -> Utils.openFileExplorer(file.getParentFile()));
        openInExplorer.setMinWidth(Region.USE_PREF_SIZE);                               // prevent shrinking below preferred width
        openInExplorer.setMaxWidth(Region.USE_PREF_SIZE);                               // prevent growing beyond preferred width
        openInExplorer.setStyle("-fx-text-overrun: clip; -fx-cursor: hand;");           // clip if really tiny, no ellipsis
        return openInExplorer;
    }
}

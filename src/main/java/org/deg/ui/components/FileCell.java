package org.deg.ui.components;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import org.deg.utils.Utils;

public class FileCell extends HBox {
    public FileCell(String name, long numberOfBytes) {
        Label nameLabel = new Label(name);

        HBox gap = new HBox();
        HBox.setHgrow(gap, Priority.ALWAYS);

        Label sizeLabel = new Label(Utils.bytesToReadableString(numberOfBytes));
        sizeLabel.setMinWidth(Region.USE_PREF_SIZE);            // prevent shrinking below preferred width
        sizeLabel.setMaxWidth(Region.USE_PREF_SIZE);            // prevent growing beyond preferred width
        sizeLabel.setStyle("-fx-text-overrun: clip;");          // clip if really tiny, no ellipsis

        getChildren().addAll(nameLabel, gap, sizeLabel);
    }
}

package org.deg.ui.views;

import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import org.deg.backend.Backend;
import org.deg.ui.components.FilesSelection;

public class SendView extends VBox {

    public SendView(Backend backend) {
        super(15);

        setPadding(new Insets(15));

        FilesSelection filesSelection = new FilesSelection();
        PeersSelection peersSelection = new PeersSelection(backend, filesSelection.filesToSend);

        getChildren().addAll(filesSelection, peersSelection);
    }
}

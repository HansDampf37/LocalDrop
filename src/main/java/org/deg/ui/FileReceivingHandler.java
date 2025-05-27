package org.deg.ui;

import javafx.application.Platform;
import org.deg.core.Peer;
import org.deg.core.callbacks.FileReceivingEventHandler;
import org.deg.ui.views.ReceivePopup;

import java.io.File;

public class FileReceivingHandler implements FileReceivingEventHandler {
    private ReceivePopup receivePopup = null;

    @Override
    public boolean onIncomingFile(File file, Peer sender) {
        Platform.runLater(() -> receivePopup = new ReceivePopup(file, sender));
        return false;
    }

    @Override
    public void onReceivingProgress(float progress) {
        if (receivePopup != null) {
            receivePopup.onReceivingProgress(progress);
        }
    }

    @Override
    public void onReceivingFinished(File file, Peer sender) {
        if (receivePopup != null) receivePopup.close();

    }

    @Override
    public void onReceivingFailed(Exception e) {
        e.printStackTrace();
    }
}

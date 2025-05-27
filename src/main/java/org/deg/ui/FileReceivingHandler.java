package org.deg.ui;

import javafx.application.Platform;
import org.deg.core.Peer;
import org.deg.core.callbacks.FileReceivingEventHandler;
import org.deg.ui.views.ReceivePopup;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class FileReceivingHandler implements FileReceivingEventHandler {
    private ReceivePopup receivePopup = null;

    @Override
    public boolean onIncomingFile(File file, Peer sender) {
        CompletableFuture<Boolean> userResponse = new CompletableFuture<>();

        Platform.runLater(() -> {
            ReceivePopup popup = new ReceivePopup(file, sender, userResponse::complete);
            this.receivePopup = popup;
            popup.show();
        });

        try {
            if (userResponse.get()) {
                return true;
            } else {
                Platform.runLater(() -> this.receivePopup.close());
                return false;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onReceivingProgress(float progress) {
        if (receivePopup != null) {
            receivePopup.onReceivingProgress(progress);
        }
    }

    @Override
    public void onReceivingFinished(File file, Peer sender) {
        if (receivePopup != null) {
            Platform.runLater(() -> receivePopup.close());
        }

    }

    @Override
    public void onReceivingFailed(Exception e) {
        e.printStackTrace();
    }
}

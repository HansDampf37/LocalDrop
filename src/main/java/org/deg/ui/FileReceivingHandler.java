package org.deg.ui;

import javafx.application.Platform;
import org.deg.core.FileWithRelativePath;
import org.deg.core.Peer;
import org.deg.core.callbacks.FileReceivingEventHandler;
import org.deg.ui.components.Toast;
import org.deg.ui.components.ToastMode;
import org.deg.ui.views.ReceivePopup;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class FileReceivingHandler implements FileReceivingEventHandler {
    private ReceivePopup receivePopup = null;

    @Override
    public boolean onIncomingFiles(List<FileWithRelativePath> files, Peer sender) {
        CompletableFuture<Boolean> userResponse = new CompletableFuture<>();

        Platform.runLater(() -> {
            ReceivePopup popup = new ReceivePopup(files, sender, userResponse::complete);
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
    public void onReceivingProgress(FileWithRelativePath file, float progress) {
        if (receivePopup != null) {
            receivePopup.onReceivingProgress(progress);
        }
    }

    @Override
    public void onReceivingFinished(FileWithRelativePath file, Peer sender) {}

    @Override
    public void onReceivingFinished(Peer sender) {
        if (receivePopup != null) {
            Platform.runLater(() -> receivePopup.close());
        }
    }

    @Override
    public void onReceivingFailed(Exception e) {
        Toast.show(receivePopup, e.getMessage(), 3000, ToastMode.ERROR);
        e.printStackTrace();
    }
}

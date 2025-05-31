package org.deg.ui;

import javafx.application.Platform;
import org.deg.core.FileWithMetadata;
import org.deg.core.Peer;
import org.deg.core.callbacks.FileReceivingEventHandler;
import org.deg.core.callbacks.Progress;
import org.deg.ui.components.Toast;
import org.deg.ui.components.ToastMode;
import org.deg.ui.views.ReceivePopup;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class FileReceivingHandler implements FileReceivingEventHandler {
    private ReceivePopup receivePopup = null;
    private Consumer<Exception> onFailed = null;
    private Consumer<Peer> onFinished = null;

    @Override
    public boolean onIncomingFiles(List<FileWithMetadata> files, Peer sender) {
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
    public void onReceivingProgress(Progress progress) {
        if (receivePopup != null) {
            Platform.runLater(() -> receivePopup.onReceivingProgress(progress));
        }
    }

    @Override
    public void onReceivingFinished(FileWithMetadata file, Peer sender) {}

    @Override
    public void onReceivingFinished(Peer sender) {
        if (receivePopup != null) {
            Platform.runLater(() -> {
                receivePopup.close();
                receivePopup = null;
            });
        }
        if (onFinished != null) onFinished.accept(sender);
    }

    @Override
    public void onReceivingFailed(Exception e) {
        Platform.runLater(() -> Toast.show(receivePopup, e.getMessage(), 3000, ToastMode.ERROR));
        e.printStackTrace();
        if (onFailed != null) onFailed.accept(e);
    }

    public void setOnFailed(Consumer<Exception> onFailed) {
        this.onFailed = onFailed;
    }

    public void setOnFinished(Consumer<Peer> onFinished) {
        this.onFinished = onFinished;
    }
}

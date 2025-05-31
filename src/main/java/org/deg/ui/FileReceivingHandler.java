package org.deg.ui;

import javafx.application.Platform;
import javafx.stage.Stage;
import org.deg.backend.UserConfigurations;
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

public class FileReceivingHandler implements FileReceivingEventHandler {
    private ReceivePopup receivePopup = null;
    private final Stage mainStage;

    public FileReceivingHandler(Stage mainStage) {
        this.mainStage = mainStage;
    }

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
        if (receivePopup != null) Platform.runLater(() -> receivePopup.updateProgress(progress));
    }

    @Override
    public void onReceivingFinished(List<FileWithMetadata> files, Peer sender) {
        if (receivePopup != null) {
            Platform.runLater(() -> {
                receivePopup.close();
                receivePopup = null;
            });
        }
        Platform.runLater(() -> {
            long failCount = files.stream().filter(f -> !f.transmissionSuccess).count();
            long successCount = files.stream().filter(f -> f.transmissionSuccess).count();
            ToastMode toastMode;
            String message;
            if (successCount > 0 && failCount == 0) {
                toastMode = ToastMode.SUCCESS;
                message = "Successfully received " + successCount + " files by '" + sender.name() + "' (saved to"
                        + UserConfigurations.DEFAULT_SAFE_PATH.getAbsolutePath() + ")";
            } else if (successCount == 0 && failCount > 0) {
                toastMode = ToastMode.ERROR;
                message = "Failed to receive " + failCount + " files by '" + sender.name() + "'";
            } else {
                toastMode = ToastMode.ERROR;
                message = "Successfully received " + successCount + " files by '" + sender.name() + "' (saved to"
                        + UserConfigurations.DEFAULT_SAFE_PATH.getAbsolutePath() + ")" +
                        "\nFailed to receive " + failCount + " files by '" + sender.name() + "'";
            }
            Toast.show(mainStage, message, 4000, toastMode);
        });
    }

    @Override
    public void onReceivingError(Exception e) {
        receivePopup.close();
        Platform.runLater(() -> Toast.show(mainStage, e.getMessage(), 3000, ToastMode.ERROR));
        e.printStackTrace();
    }
}

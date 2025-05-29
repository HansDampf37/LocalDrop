package org.deg.ui.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.deg.core.callbacks.Progress;
import org.deg.utils.Utils;

public class FileSendingProgressBar extends HBox {
    private final ProgressBar progressBar = new ProgressBar();
    private final Label filesSentCounterLabel = new Label("0/0");
    private final Label transmissionRateLabel = new Label("0 Bit/s");

    public FileSendingProgressBar(boolean startVisible) {
        setSpacing(10);
        setAlignment(Pos.CENTER);
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setProgress(0.0);
        HBox.setHgrow(progressBar, Priority.ALWAYS); // always grow progress bar when possible
        getChildren().addAll(filesSentCounterLabel, progressBar, transmissionRateLabel);
        this.setVisible(startVisible);
    }

    public void setProgress(Progress progress) {
        filesSentCounterLabel.setText(progress.filesTransmitted + "/" + progress.totalFiles);
        progressBar.setProgress(progress.totalProgress());
        transmissionRateLabel.setText(Utils.bitsPerSecondToReadableString(progress.bitsPerSecondEstimation));
    }

    public void onTransmissionStart() {
        progressBar.setProgress(0);
        filesSentCounterLabel.setText("0");
        this.setVisible(true);
    }

    public void onTransmissionStop() {
        this.setVisible(false);
    }
}

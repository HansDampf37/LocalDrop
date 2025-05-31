package org.deg.core.callbacks;

import org.deg.core.FileWithMetadata;
import org.deg.core.Peer;

import java.util.List;

/**
 * Interface that is used to report the progress when sending a file over a tcp connection
 */
public interface FileSendingEventHandler {
    /**
     * Is called when new bytes of the file are sent.
     * @param progress The total progress
     */
    void onSendingProgress(Progress progress);

    /**
     * Is called whenever the transmission of the file failed
     * @param e the exception that caused the failure
     */
    void onSendingFailed(Exception e);

    /**
     * Is called when ALL files were successfully transmitted
     * @param files the list of files to send
     * @param receiver the receiving peer
     */
    void onFinished(List<FileWithMetadata> files, Peer receiver);

    /**
     * Is called when the receiver denies the transmission request
     */
    void onDenied(Peer receiver);

    /**
     * Is called when the receiver accepts the transmission request
     */
    void onAccepted(Peer receiver);
}


package org.deg.core.callbacks;

import org.deg.core.Peer;

import java.io.File;

/**
 * Interface that is used to report the progress when sending a file over a tcp connection
 */
public interface FileSendingEventHandler {
    /**
     * Is called when new bytes of the file are sent.
     * @param progress The total progress between 0 and 1
     */
    void onSendingProgress(float progress);

    /**
     * Is called when the file was successfully transmitted
     * @param file the file
     * @param receiver the receiving peer
     */
    void onFinished(File file, Peer receiver);

    /**
     * Is called whenever the transmission of the file failed
     * @param e the exception that caused the failure
     */
    void onSendingFailed(Exception e);
}

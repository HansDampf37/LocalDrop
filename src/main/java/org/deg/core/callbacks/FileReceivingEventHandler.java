package org.deg.core.callbacks;

import org.deg.core.Peer;

import java.io.File;

/**
 * Interface that handles events while receiving files
 */
public interface FileReceivingEventHandler {
    /**
     * Is called when a new request for file transmission is received. Depending
     * on the return value transmission is allowed or denied.
     * @param file the incoming file's name
     * @param sender the sending peer
     * @return whether to accept this request
     */
    boolean onIncomingFile(File file, Peer sender);

    /**
     * Is called when new bytes of the file are received.
     * @param progress The total progress between 0 and 1
     */
    void onReceivingProgress(float progress);

    /**
     * Is called when the file was successfully transmitted
     * @param file the file
     * @param sender the sending peer
     */
    void onReceivingFinished(File file, Peer sender);

    /**
     * Is called whenever the transmission of the file failed
     * @param e the exception that caused the failure
     */
    void onReceivingFailed(Exception e);
}

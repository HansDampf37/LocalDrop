package org.deg.core.callbacks;

import org.deg.core.FileWithMetadata;
import org.deg.core.Peer;

import java.util.List;

/**
 * Interface that handles events while receiving files
 */
public interface FileReceivingEventHandler {
    /**
     * Is called when a new request for file transmission is received. Depending
     * on the return value transmission is allowed or denied.
     * @param files the incoming files' names
     * @param sender the sending peer
     * @return whether to accept this request
     */
    boolean onIncomingFiles(List<FileWithMetadata> files, Peer sender);

    /**
     * Is called when new bytes are received.
     * @param progress The total progress
     */
    void onReceivingProgress(Progress progress);

    /**
     * Is called when all files were transmitted
     * @param sender the sending peer
     * @param files the transmitted files
     */
    void onReceivingFinished(List<FileWithMetadata> files, Peer sender);

    /**
     * Is called whenever the transmission failed
     * @param e the exception that caused the failure
     */
    void onReceivingError(Exception e);
}

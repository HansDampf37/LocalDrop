package org.deg.core;

import java.io.File;

/**
 * Functional interface that handles events when a file is successfully received.
 */
@FunctionalInterface
public interface FileReceivedCallback {
    void handle(File file, Peer sender);
}

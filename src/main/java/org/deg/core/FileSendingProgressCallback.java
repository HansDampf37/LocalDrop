package org.deg.core;

/**
 * Functional interface that is used to report the progress when sending a file over a tcp connection
 */
@FunctionalInterface
public interface FileSendingProgressCallback {
    void handle(float progress);
}

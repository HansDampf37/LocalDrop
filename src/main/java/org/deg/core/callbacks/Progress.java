package org.deg.core.callbacks;

import org.deg.core.FileWithMetadata;

import java.util.List;

/**
 * The Progress while sending a list of files to another peer
 */
public class Progress {
    /**
     * The file that is currently being transmitted
     */
    public List<FileWithMetadata> files;
    /**
     * The number of bytes that have been transmitted
     */
    public long bytesTransmitted;
    /**
     * The total number of bytes for the entire transmission
     */
    public long totalBytes;
    /**
     * The number of files that have been transmitted
     */
    public int filesTransmitted;
    /**
     * The total number of files for the entire transmission
     */
    public long totalFiles;
    /**
     * An estimation of the current transmission speed
     */
    public long bitsPerSecondEstimation;

    public Progress(List<FileWithMetadata> files, long bytesTransmitted, long totalBytes, int filesTransmitted, int totalFiles) {
        this.files = files;
        this.bytesTransmitted = bytesTransmitted;
        this.totalBytes = totalBytes;
        this.filesTransmitted = filesTransmitted;
        this.totalFiles = totalFiles;
        this.bitsPerSecondEstimation = 0;
    }

    public float totalProgress() {
        return (float)((double) bytesTransmitted / (double) totalBytes);
    }

    public float remainingSecondsEstimation() {
        return (totalBytes - bytesTransmitted) / ((this.bitsPerSecondEstimation + 1) / 8f);
    }
}

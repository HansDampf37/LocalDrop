package org.deg.core.callbacks;

import org.deg.core.FileWithRelativePath;

/**
 * The Progress while sending a list of files to another peer
 */
public class Progress {
    /**
     * The file that is currently being transmitted
     */
    public FileWithRelativePath currentFile;
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
    public float megaBitsPerSecondEstimation;

    public Progress(FileWithRelativePath file, long bytesTransmitted, long totalBytes, int filesTransmitted, int totalFiles) {
        this.currentFile = file;
        this.bytesTransmitted = bytesTransmitted;
        this.totalBytes = totalBytes;
        this.filesTransmitted = filesTransmitted;
        this.totalFiles = totalFiles;
        this.megaBitsPerSecondEstimation = 0;
    }

    public float totalProgress() {
        return (float)((double) bytesTransmitted / (double) totalBytes);
    }
}

package org.deg.core;

import java.util.List;

/**
 * A simple data class to store metadata about sending files.
 */
public class Metadata {
    public int fileCount;
    public List<String> fileNames;
    public List<Long> fileSizes;
    public Peer sender;

    /**
     * Constructs a metadata object for a file.
     * @param fileCount The number of files.
     * @param fileNames The name of the file.
     * @param fileSizes The size of the file in bytes.
     * @param sender the sending peer
     */
    public Metadata(int fileCount, List<String> fileNames, List<Long> fileSizes, Peer sender) {
        this.fileCount = fileCount;
        this.fileNames = fileNames;
        this.fileSizes = fileSizes;
        this.sender = sender;
    }
}

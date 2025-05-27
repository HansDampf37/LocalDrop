package org.deg.core;

/**
 * A simple data class to store file metadata.
 */
public class Metadata {
    public String fileName;
    public long fileSize;

    /**
     * Constructs a metadata object for a file.
     * @param fileName The name of the file.
     * @param fileSize The size of the file in bytes.
     */
    public Metadata(String fileName, long fileSize) {
        this.fileName = fileName;
        this.fileSize = fileSize;
    }
}

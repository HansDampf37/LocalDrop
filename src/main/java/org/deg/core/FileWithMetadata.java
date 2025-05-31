package org.deg.core;

import java.io.File;

/**
 * A wrapper class around a file that includes metadata that is relevant in the context of sending.
 */
public class FileWithMetadata {
    /**
     * The wrapped file
     */
    public final File file;
    /**
     * The relative path which will be used on the receiver side
     */
    public final String relativePath;
    /**
     * The size of the file in bytes
     */
    public final long sizeInBytes;
    /**
     * Weather or not the file was transmitted successfully
     */
    public Boolean transmissionSuccess;

    public FileWithMetadata(File file, String relativePath, long sizeInBytes, Boolean transmissionSuccess) {
        this.file = file;
        this.relativePath = relativePath;
        this.sizeInBytes = sizeInBytes;
        this.transmissionSuccess = transmissionSuccess;
    }
}

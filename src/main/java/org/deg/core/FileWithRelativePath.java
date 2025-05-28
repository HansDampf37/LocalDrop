package org.deg.core;

import java.io.File;

public class FileWithRelativePath {
    public final File file;
    public final String relativePath;

    public FileWithRelativePath(File file, String relativePath) {
        this.file = file;
        this.relativePath = relativePath;
    }
}

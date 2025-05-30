package org.deg.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileExpander {

    /**
     * Expands folders into files and stores their relative paths for reconstruction.
     *
     * @param filesToSend List of files or directories
     * @return List of files with their relative paths
     */
    public static List<FileWithMetadata> expandFilesWithRelativePaths(List<File> filesToSend) {
        List<FileWithMetadata> result = new ArrayList<>();
        for (File file : filesToSend) {
            if (file.isDirectory()) {
                result.addAll(getFilesRecursively(file, file));
            } else {
                result.add(new FileWithMetadata(file, file.getName(), file.length()));
            }
        }
        return result;
    }

    private static List<FileWithMetadata> getFilesRecursively(File rootDir, File current) {
        List<FileWithMetadata> files = new ArrayList<>();
        File[] contents = current.listFiles();
        if (contents != null) {
            for (File file : contents) {
                if (file.isDirectory()) {
                    files.addAll(getFilesRecursively(rootDir, file));
                } else {
                    String relativePath = rootDir.getName() + File.separator + rootDir.toPath().relativize(file.toPath());
                    files.add(new FileWithMetadata(file, relativePath, file.length()));
                }
            }
        }
        return files;
    }
}

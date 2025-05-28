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
    public static List<FileWithRelativePath> expandFilesWithRelativePaths(List<File> filesToSend) {
        List<FileWithRelativePath> result = new ArrayList<>();
        for (File file : filesToSend) {
            if (file.isDirectory()) {
                result.addAll(getFilesRecursively(file, file));
            } else {
                result.add(new FileWithRelativePath(file, file.getName()));
            }
        }
        return result;
    }

    private static List<FileWithRelativePath> getFilesRecursively(File rootDir, File current) {
        List<FileWithRelativePath> files = new ArrayList<>();
        File[] contents = current.listFiles();
        if (contents != null) {
            for (File file : contents) {
                if (file.isDirectory()) {
                    files.addAll(getFilesRecursively(rootDir, file));
                } else {
                    String relativePath = rootDir.getName() + File.separator + rootDir.toPath().relativize(file.toPath());
                    files.add(new FileWithRelativePath(file, relativePath));
                }
            }
        }
        return files;
    }
}

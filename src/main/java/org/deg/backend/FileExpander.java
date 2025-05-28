package org.deg.backend;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileExpander {

    /**
     * Expands the input list of files by replacing any directories with their contained files.
     *
     * @param filesToSend List of files or directories
     * @return List containing only files (no directories)
     */
    public static List<File> expandFiles(List<File> filesToSend) {
        List<File> result = new ArrayList<>();
        for (File file : filesToSend) {
            if (file.isDirectory()) {
                result.addAll(getFilesRecursively(file));
            } else {
                result.add(file);
            }
        }
        return result;
    }

    /**
     * Recursively collects all files in a directory.
     *
     * @param directory The directory to search
     * @return List of files contained in the directory
     */
    private static List<File> getFilesRecursively(File directory) {
        List<File> files = new ArrayList<>();
        File[] contents = directory.listFiles();
        if (contents != null) {
            for (File file : contents) {
                if (file.isDirectory()) {
                    files.addAll(getFilesRecursively(file));
                } else {
                    files.add(file);
                }
            }
        }
        return files;
    }
}

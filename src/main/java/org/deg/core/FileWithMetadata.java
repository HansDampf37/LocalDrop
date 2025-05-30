package org.deg.core;

import java.io.File;

/**
 * A wrapper dataclass around a file that includes a relative path of this file
 * @param file The file
 * @param relativePath its relative path
 * @param sizeInBytes the file's size in bytes
 */
public record FileWithMetadata(File file, String relativePath, long sizeInBytes) {}

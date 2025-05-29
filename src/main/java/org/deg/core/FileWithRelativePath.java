package org.deg.core;

import java.io.File;

/**
 * A wrapper dataclass around a file that includes a relative path of this file
 * @param file The file
 * @param relativePath its relative path
 */
public record FileWithRelativePath(File file, String relativePath) {}

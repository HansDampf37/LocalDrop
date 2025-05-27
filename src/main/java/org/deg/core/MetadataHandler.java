package org.deg.core;

/**
 * Utility class for creating and parsing file metadata strings
 * used in file transfer over the network.
 */
public class MetadataHandler {

    /**
     * Parses a metadata string into a Metadata object.
     * Expected format: FILENAME:example.txt|SIZE:2048
     * @param metadataStr The metadata string.
     * @return Parsed Metadata object.
     */
    public static Metadata parseMetadata(String metadataStr) {
        String[] parts = metadataStr.split("\\|");
        String fileName = "";
        long fileSize = 0;

        for (String part : parts) {
            String[] keyVal = part.split(":", 2);
            if (keyVal.length == 2) {
                switch (keyVal[0]) {
                    case "FILENAME":
                        fileName = keyVal[1];
                        break;
                    case "SIZE":
                        fileSize = Long.parseLong(keyVal[1]);
                        break;
                }
            }
        }

        return new Metadata(fileName, fileSize);
    }

    /**
     * Builds a metadata string from a Metadata object.
     * @param metadata The metadata to convert.
     * @return Formatted string for transmission.
     */
    public static String buildMetadata(Metadata metadata) {
        return "FILENAME:" + metadata.fileName + "|SIZE:" + metadata.fileSize;
    }
}


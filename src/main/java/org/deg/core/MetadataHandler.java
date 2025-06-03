package org.deg.core;

import org.deg.backend.UserConfigurations;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class for creating and parsing metadata objects
 * used in file transfer over the network.
 */
public class MetadataHandler {

    /**
     * Parses a metadata string into a Metadata object.
     * Expected format:
     * FILE_COUNT:2|FILENAMES:file1.txt,file2.jpg|SIZES:12345,67890|SENDER_NAME:Alice|SENDER_IP:192.168.1.100|SENDER_PORT:9000
     *
     * @param metadataStr The metadata string.
     * @return Parsed Metadata object.
     */
    public static Metadata parseMetadata(String metadataStr) {
        String[] parts = metadataStr.split("\\|");
        int fileCount = 0;
        List<String> fileNames = new ArrayList<>();
        List<Long> fileSizes = new ArrayList<>();
        String senderName = "UnknownSenderName";
        String senderIP = "UnknownSenderIP";
        int senderPort = -1;
        String profilePicName = "UnknownProfilePicName";

        for (String part : parts) {
            String[] keyVal = part.split(":", 2);
            if (keyVal.length == 2) {
                switch (keyVal[0]) {
                    case "FILE_COUNT":
                        fileCount = Integer.parseInt(keyVal[1]);
                        break;
                    case "FILENAMES":
                        fileNames = Arrays.asList(keyVal[1].split(","));
                        break;
                    case "SIZES":
                        String[] sizesStr = keyVal[1].split(",");
                        for (String size : sizesStr) {
                            fileSizes.add(Long.parseLong(size));
                        }
                        break;
                    case "SENDER_NAME":
                        senderName = keyVal[1];
                        break;
                    case "SENDER_IP":
                        senderIP = keyVal[1];
                        break;
                    case "SENDER_PORT":
                        senderPort = Integer.parseInt(keyVal[1]);
                        break;
                    case "SENDER_PROFILE_PIC_NAME":
                        profilePicName = keyVal[1];
                        break;
                }
            }
        }

        return new Metadata(fileCount, fileNames, fileSizes, new Peer(senderName, senderIP, senderPort, profilePicName));
    }

    /**
     * Builds a metadata string from a Metadata object.
     * Format:
     * FILE_COUNT:2|FILENAMES:file1.txt,file2.jpg|SIZES:12345,67890|SENDER_NAME:Alice|SENDER_IP:192.168.1.100|SENDER_PORT:9000
     *
     * @param metadata The metadata to convert.
     * @return Formatted string for transmission.
     */
    public static String buildMetadata(Metadata metadata) {
        String fileNamesStr = String.join(",", metadata.fileNames);
        StringBuilder fileSizesStr = new StringBuilder();
        for (int i = 0; i < metadata.fileSizes.size(); i++) {
            fileSizesStr.append(metadata.fileSizes.get(i));
            if (i < metadata.fileSizes.size() - 1) {
                fileSizesStr.append(",");
            }
        }

        return "FILE_COUNT:" + metadata.fileCount +
                "|FILENAMES:" + fileNamesStr +
                "|SIZES:" + fileSizesStr +
                "|SENDER_NAME:" + metadata.sender.name() +
                "|SENDER_IP:" + metadata.sender.ip() +
                "|SENDER_PORT:" + metadata.sender.fileTransferPort() +
                "|SENDER_PROFILE_PIC_NAME:" + metadata.sender.profilePicName();
    }

    public static List<FileWithMetadata> buildFilesWithMetadataList(Metadata metadata) {
        List<FileWithMetadata> receivedFiles = new ArrayList<>();
        for (int i = 0; i < metadata.fileCount; i++) {
            String name = metadata.fileNames.get(i).replace("/", File.separator);
            File file = new File(UserConfigurations.DEFAULT_SAFE_PATH, name);
            long size = metadata.fileSizes.get(i);
            FileWithMetadata fileWithMetadata = new FileWithMetadata(file, name, size, null);
            receivedFiles.add(fileWithMetadata);
        }
        return receivedFiles;
    }
}

package org.deg.utils;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;

public class Utils {
    /**
     * Converts a byte count into a human-readable string using binary prefixes.
     * For example: 1024 → "1.0 KB", 1048576 → "1.0 MB"
     *
     * @param bytes The number of bytes
     * @return Human-readable string like "1.5 MB"
     */
    public static String bytesToReadableString(long bytes) {
        if (bytes < 1024) return bytes + " B";
        final String[] units = {"KB", "MB", "GB", "TB", "PB", "EB"};
        return getStringWithUnits(bytes, units);
    }

    /**
     * Converts a bit per second count into a human-readable string using binary prefixes.
     * For example: 1024 → "1.0 Kbit/s", 1048576 → "1.0 Mbit/s"
     *
     * @param bitsPerSecond The number of bytes
     * @return Human-readable string like "1.5 MB"
     */
    public static String bitsPerSecondToReadableString(long bitsPerSecond) {
        if (bitsPerSecond < 1024) return bitsPerSecond + " bit/s";
        final String[] units = {"Kbit/s", "Mbit/s", "Gbit/s"};
        return getStringWithUnits(bitsPerSecond, units);
    }

    private static String getStringWithUnits(long bitsPerSecond, String[] units) {
        double size = bitsPerSecond;
        int unitIndex = -1;

        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }

        return String.format("%.1f %s", size, units[unitIndex]);
    }

    public static void sendUDPBroadcast(String message, int port) {
        try (DatagramSocket sendSocket = new DatagramSocket()) {
            sendSocket.setBroadcast(true);
            byte[] data = message.getBytes();
            DatagramPacket packet = new DatagramPacket(
                    data, data.length,
                    InetAddress.getByName("255.255.255.255"), port
            );
            sendSocket.send(packet);
        } catch (IOException e) {
            System.err.println("Failed to send message: " + e.getMessage());
        }
    }

    /**
     * Calculates the total size of a directory by summing the sizes of all regular files
     * contained within it, including files in subdirectories.
     *
     * @param dir the directory whose size is to be calculated
     * @return the total size in bytes of all regular files within the directory
     * @throws IOException if an I/O error occurs accessing the file system
     * @throws IllegalArgumentException if the input is not a directory or doesn't exist
     */
    public static long getDirSize(File dir) throws IOException {
        if (dir == null || !dir.exists() || !dir.isDirectory()) {
            throw new IllegalArgumentException("Input must be an existing directory.");
        }

        try (var stream = Files.walk(dir.toPath())) {
            return stream
                    .filter(Files::isRegularFile)
                    .mapToLong(p -> {
                        try {
                            return Files.size(p);
                        } catch (IOException e) {
                            // Optionally log the unreadable file path here
                            return 0L;
                        }
                    })
                    .sum();
        }
    }
}

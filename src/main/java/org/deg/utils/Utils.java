package org.deg.utils;

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
}

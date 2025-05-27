package org.deg.core;

/**
 * Represents a peer in the LAN with a unique name and file transfer port.
 * A peer also has a receiver and can be exposed via a discovery listener.
 */
public record Peer(String name, String ip, int fileTransferPort) {

    /**
     * Returns a string representation of the peer (name, IP, and port).
     */
    @Override
    public String toString() {
        return name + " - " + ip + ":" + fileTransferPort;
    }

    /**
     * Generates a discovery response string for this peer.
     *
     * @return A string in the format "RESPONSE|name|ip|fileTransferPort".
     */
    public String toDiscoveryResponse() {
        return "RESPONSE|" + name + "|" + ip + "|" + fileTransferPort;
    }

    /**
     * Static factory method to create a Peer from a discovery response string.
     *
     * @param response The discovery response string.
     * @return A Peer object parsed from the response.
     */
    public static Peer fromDiscoveryResponse(String response) {
        String[] parts = response.split("\\|");
        if (parts.length == 4) {
            String name = parts[1];
            String ip = parts[2];
            int port = Integer.parseInt(parts[3]);
            return new Peer(name, ip, port);
        }
        return null;
    }
}

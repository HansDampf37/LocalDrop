package org.deg.core;

import static org.deg.core.Constants.*;

/**
 * Represents a peer in the LAN with a unique name and file transfer port.
 * A peer also has a receiver and can be exposed via a discovery listener.
 */
public record Peer(String name, String ip, int fileTransferPort, String profilePicName) {

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
     * @return A string in the format "DISCOVERY_RESPONSE|name|ip|fileTransferPort".
     */
    public String toDiscoveryResponse() {
        return DISCOVERY_RESPONSE + "|" + name + "|" + ip + "|" + fileTransferPort + "|" + profilePicName;
    }

    /**
     * Generates a hello message string for this peer.
     *
     * @return A string in the format "HELLO|name|ip|fileTransferPort".
     */
    public String toHelloMessage() {
        return HELLO + "|" + name + "|" + ip + "|" + fileTransferPort + "|" + profilePicName;
    }

    /**
     * Generates a bye message string for this peer.
     *
     * @return A string in the format "BYE|name|ip|fileTransferPort".
     */
    public String toByeMessage() {
        return BYE + "|" + name + "|" + ip + "|" + fileTransferPort + "|" + profilePicName;
    }

    /**
     * Static factory method to create a Peer from a discovery response string.
     *
     * @param response The discovery response string.
     * @return A Peer object parsed from the response.
     */
    public static Peer fromDiscoveryResponse(String response) {
        String[] parts = response.split("\\|");
        if (parts.length == 5) {
            String name = parts[1];
            String ip = parts[2];
            int port = Integer.parseInt(parts[3]);
            String profilePicName = parts[4];
            return new Peer(name, ip, port, profilePicName);
        }
        return null;
    }

    /**
     * Static factory method to create a Peer from a hello message string.
     *
     * @param message The hello message.
     * @return A Peer object parsed from the message.
     */
    public static Peer fromHelloMessage(String message) {
        return fromDiscoveryResponse(message);
    }

    /**
     * Static factory method to create a Peer from a bye message string.
     *
     * @param message The bye message.
     * @return A Peer object parsed from the message.
     */
    public static Peer fromByeMessage(String message) {
        return fromHelloMessage(message);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Peer otherPeer) {
            return ip().equals(otherPeer.ip()) && fileTransferPort() == otherPeer.fileTransferPort();
        }
        return false;
    }
}

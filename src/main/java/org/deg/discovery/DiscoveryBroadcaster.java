package org.deg.discovery;

import org.deg.core.Peer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

/**
 * Sends a UDP broadcast message on the local network to discover peers.
 */
public class DiscoveryBroadcaster {

    private static final int DISCOVERY_PORT = 8888;
    private static final String DISCOVERY_REQUEST = "DISCOVER_REQUEST";

    /**
     * Sends a UDP broadcast request and waits for responses.
     * @param localPeer this apps peer so prevent the discovery process from discovering itself.
     * @return List of discovered peers with IP and port
     */
    public List<Peer> discoverPeers(Peer localPeer) {
        List<Peer> peers = new ArrayList<>();

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(1000); // 1 second timeout
            socket.setBroadcast(true);

            byte[] requestData = DISCOVERY_REQUEST.getBytes();
            DatagramPacket packet = new DatagramPacket(
                    requestData, requestData.length,
                    InetAddress.getByName("255.255.255.255"), DISCOVERY_PORT
            );
            socket.send(packet);

            // Collect responses
            byte[] buffer = new byte[1024];
            long endTime = System.currentTimeMillis() + 1000;

            while (System.currentTimeMillis() < endTime) {
                try {
                    DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                    socket.receive(response);

                    String message = new String(response.getData(), 0, response.getLength());
                    if (message.startsWith("RESPONSE|")) { // TODO add hello message on start
                        Peer peer = Peer.fromDiscoveryResponse(message);
                        if (peer != null && !isSelf(peer, localPeer)) {
                            peers.add(peer);
                        }
                    }
                } catch (SocketTimeoutException ignored) {}
            }

        } catch (IOException e) {
            System.err.println("Discovery error: " + e.getMessage());
        }

        return peers;
    }

    private boolean isSelf(Peer discovered, Peer local) {
        return discovered.ip().equals(local.ip()) && discovered.fileTransferPort() == local.fileTransferPort();
    }
}

package org.deg.discovery;

import org.deg.core.Peer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import static org.deg.core.Settings.DISCOVERY_PORT;
import static org.deg.core.Settings.DISCOVERY_REQUEST;
import static org.deg.core.Settings.DISCOVERY_RESPONSE;

/**
 * Sends a UDP broadcast message on the local network. Waits for them to respond and returns a list of discovered peers.
 */
public class DiscoveryBroadcaster {
    /**
     * Sends a UDP broadcast request and waits for responses.
     * @param localPeer this apps peer so prevent the discovery process from discovering itself.
     * @param timeout the number of milliseconds to wait for peers to respond.
     * @return List of discovered peers with IP and port
     */
    public List<Peer> discoverPeers(Peer localPeer, int timeout) {
        List<Peer> peers = new ArrayList<>();

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(timeout);
            socket.setBroadcast(true);

            byte[] requestData = DISCOVERY_REQUEST.getBytes();
            DatagramPacket packet = new DatagramPacket(
                    requestData,
                    requestData.length,
                    InetAddress.getByName("255.255.255.255"),
                    DISCOVERY_PORT
            );
            socket.send(packet);

            // Collect responses
            byte[] buffer = new byte[1024];
            long endTime = System.currentTimeMillis() + timeout;

            while (System.currentTimeMillis() < endTime) {
                try {
                    DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                    socket.receive(response);

                    String message = new String(response.getData(), 0, response.getLength());
                    if (message.startsWith(DISCOVERY_RESPONSE + "|")) {
                        Peer peer = Peer.fromDiscoveryResponse(message);
                        if (peer != null && !peer.equals(localPeer)) {
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
}

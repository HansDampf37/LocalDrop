package org.deg.discovery;

import org.deg.core.Peer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import static org.deg.core.Settings.DISCOVERY_PORT;
import static org.deg.core.Settings.DISCOVERY_REQUEST;

/**
 * Listens for UDP broadcast discovery requests and responds with peer info.
 */
public class DiscoveryListener implements Runnable {
    private volatile boolean running = false;
    private final Peer peer;
    private DatagramSocket socket;

    public DiscoveryListener(Peer peer) {
        this.peer = peer;
    }

    @Override
    public void run() {
        answerToIncomingDiscoveryRequests();
    }

    private void answerToIncomingDiscoveryRequests() {
        try {
            socket = new DatagramSocket(DISCOVERY_PORT, InetAddress.getByName("0.0.0.0"));
            socket.setBroadcast(true);

            byte[] buffer = new byte[1024];
            running = true;
            while (running) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                try {
                    socket.receive(packet);
                } catch (SocketException e) {
                    if (!running) break;
                    else throw e;
                }
                String message = new String(packet.getData(), 0, packet.getLength());
                if (DISCOVERY_REQUEST.equals(message)) {
                    String response = peer.toDiscoveryResponse();
                    byte[] responseData = response.getBytes();

                    DatagramPacket responsePacket = new DatagramPacket(
                            responseData, responseData.length,
                            packet.getAddress(), packet.getPort()
                    );

                    socket.send(responsePacket);
                }
            }

        } catch (IOException e) {
            System.err.println("DiscoveryListener error: " + e.getMessage());
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }

    public void stop() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            socket.close(); // This unblocks receive()
        }
    }
}

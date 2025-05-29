package org.deg.discovery;

import org.deg.core.Peer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.function.Consumer;

import static org.deg.Settings.*;

/**
 * Listens for UDP broadcast hello requests.
 * On start also sends a hello udp broadcast into the network.
 */
public class HelloListener implements Runnable {
    private boolean running = false;
    private final Consumer<Peer> onNewPeer;
    private final Peer localPeer;

    public HelloListener(Peer peer, Consumer<Peer> onNewPeerCallback) {
        this.localPeer = peer;
        onNewPeer = onNewPeerCallback;
    }

    @Override
    public void run() {
        sendHelloMessage();
        listenToIncomingHelloMessages();
    }

    private void listenToIncomingHelloMessages() {
        try (DatagramSocket socket = new DatagramSocket(HELLO_PORT, InetAddress.getByName("0.0.0.0"))) {
            socket.setBroadcast(true);

            byte[] buffer = new byte[1024];
            running = true;
            while (running) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());
                if (message.equals(HELLO)) {
                    Peer peer = Peer.fromHelloMessage(message);
                    if (peer != null && !peer.equals(localPeer)) {
                        onNewPeer.accept(peer);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Hello listener error: " + e.getMessage());
        }
    }

    private void sendHelloMessage() {
        try (DatagramSocket socket = new DatagramSocket(HELLO_PORT, InetAddress.getByName("0.0.0.0"))) {
            socket.setBroadcast(true);
            byte[] requestData = localPeer.toHelloMessage().getBytes();
            DatagramPacket packet = new DatagramPacket(
                    requestData, requestData.length,
                    InetAddress.getByName("255.255.255.255"), HELLO_PORT
            );
            socket.send(packet);
        } catch (IOException e) {
            System.err.println("Hello message could not be sent: " + e.getMessage());
        }
    }

    public void stop() {
        running = false;
    }
}


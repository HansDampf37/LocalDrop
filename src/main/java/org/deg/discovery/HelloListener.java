package org.deg.discovery;

import org.deg.core.Peer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.function.Consumer;

import static org.deg.core.Constants.*;
import static org.deg.utils.Utils.sendUDPBroadcast;

/**
 * On start, sends a hello UDP broadcast into the network.
 * Afterward, continuously listens for hello/bye messages sent by other peers as UDP broadcast.
 * On stop, sends a bye UDP broadcast into the network.
 */
public class HelloListener implements Runnable {
    private volatile boolean running = false;
    private final Peer localPeer;
    private Consumer<Peer> onNewPeer;
    private Consumer<Peer> onPeerDisconnected;
    private DatagramSocket socket;

    public HelloListener(Peer peer, Consumer<Peer> onNewPeerCallback, Consumer<Peer> onPeerDisconnectedCallback) {
        localPeer = peer;
        onNewPeer = onNewPeerCallback;
        onPeerDisconnected = onPeerDisconnectedCallback;
    }

    /**
     * Sends a hello UDP broadcast into the network.
     * Afterward, continuously listens for hello/bye messages sent by other peers as UDP broadcast.
     * When the stop method is called this listening is interrupted and a bye UDP broadcast is sent
     * into the network.
     */
    @Override
    public void run() {
        // send hello message
        sendUDPBroadcast(localPeer.toHelloMessage(), HELLO_PORT);
        try {
            listenForMessages();
        } finally {
            // send bye message
            sendUDPBroadcast(localPeer.toByeMessage(), HELLO_PORT);
        }
    }

    private void listenForMessages() {
        try {
            socket = new DatagramSocket(HELLO_PORT, InetAddress.getByName("0.0.0.0"));
            socket.setBroadcast(true);
            running = true;

            byte[] buffer = new byte[1024];

            while (running) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                try {
                    socket.receive(packet);
                } catch (SocketException e) {
                    if (!running) break;
                    else throw e;
                }
                String message = new String(packet.getData(), 0, packet.getLength());
                handleIncomingMessage(message);
            }
        } catch (IOException e) {
            System.err.println("HelloListener socket error: " + e.getMessage());
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }

    private void handleIncomingMessage(String message) {
        if (message.startsWith(HELLO)) {
            Peer peer = Peer.fromHelloMessage(message);
            if (peer != null && !peer.equals(localPeer)) {
                onNewPeer.accept(peer);
            }
        } else if (message.startsWith(BYE)) {
            Peer peer = Peer.fromByeMessage(message);
            if (peer != null && !peer.equals(localPeer)) {
                onPeerDisconnected.accept(peer);
            }
        }
    }

    /**
     * Stops this listener.
     * A bye message is sent to other peers in the network before this thread completes its run message.
     */
    public void stop() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    /**
     * Sets a callback that is called when a new hello message is received by another peer
     * @param onNewPeer the callback
     */
    public void setOnNewPeerCallback(Consumer<Peer> onNewPeer) {
        this.onNewPeer = onNewPeer;
    }

    /**
     * Sets a callback that is called when a new bye message is received by another peer
     * @param onPeerDisconnected the other peer
     */
    public void setOnPeerDisconnectedCallback(Consumer<Peer> onPeerDisconnected) {
        this.onPeerDisconnected = onPeerDisconnected;
    }
}

package org.deg.backend;

import org.deg.core.FileReceivedCallback;
import org.deg.core.FileReceiver;
import org.deg.core.FileSender;
import org.deg.core.Peer;
import org.deg.discovery.DiscoveryBroadcaster;
import org.deg.discovery.DiscoveryListener;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.Random;

/**
 * Handles backend operations for LAN file sharing, including peer initialization,
 * dynamic port allocation, and lifecycle control of file receiver and discovery listener.
 */
public class Backend {
    public final Peer localPeer;
    private final FileReceiver fileReceiver;
    private final DiscoveryListener discoveryListener;
    private Thread receiverThread;
    private Thread discoveryListenerThread;

    /**
     * Constructs a backend with a unique peer name, dynamic port, and LAN-compatible IP.
     *
     * @throws IOException if no suitable IP address or port can be found.
     */
    public Backend() throws IOException {
        String peerName = "Peer-" + new Random().nextInt(1000);
        String localIp = findLanAddress();
        int fileTransferPort = findFreePort();

        localPeer = new Peer(peerName, localIp, fileTransferPort);
        fileReceiver = new FileReceiver(fileTransferPort);
        discoveryListener = new DiscoveryListener(localPeer);
    }

    /**
     * Starts the backend by launching file receiver and discovery listener in background threads.
     */
    public void start() {
        receiverThread = new Thread(fileReceiver);
        receiverThread.setDaemon(true);
        receiverThread.start();

        discoveryListenerThread = new Thread(discoveryListener);
        discoveryListenerThread.setDaemon(true);
        discoveryListenerThread.start();
    }

    /**
     * Stops the backend gracefully. Currently, threads are daemonized and terminate with the app.
     */
    public void stop() throws InterruptedException {
        fileReceiver.stop();
        discoveryListener.stop();

        receiverThread.join(1000);
        discoveryListenerThread.join(1000);
    }

    public List<Peer> discoverPeers() {
        return new DiscoveryBroadcaster().discoverPeers(localPeer);
    }

    /**
     * Attempts to find a LAN-compatible IP address (not loopback or link-local).
     */
    private String findLanAddress() throws SocketException {
        for (NetworkInterface ni : java.util.Collections.list(NetworkInterface.getNetworkInterfaces())) {
            if (!ni.isUp() || ni.isLoopback()) continue;
            for (InetAddress addr : java.util.Collections.list(ni.getInetAddresses())) {
                if (addr instanceof Inet4Address &&
                        !addr.isLoopbackAddress() &&
                        !addr.isLinkLocalAddress()) {
                    return addr.getHostAddress();
                }
            }
        }
        throw new SocketException("No suitable LAN IP found");
    }

    /**
     * Finds a free port dynamically.
     */
    private int findFreePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    /**
     * Sends a file to another peer
     * @param ip the other peer's ip
     * @param port the port
     * @param file the file
     */
    public void sendFile(String ip, int port, File file) {
        new FileSender(ip, port, file).send();
    }

    public void onFileReceived(FileReceivedCallback callback) {
        fileReceiver.onFileReceived(callback);
    }
}

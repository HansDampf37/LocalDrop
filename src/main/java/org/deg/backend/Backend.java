package org.deg.backend;

import org.deg.core.FileReceiver;
import org.deg.core.FileSender;
import org.deg.core.Peer;
import org.deg.core.callbacks.FileReceivingEventHandler;
import org.deg.core.callbacks.FileSendingEventHandler;
import org.deg.discovery.DiscoveryBroadcaster;
import org.deg.discovery.DiscoveryListener;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Handles backend operations for LAN file sharing, including peer initialization,
 * dynamic port allocation, and lifecycle control of file receiver and discovery listener.
 */
public class Backend {
    public final Peer localPeer;
    private final FileReceiver fileReceiver;
    private final DiscoveryListener discoveryListener;
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    /**
     * Constructs a backend with a unique peer name, dynamic port, and LAN-compatible IP.
     *
     * @throws IOException if no suitable IP address or port can be found.
     */
    public Backend() throws IOException {
        String peerName = UserConfigurations.USERNAME;
        String localIp = findLanAddress();
        int fileTransferPort = findFreePort();

        localPeer = new Peer(peerName, localIp, fileTransferPort);
        fileReceiver = new FileReceiver(fileTransferPort, UserConfigurations.DEFAULT_SAFE_PATH);
        discoveryListener = new DiscoveryListener(localPeer);
    }

    /**
     * Starts the backend by launching file receiver and discovery listener in background threads.
     */
    public void start() {
        Thread receiverThread = new Thread(fileReceiver);
        receiverThread.start();

        Thread discoveryListenerThread = new Thread(discoveryListener);
        discoveryListenerThread.start();
    }

    /**
     * Stops the backend gracefully. Currently, threads are daemonized and terminate with the app.
     */
    public void stop() {
        fileReceiver.stop();
        discoveryListener.stop();
        executor.shutdown();
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
     * @param sender the sending peer
     * @param receiver the receiving peer
     * @param file the file
     */
    public void startFileTransfer(Peer sender, Peer receiver, File file, FileSendingEventHandler callback) {
        executor.submit(() -> new FileSender(sender, receiver, file).send(callback));
    }

    /**
     * Adds a handler that is called whenever a file is received
     *
     * @param handler the handler
     * @see FileReceivingEventHandler
     */
    public void setFileReceivedHandler(FileReceivingEventHandler handler) {
        fileReceiver.setEventHandler(handler);
    }

    /**
     * Sends all files over a tcp connection to the receiver
     * @param sender the sending peer
     * @param receiver the receiving peer
     * @param filesToSend the list of files to send
     * @param handler the handler for sending events
     */
    public void startFilesTransfer(Peer sender, Peer receiver, List<File> filesToSend, FileSendingEventHandler handler) {
        startFileTransfer(sender, receiver, filesToSend.getFirst(), handler);
    }
}

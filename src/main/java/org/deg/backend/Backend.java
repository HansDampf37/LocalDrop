package org.deg.backend;

import javafx.util.Pair;
import org.deg.core.FileReceiver;
import org.deg.core.FileSender;
import org.deg.core.Peer;
import org.deg.core.SendingDeniedException;
import org.deg.core.callbacks.FileReceivingEventHandler;
import org.deg.core.callbacks.FileSendingEventHandler;
import org.deg.discovery.DiscoveryBroadcaster;
import org.deg.discovery.DiscoveryListener;
import org.deg.discovery.HelloListener;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Handles backend operations for LAN file sharing, including peer initialization,
 * dynamic port allocation, and lifecycle control of file receiver and discovery listener.
 */
public class Backend {
    public final Peer localPeer;
    private final FileReceiver fileReceiver;
    private final DiscoveryListener discoveryListener;
    private final HelloListener helloListener;
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private final List<Pair<Peer, File>> sentLog = new ArrayList<>();

    /**
     * Constructs a backend with a unique peer name, dynamic port, and LAN-compatible IP.
     *
     * @throws IOException if no suitable IP address or port can be found.
     */
    public Backend() throws IOException {
        UserConfigurations.loadConfigurations();
        String peerName = UserConfigurations.USERNAME;
        String localIp = findLanAddress();
        int fileTransferPort = findFreePort();

        localPeer = new Peer(peerName, localIp, fileTransferPort);
        fileReceiver = new FileReceiver(fileTransferPort, UserConfigurations.DEFAULT_SAFE_PATH);
        discoveryListener = new DiscoveryListener(localPeer);
        helloListener = new HelloListener(localPeer, null, null);
    }

    /**
     * Starts the backend by launching file receiver and hallo + discovery listener in background threads.
     */
    public void start() {
        Thread receiverThread = new Thread(fileReceiver);
        receiverThread.start();

        Thread discoveryListenerThread = new Thread(discoveryListener);
        discoveryListenerThread.start();

        Thread helloListenerThread = new Thread(helloListener);
        helloListenerThread.start();
    }

    /**
     * Stops the backend gracefully.
     */
    public void stop() {
        fileReceiver.stop();
        discoveryListener.stop();
        helloListener.stop();
        executor.shutdown();
    }

    public List<Peer> discoverPeers() {
        return new DiscoveryBroadcaster().discoverPeers(localPeer, 2000);
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
     * Sends all files over a tcp connection to the receiver. The files can be files and directories.
     * @param sender the sending peer
     * @param receiver the receiving peer
     * @param filesToSend the list of files to send
     * @param handler the handler for sending events
     */
    public void startFilesTransfer(Peer sender, Peer receiver, List<File> filesToSend, FileSendingEventHandler handler) {
        executor.submit(() -> {
            try {
                new FileSender(sender, receiver, filesToSend).send(handler);
            } catch (SendingDeniedException e) {
                System.out.println("Sending denied");
            }
            for (File file : filesToSend) sentLog.add(new Pair<>(receiver, file));
        });
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
     * Adds a callback that is called whenever a new peer is discovered using the hello protocol.
     * @param onNewPeer the callback
     */
    public void setOnNewPeerCallback(Consumer<Peer> onNewPeer) {
        this.helloListener.setOnNewPeerCallback(onNewPeer);
    }

    /**
     * Adds a callback that is called whenever a new peer sends a bye message.
     * @param onPeerDisconnected the callback
     */
    public void setOnPeerDisconnectedCallback(Consumer<Peer> onPeerDisconnected) {
        this.helloListener.setOnPeerDisconnectedCallback(onPeerDisconnected);
    }

    public List<Pair<Peer, File>> getSentLog() {
        return sentLog;
    }

    public List<Pair<Peer, File>> getReceivedLog() {
        return fileReceiver.getReceivedLog();
    }
}

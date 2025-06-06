package org.deg.core;

import javafx.util.Pair;
import org.deg.core.callbacks.FileReceivingEventHandler;
import org.deg.core.callbacks.Progress;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import static org.deg.core.Constants.ACCEPT_TRANSMISSION_REQUEST;
import static org.deg.core.Constants.DENY_TRANSMISSION_REQUEST;

/**
 * The FileReceiver class listens on a given port for incoming file transfer requests,
 * receives metadata and file content, and stores the received file locally.
 */
public class FileReceiver implements Runnable {
    private final int port;
    private boolean running = false;
    private ServerSocket serverSocket;
    private FileReceivingEventHandler callback = null;
    private final List<Pair<Peer, File>> receivedLog = new ArrayList<>();

    /**
     * Constructs a FileReceiver to listen on a specific port.
     *
     * @param port The TCP port to listen on.
     */
    public FileReceiver(int port) {
        this.port = port;
    }

    /**
     * Starts the receiver, waits for a connection, and processes the file transfer.
     */
    @Override
    public void run() {
        running = true;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Receiver listening on port " + port + "...");
            while (running) {
                Socket socket;
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    if (!running) break;
                    throw e;
                }

                try (DataInputStream dis = new DataInputStream(socket.getInputStream());
                     DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {

                    // Step 1: Read metadata
                    int length = dis.readInt();
                    byte[] data = new byte[length];
                    dis.readFully(data);
                    String metadataStr = new String(data, StandardCharsets.UTF_8);
                    Metadata metadata = MetadataHandler.parseMetadata(metadataStr);
                    List<FileWithMetadata> receivedFiles = MetadataHandler.buildFilesWithMetadataList(metadata);
                    System.out.println("Transmission request received from " + metadata.sender.name() + ", Filename: " + metadata.fileNames);

                    // Step 2: Accept or deny
                    if (callback == null || callback.onIncomingFiles(receivedFiles, metadata.sender)) {
                        dos.writeUTF(ACCEPT_TRANSMISSION_REQUEST);
                        System.out.println("Accept transmission request");
                    } else {
                        dos.writeUTF(DENY_TRANSMISSION_REQUEST);
                        System.out.println("Deny transmission request");
                        continue;
                    }

                    // Step 3: Receive files
                    System.out.println("Start receiving of files " + receivedFiles.stream().map(f -> f.file.getName()).toList());
                    int totalBytesReceived = 0;
                    long startTime = System.currentTimeMillis();
                    long totalBytes = metadata.fileSizes.stream().mapToLong(Long::longValue).sum();
                    DataInputStream compressedDataInputStream = new DataInputStream(new GZIPInputStream(socket.getInputStream()));
                    for (int i = 0; i < receivedFiles.size(); i++) {
                        FileWithMetadata fileWithMetadata = receivedFiles.get(i);
                        File file = fileWithMetadata.file;
                        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
                        if (!file.exists()) {
                            try {
                                file.createNewFile();
                            } catch (IOException e) {
                                callback.onReceivingError(e);
                                fileWithMetadata.transmissionSuccess = false;
                                continue;
                            }
                        }

                        try (FileOutputStream fos = new FileOutputStream(file)) {
                            byte[] buffer = new byte[4096];
                            long remaining = fileWithMetadata.sizeInBytes;
                            int bytesRead;
                            while (remaining > 0 && (bytesRead = compressedDataInputStream.read(buffer, 0, (int) Math.min(buffer.length, remaining))) != -1) {
                                fos.write(buffer, 0, bytesRead);
                                remaining -= bytesRead;
                                totalBytesReceived += bytesRead;

                                if (callback != null) {
                                    Progress progress = new Progress(receivedFiles, totalBytesReceived, totalBytes, i, receivedFiles.size());
                                    long durationSoFar = System.currentTimeMillis() - startTime;
                                    float totalTimeInSeconds = durationSoFar / 1000f;
                                    progress.bitsPerSecondEstimation = (long) (totalBytesReceived * 8L / totalTimeInSeconds);
                                    callback.onReceivingProgress(progress);
                                }
                            }
                            System.out.println("Finished receiving file: " + file.getAbsolutePath());
                            receivedLog.add(new Pair<>(metadata.sender, file));
                            fileWithMetadata.transmissionSuccess = true;
                        } catch (IOException e) {
                            fileWithMetadata.transmissionSuccess = false;
                        }
                    }

                    System.out.println("All files received successfully");
                    if (callback != null) callback.onReceivingFinished(receivedFiles, metadata.sender);

                } catch (IOException e) {
                    System.err.println("Error during file reception: " + e.getMessage());
                    if (callback != null) callback.onReceivingError(e);
                }
            }
        } catch (IOException e) {
            System.err.println("Receiver error: " + e.getMessage());
            if (callback != null) callback.onReceivingError(e);
        } finally {
            try {
                if (serverSocket != null && !serverSocket.isClosed()) serverSocket.close();
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * Stops the receiver and unblocks any waiting operations.
     */
    public void stop() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close(); // unblocks accept()
            }
        } catch (IOException e) {
            System.err.println("Error while closing server socket: " + e.getMessage());
        }
    }

    /**
     * Sets a callback method that is called as soon as a file is received.
     *
     * @param callback the callback method
     * @see FileReceivingEventHandler
     */
    public void setEventHandler(FileReceivingEventHandler callback) {
        this.callback = callback;
    }

    public List<Pair<Peer, File>> getReceivedLog() {
        return receivedLog;
    }
}

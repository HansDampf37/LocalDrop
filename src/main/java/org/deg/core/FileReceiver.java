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

import static org.deg.Settings.ACCEPT_TRANSMISSION_REQUEST;
import static org.deg.Settings.DENY_TRANSMISSION_REQUEST;

/**
 * The FileReceiver class listens on a given port for incoming file transfer requests,
 * receives metadata and file content, and stores the received file locally.
 */
public class FileReceiver implements Runnable {
    private final int port;
    private boolean running = false;
    private FileReceivingEventHandler callback = null;
    private final File defaultSaveDirectory;
    private final List<Pair<Peer, File>> receivedLog = new ArrayList<>();

    /**
     * Constructs a FileReceiver to listen on a specific port.
     *
     * @param port The TCP port to listen on.
     */
    public FileReceiver(int port, File defaultSaveDirectory) {
        this.port = port;
        this.defaultSaveDirectory = defaultSaveDirectory;
    }

    /**
     * Starts the receiver, waits for a connection, and processes the file transfer.
     */
    @Override
    public void run() {
        running = true;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Receiver listening on port " + port + "...");
            while (running) {
                Socket socket = serverSocket.accept();
                DataInputStream dis = new DataInputStream(socket.getInputStream());

                // Step 1: Read metadata
                int length = dis.readInt();
                byte[] data = new byte[length];
                dis.readFully(data);
                String metadataStr = new String(data, StandardCharsets.UTF_8);
                Metadata metadata = MetadataHandler.parseMetadata(metadataStr);
                System.out.println("Transmission request received from " + metadata.sender.name() + ", Filename: " + metadata.fileNames);

                // Step 2: Accept transmission
                List<FileWithRelativePath> outputFiles = metadata.fileNames.stream().map(
                        (name) -> new FileWithRelativePath(new File(defaultSaveDirectory, name), name)
                ).toList();
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                if (callback == null || callback.onIncomingFiles(outputFiles, metadata.sender)) {
                    dos.writeUTF(ACCEPT_TRANSMISSION_REQUEST);
                    System.out.println("Accept transmission request");
                } else {
                    dos.writeUTF(DENY_TRANSMISSION_REQUEST);
                    System.out.println("Deny transmission request");
                    continue;
                }

                // Step 3: Receive transmission
                System.out.println("Start receiving of files " + outputFiles.stream().map((FileWithRelativePath f) -> f.file().getName()).toList());
                int totalBytesReceived = 0;
                long startTime = System.currentTimeMillis();
                long totalBytes = metadata.fileSizes.stream().mapToLong(Long::longValue).sum();
                for (int i = 0; i < outputFiles.size(); i++) {
                    long fileSize = metadata.fileSizes.get(i);
                    FileWithRelativePath outputFile = outputFiles.get(i);
                    if (!outputFile.file().getParentFile().exists()) outputFile.file().getParentFile().mkdirs();
                    if (!outputFile.file().exists()) outputFile.file().createNewFile();

                    try (FileOutputStream fos = new FileOutputStream(outputFile.file())) {
                        byte[] buffer = new byte[4096];
                        long remaining = fileSize;
                        int bytesRead;
                        while (remaining > 0 && (bytesRead = dis.read(buffer, 0, (int) Math.min(buffer.length, remaining))) != -1) {
                            fos.write(buffer, 0, bytesRead);
                            remaining -= bytesRead;
                            totalBytesReceived += bytesRead;
                            if (callback != null) {
                                Progress progress = new Progress(
                                        outputFile,
                                        totalBytesReceived,
                                        totalBytes,
                                        i,
                                        outputFiles.size()
                                );
                                long durationSoFar = System.currentTimeMillis() - startTime;
                                float totalTimeInSeconds = durationSoFar / 1000f;
                                progress.bitsPerSecondEstimation = (long)(totalBytesReceived * 8L / totalTimeInSeconds);
                                callback.onReceivingProgress(progress);
                            }
                        }
                    }
                    System.out.println("Finished receiving of file " + outputFile.file().getAbsolutePath());
                    receivedLog.add(new Pair<>(metadata.sender, outputFile.file()));
                    if (callback != null) callback.onReceivingFinished(outputFile, metadata.sender);
                }
                System.out.println("All files received successfully");
                if (callback != null) callback.onReceivingFinished(metadata.sender);
            }
        } catch (IOException e) {
            System.err.println("Receiver error: " + e.getMessage());
            if (callback != null) {
                callback.onReceivingFailed(e);
            }
        }
    }

    public void stop() {
        running = false;
    }

    /**
     * Adds a callback-method that is called as soon as a file is received.
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


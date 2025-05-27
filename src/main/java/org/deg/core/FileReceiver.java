package org.deg.core;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * The FileReceiver class listens on a given port for incoming file transfer requests,
 * receives metadata and file content, and stores the received file locally.
 */
public class FileReceiver implements Runnable {

    private final int port;
    private boolean running = false;
    private final List<FileReceivedCallback> callbacks = new ArrayList<>();

    /**
     * Constructs a FileReceiver to listen on a specific port.
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
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Receiver listening on port " + port + "...");
            while (running) {
                Socket socket = serverSocket.accept();
                System.out.println("Connection received from " + socket.getInetAddress());

                DataInputStream dis = new DataInputStream(socket.getInputStream());

                // Step 1: Read metadata
                String metadataStr = dis.readUTF();
                Metadata metadata = MetadataHandler.parseMetadata(metadataStr);
                System.out.println("Receiving file: " + metadata.fileName + " (" + metadata.fileSize + " bytes)");

                // Step 2: Receive file
                File outputFile = new File("received_" + metadata.fileName);
                try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                    byte[] buffer = new byte[4096];
                    long remaining = metadata.fileSize;
                    int bytesRead;
                    while (remaining > 0 && (bytesRead = dis.read(buffer, 0, (int) Math.min(buffer.length, remaining))) != -1) {
                        fos.write(buffer, 0, bytesRead);
                        remaining -= bytesRead;
                    }
                    for (FileReceivedCallback callback : callbacks) callback.handle(outputFile, metadata.sender);
                    System.out.println("File saved as: " + outputFile.getAbsolutePath());
                }
            }

        } catch (IOException e) {
            System.err.println("Receiver error: " + e.getMessage());
        }
    }

    public void stop() {
        running = false;
    }

    /**
     * Adds a callback-method that is called as soon as a file is received.
     * @see FileReceivedCallback
     * @param callback the callback method
     */
    public void onFileReceived(FileReceivedCallback callback) {
        callbacks.add(callback);
    }
}


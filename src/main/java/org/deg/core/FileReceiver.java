package org.deg.core;

import org.deg.core.callbacks.FileReceivingEventHandler;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The FileReceiver class listens on a given port for incoming file transfer requests,
 * receives metadata and file content, and stores the received file locally.
 */
public class FileReceiver implements Runnable {

    private final int port;
    private boolean running = false;
    private FileReceivingEventHandler callback = null;
    private final File defaultSaveDirectory;

    /**
     * Constructs a FileReceiver to listen on a specific port.
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
                System.out.println("Connection received from " + socket.getInetAddress());

                DataInputStream dis = new DataInputStream(socket.getInputStream());

                // Step 1: Read metadata
                String metadataStr = dis.readUTF();
                Metadata metadata = MetadataHandler.parseMetadata(metadataStr);
                System.out.println("Receiving file: " + metadata.fileName + " (" + metadata.fileSize + " bytes)");

                // Step 2: Receive file
                File outputFile = new File(defaultSaveDirectory, metadata.fileName);
                if (!outputFile.exists()) outputFile.createNewFile();
                if (callback == null || callback.onIncomingFile(outputFile, metadata.sender)) {
                    try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                        byte[] buffer = new byte[4096];
                        long remaining = metadata.fileSize;
                        int bytesRead;
                        int totalBytesRead = 0;
                        while (remaining > 0 && (bytesRead = dis.read(buffer, 0, (int) Math.min(buffer.length, remaining))) != -1) {
                            fos.write(buffer, 0, bytesRead);
                            remaining -= bytesRead;
                            totalBytesRead += bytesRead;
                            if (callback != null) {
                                callback.onReceivingProgress((float) totalBytesRead / (float) metadata.fileSize);
                            }
                        }
                        System.out.println("File saved as: " + outputFile.getAbsolutePath());
                        if (callback != null) {
                            callback.onReceivingFinished(outputFile, metadata.sender);
                        }
                    }
                }
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
     * @see FileReceivingEventHandler
     * @param callback the callback method
     */
    public void setEventHandler(FileReceivingEventHandler callback) {
        this.callback = callback;
    }
}


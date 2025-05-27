package org.deg.core;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * The FileSender class connects to a remote peer and sends a file,
 * preceded by file metadata (name and size).
 */
public class FileSender {

    private final String peerIp;
    private final int port;
    private final File file;

    /**
     * Constructs a FileSender for a specific file and target peer.
     * @param peerIp The IP address of the receiver.
     * @param port The port the receiver is listening on.
     * @param file The file to send.
     */
    public FileSender(String peerIp, int port, File file) {
        this.peerIp = peerIp;
        this.port = port;
        this.file = file;
    }

    /**
     * Initiates the file transfer to the specified peer.
     */
    public void send() {
        try (Socket socket = new Socket(peerIp, port)) {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            // Step 1: Send metadata
            Metadata metadata = new Metadata(file.getName(), file.length());
            String metadataStr = MetadataHandler.buildMetadata(metadata);
            dos.writeUTF(metadataStr);

            // Step 2: Send file content
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    dos.write(buffer, 0, bytesRead);
                }
                dos.flush();
                System.out.println("File sent successfully.");
            }

        } catch (IOException e) {
            System.err.println("Sender error: " + e.getMessage());
        }
    }
}

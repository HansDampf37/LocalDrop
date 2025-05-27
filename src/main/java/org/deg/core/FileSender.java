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
    private final Peer sender;
    private final Peer receiver;
    private final File file;

    /**
     * Constructs a FileSender for a specific file and target peer.
     * @param sender The sending peer.
     * @param receiver The receiving peer.
     * @param file The file to send.
     */
    public FileSender(Peer sender, Peer receiver, File file) {
        this.file = file;
        this.sender = sender;
        this.receiver = receiver;
    }

    /**
     * Initiates the file transfer to the specified peer.
     * @param progressCallback the callback is called whenever new bytes are sent.
     */
    public void send(FileSendingProgressCallback progressCallback) {
        try (Socket socket = new Socket(receiver.ip(), receiver.fileTransferPort())) {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            // Step 1: Send metadata
            Metadata metadata = new Metadata(file.getName(), file.length(), sender);
            String metadataStr = MetadataHandler.buildMetadata(metadata);
            dos.writeUTF(metadataStr);

            // Step 2: Send file content
            int totalBytesWritten = 0;
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    dos.write(buffer, 0, bytesRead);
                    totalBytesWritten += bytesRead;
                    if (progressCallback != null) {
                        progressCallback.handle((float)totalBytesWritten / (float)file.length());
                    }
                }
                dos.flush();
                System.out.println("File sent successfully.");
            }

        } catch (IOException e) {
            System.err.println("Sender error: " + e.getMessage());
        }
    }

    /**
     * Initiates the file transfer to the specified peer without a progressCallback
     */
    public void send() {
        send(null);
    }
}

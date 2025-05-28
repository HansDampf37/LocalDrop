package org.deg.core;

import org.deg.core.callbacks.FileSendingEventHandler;

import java.io.*;
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
     * @param callback the callback is called whenever new bytes are sent (can be null)
     */
    public void send(FileSendingEventHandler callback) throws SendingDeniedException {
        try (Socket socket = new Socket(receiver.ip(), receiver.fileTransferPort())) {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            // Step 1: Send metadata
            System.out.println("Send transmission request to " + receiver.name());
            Metadata metadata = new Metadata(file.getName(), file.length(), sender);
            String metadataStr = MetadataHandler.buildMetadata(metadata);
            dos.writeUTF(metadataStr);

            // Step 2: Wait until accepted
            System.out.println("Waiting for transmission request response...");
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            String accepted = dis.readUTF();
            if (!accepted.equals("ACCEPT")) {
                throw new SendingDeniedException();
            }

            // Step 3: Send file content
            System.out.println(receiver.name() + " accepted transmission. Start sending file...");
            int totalBytesWritten = 0;
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    dos.write(buffer, 0, bytesRead);
                    totalBytesWritten += bytesRead;
                    if (callback != null) {
                        callback.onSendingProgress((float) totalBytesWritten / (float) file.length());
                    }
                }
                dos.flush();
                System.out.println("File sending finished successfully.");
                if (callback != null) callback.onFinished(file, receiver);
            }
        } catch (IOException e) {
            System.err.println("Sender error: " + e.getMessage());
            if (callback != null) callback.onSendingFailed(e);
        }
    }
}

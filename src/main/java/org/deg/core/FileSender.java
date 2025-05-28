package org.deg.core;

import org.deg.backend.FileExpander;
import org.deg.core.callbacks.FileSendingEventHandler;

import java.io.*;
import java.net.Socket;
import java.util.List;

/**
 * The FileSender class connects to a remote peer and sends files,
 * preceded by file metadata (name and size).
 */
public class FileSender {
    private final Peer sender;
    private final Peer receiver;
    private final List<File> files;

    /**
     * Constructs a FileSender for a set of files and target peer.
     *
     * @param sender   The sending peer.
     * @param receiver The receiving peer.
     * @param files    The files to send.
     */
    public FileSender(Peer sender, Peer receiver, List<File> files) {
        this.files = FileExpander.expandFiles(files);
        this.sender = sender;
        this.receiver = receiver;
    }

    /**
     * Initiates the file transfer to the specified peer.
     *
     * @param callback the callback is called whenever new bytes are sent (can be null)
     */
    public void send(FileSendingEventHandler callback) throws SendingDeniedException {
        try (Socket socket = new Socket(receiver.ip(), receiver.fileTransferPort())) {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            // Step 1: Send metadata
            System.out.println("Send transmission request to " + receiver.name());
            Metadata metadata = new Metadata(
                    files.size(),
                    files.stream().map(File::getName).toList(),
                    files.stream().map(File::length).toList(),
                    sender
            );
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
            System.out.println(receiver.name() + " accepted transmission. Start sending files...");
            int totalBytesWritten = 0;
            for (File file : files) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        dos.write(buffer, 0, bytesRead);
                        totalBytesWritten += bytesRead;
                        if (callback != null) {
                            callback.onSendingProgress(file, (float) totalBytesWritten / (float) file.length());
                        }
                    }
                    dos.flush();
                }
                System.out.println(file.getName() + " sending finished successfully.");
                if (callback != null) callback.onFinished(file, receiver);
            }
            System.out.println("All Files sending finished successfully.");
            if (callback != null) callback.onFinished(receiver);
        } catch (IOException e) {
            System.err.println("Sender error: " + e.getMessage());
            if (callback != null) callback.onSendingFailed(e);
        }
    }
}

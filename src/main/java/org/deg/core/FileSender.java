package org.deg.core;

import org.deg.core.callbacks.FileSendingEventHandler;
import org.deg.core.callbacks.Progress;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.deg.core.Constants.ACCEPT_TRANSMISSION_REQUEST;

/**
 * The FileSender class connects to a remote peer and sends files,
 * preceded by file metadata (name and size).
 */
public class FileSender {
    private final Peer sender;
    private final Peer receiver;
    private final List<FileWithMetadata> files;

    /**
     * Constructs a FileSender for a set of files and target peer.
     *
     * @param sender   The sending peer.
     * @param receiver The receiving peer.
     * @param files    The files to send.
     */
    public FileSender(Peer sender, Peer receiver, List<File> files) {
        this.files = FileExpander.expandFilesWithRelativePaths(files);
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
                    files.stream().map(FileWithMetadata::relativePath).toList(),
                    files.stream().map(FileWithMetadata::sizeInBytes).toList(),
                    sender
            );
            String metadataStr = MetadataHandler.buildMetadata(metadata);
            byte[] data = metadataStr.getBytes(StandardCharsets.UTF_8);
            dos.writeInt(data.length);
            dos.write(data);

            // Step 2: Wait until accepted
            System.out.println("Waiting for transmission request response...");
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            String accepted = dis.readUTF();
            if (accepted.equals(ACCEPT_TRANSMISSION_REQUEST)) {
                System.out.println(receiver.name() + " accepted transmission. Start sending files...");
                if (callback != null) callback.onAccepted(receiver);
            } else {
                if (callback != null) callback.onDenied(receiver);
                throw new SendingDeniedException();
            }

            // Step 3: Send file content
            int i = 0;
            int totalBytesSent = 0;
            long startTime = System.currentTimeMillis();
            long totalBytes = files.stream().mapToLong(FileWithMetadata::sizeInBytes).sum();
            for (File file : files.stream().map(FileWithMetadata::file).toList()) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        dos.write(buffer, 0, bytesRead);
                        totalBytesSent += bytesRead;
                        if (callback != null) {
                            Progress progress = new Progress(files.get(i), totalBytesSent, totalBytes, i, files.size());
                            long durationSoFar = System.currentTimeMillis() - startTime;
                            float totalTimeInSeconds = durationSoFar / 1000f;
                            progress.bitsPerSecondEstimation = (long)(totalBytesSent * 8L / totalTimeInSeconds);
                            callback.onSendingProgress(progress);
                        }
                    }
                    dos.flush();
                    i++;
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

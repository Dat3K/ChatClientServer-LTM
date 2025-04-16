package chatclientserver.ltm.server;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import chatclientserver.ltm.database.FileTransferDAO;
import chatclientserver.ltm.database.MessageDAO;
import chatclientserver.ltm.database.UserDAO;
import chatclientserver.ltm.encryption.PlayfairCipher;
import chatclientserver.ltm.model.FileTransfer;
import chatclientserver.ltm.model.Message;
import chatclientserver.ltm.model.User;
import chatclientserver.ltm.util.Constants;
import chatclientserver.ltm.util.FileUtils;

import org.apache.commons.lang3.StringUtils;

/**
 * Handler for client connections.
 * This class is responsible for handling communication with a specific client.
 */
public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private String clientId;
    private boolean running;
    private MessageDAO messageDAO;
    private FileTransferDAO fileTransferDAO;
    private UserDAO userDAO;
    private User currentUser;
    private ChatServer server;

    /**
     * Constructs a ClientHandler for the specified client socket.
     *
     * @param clientSocket The client socket
     */
    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.clientId = UUID.randomUUID().toString();
        this.messageDAO = new MessageDAO();
        this.fileTransferDAO = new FileTransferDAO();
        this.userDAO = new UserDAO();

        try {
            // Create the streams
            this.outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            this.inputStream = new ObjectInputStream(clientSocket.getInputStream());

            // Create the received files directory
            FileUtils.createReceivedFilesDir();
        } catch (IOException e) {
            System.err.println("Error creating streams: " + e.getMessage());
            // Close resources to prevent leaks
            close();
        }
    }

    @Override
    public void run() {
        running = true;

        // Check if streams were created successfully
        if (inputStream == null || outputStream == null) {
            System.err.println("Cannot start client handler: streams not initialized");
            close();
            return;
        }

        try {
            while (running) {
                // Read the message type
                int messageType = inputStream.readInt();

                switch (messageType) {
                    case Constants.MESSAGE_TYPE_TEXT:
                        handleTextMessage();
                        break;
                    case Constants.MESSAGE_TYPE_FILE:
                        handleFileTransfer();
                        break;
                    case Constants.MESSAGE_TYPE_KEY_EXCHANGE:
                        handleKeyExchange();
                        break;
                    case Constants.MESSAGE_TYPE_USER_INFO:
                        handleUserInfo();
                        break;
                    default:
                        System.err.println("Unknown message type: " + messageType);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            close();
        }
    }

    /**
     * Handles a text message from the client.
     *
     * @throws IOException If an I/O error occurs
     * @throws ClassNotFoundException If the class of a serialized object cannot be found
     */
    private void handleTextMessage() throws IOException, ClassNotFoundException {
        // Read the message
        Message message = (Message) inputStream.readObject();
        message.setClientId(clientId);

        // Set user ID if authenticated
        if (currentUser != null && message.getUserId() == 0) {
            message.setUserId(currentUser.getId());
        }

        System.out.println("Received encrypted message: " + message.getEncryptedMessage());

        // Notify the server that a message has been received
        if (server != null) {
            server.notifyMessageReceived(this, message);
        }

        // Decrypt the message
        PlayfairCipher cipher = new PlayfairCipher(message.getKey());
        String decryptedMessage = cipher.decrypt(message.getEncryptedMessage());
        message.setDecryptedMessage(decryptedMessage);

        System.out.println("Decrypted message: " + decryptedMessage);

        // Prepare the search phrase to match the format of the decrypted message
        String preparedSearchPhrase = prepareSearchPhrase(Constants.SEARCH_PHRASE);
        System.out.println("Prepared search phrase: '" + preparedSearchPhrase + "'");

        // Find occurrences of the prepared search phrase
        List<Integer> positions = findPhrasePositions(decryptedMessage, preparedSearchPhrase);
        String positionsStr = positions.isEmpty() ? "Not found" : positions.toString();
        message.setPhrasePositions(positionsStr);

        System.out.println("Positions of '" + Constants.SEARCH_PHRASE + "': " + positionsStr);

        // Save the message to the database
        messageDAO.saveMessage(message);

        // Send the positions back to the client
        outputStream.writeInt(Constants.MESSAGE_TYPE_PHRASE_POSITIONS);
        outputStream.writeObject(positionsStr);
        outputStream.flush();
    }

    /**
     * Handles a file transfer from the client.
     *
     * @throws IOException If an I/O error occurs
     * @throws ClassNotFoundException If the class of a serialized object cannot be found
     */
    private void handleFileTransfer() throws IOException, ClassNotFoundException {
        // Read the file transfer
        FileTransfer fileTransfer = (FileTransfer) inputStream.readObject();
        fileTransfer.setClientId(clientId);

        // Set user ID if authenticated
        if (currentUser != null && fileTransfer.getUserId() == 0) {
            fileTransfer.setUserId(currentUser.getId());
        }

        System.out.println("Received file: " + fileTransfer.getFileName() + " (" + fileTransfer.getFileSize() + " bytes)");

        // Notify the server that a file transfer has been received
        if (server != null) {
            server.notifyFileTransferReceived(this, fileTransfer);
        }

        // Save the file
        File file = FileUtils.writeByteArrayToFile(fileTransfer.getFileData(), fileTransfer.getFileName());

        // Save the file transfer record to the database
        fileTransferDAO.saveFileTransfer(fileTransfer);

        System.out.println("File saved to: " + file.getAbsolutePath());
    }

    /**
     * Handles a key exchange from the client.
     *
     * @throws IOException If an I/O error occurs
     * @throws ClassNotFoundException If the class of a serialized object cannot be found
     */
    private void handleKeyExchange() throws IOException, ClassNotFoundException {
        // Read the key
        String key = (String) inputStream.readObject();

        System.out.println("Received key: " + key);

        // Notify the server that a key exchange has been received
        if (server != null) {
            server.notifyKeyExchangeReceived(this, key);
        }

        // For now, just echo the key back to confirm receipt
        outputStream.writeInt(Constants.MESSAGE_TYPE_KEY_EXCHANGE);
        outputStream.writeObject(key);
        outputStream.flush();
    }

    /**
     * Handles user information from the client.
     *
     * @throws IOException If an I/O error occurs
     * @throws ClassNotFoundException If the class of a serialized object cannot be found
     */
    private void handleUserInfo() throws IOException, ClassNotFoundException {
        // Read the user info
        User user = (User) inputStream.readObject();

        if (user != null) {
            // Store the user info
            this.currentUser = user;
            System.out.println("User authenticated: " + user.getUsername());

            // Notify the server that user information has been received
            if (server != null) {
                server.notifyUserInfoReceived(this, user);
            }
        }
    }

    /**
     * Prepares a search phrase to match the format of decrypted messages.
     * This includes removing diacritical marks and replacing spaces with 'Z'.
     *
     * @param phrase The original search phrase
     * @return The prepared search phrase
     */
    private String prepareSearchPhrase(String phrase) {
        // Remove diacritical marks (convert Vietnamese to non-accented form)
        String normalized = StringUtils.stripAccents(phrase);

        // Replace spaces with 'Z' (same as in PlayfairCipher)
        normalized = normalized.replace(' ', 'Z');

        // Convert to uppercase (decrypted messages are in uppercase)
        return normalized.toUpperCase();
    }

    /**
     * Finds all positions of a phrase in a text.
     *
     * @param text The text to search in
     * @param phrase The phrase to search for (should already be prepared)
     * @return A list of positions where the phrase occurs
     */
    private List<Integer> findPhrasePositions(String text, String phrase) {
        List<Integer> positions = new ArrayList<>();

        // No need to convert to lowercase since both the text and phrase are already in uppercase
        int index = text.indexOf(phrase);
        while (index >= 0) {
            positions.add(index);
            index = text.indexOf(phrase, index + 1);
        }

        return positions;
    }

    /**
     * Closes the client connection.
     */
    public void close() {
        running = false;

        try {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing client handler: " + e.getMessage());
        }

        // Remove this handler from the server's list
        ChatServer.getInstance().removeClient(this);
    }

    /**
     * Gets the client ID.
     *
     * @return The client ID
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Sets the server observer for this client handler.
     *
     * @param server The server to notify of events
     */
    public void setObserver(ChatServer server) {
        this.server = server;
    }

    /**
     * Gets the current user associated with this client handler.
     *
     * @return The current user, or null if not authenticated
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Gets the IP address of the client.
     *
     * @return The IP address as a string
     */
    public String getClientIpAddress() {
        if (clientSocket != null && clientSocket.getInetAddress() != null) {
            return clientSocket.getInetAddress().getHostAddress();
        }
        return "Unknown";
    }

    /**
     * Gets the client socket.
     *
     * @return The client socket
     */
    public Socket getClientSocket() {
        return clientSocket;
    }
}

package chatclientserver.ltm.client;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import chatclientserver.ltm.database.MessageDAO;
import chatclientserver.ltm.encryption.PlayfairCipher;
import chatclientserver.ltm.model.FileTransfer;
import chatclientserver.ltm.model.Message;
import chatclientserver.ltm.model.User;
import chatclientserver.ltm.util.Constants;
import chatclientserver.ltm.util.FileUtils;

import java.util.List;

/**
 * Client class for the chat application.
 * This class handles the communication with the server.
 */
public class ChatClient {
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private boolean connected;
    private ExecutorService executorService;
    private MessageListener messageListener;
    private String currentKey;
    private User currentUser;
    private MessageDAO messageDAO;

    /**
     * Constructs a ChatClient.
     */
    public ChatClient() {
        executorService = Executors.newSingleThreadExecutor();
        currentKey = Constants.DEFAULT_KEY;
        messageDAO = new MessageDAO();
    }

    /**
     * Connects to the server.
     *
     * @param host The server host
     * @param port The server port
     * @param user The authenticated user (can be null for anonymous connection)
     * @return true if the connection was successful, false otherwise
     */
    private String lastErrorMessage = "";

    /**
     * Gets the last error message.
     *
     * @return The last error message
     */
    public String getLastErrorMessage() {
        return lastErrorMessage;
    }

    public boolean connect(String host, int port, User user) {
        // Reset error message
        lastErrorMessage = "";

        // Check if server is running first
        if (!isServerRunning(host, port)) {
            lastErrorMessage = "Server is not running at " + host + ":" + port;
            System.err.println(lastErrorMessage);
            return false;
        }

        try {
            socket = new Socket(host, port);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
            connected = true;
            currentUser = user;

            // Check if executor service is shutdown and recreate if necessary
            if (executorService.isShutdown()) {
                executorService = Executors.newSingleThreadExecutor();
            }

            // Start listening for messages from the server
            executorService.execute(this::listenForMessages);

            // Send user info to server if authenticated
            if (currentUser != null) {
                outputStream.writeInt(Constants.MESSAGE_TYPE_USER_INFO);
                outputStream.writeObject(currentUser);
                outputStream.flush();
            }

            return true;
        } catch (IOException e) {
            lastErrorMessage = "Error connecting to server: " + e.getMessage();
            System.err.println(lastErrorMessage);
            e.printStackTrace(); // Print stack trace for debugging
            return false;
        }
    }

    /**
     * Connects to the server anonymously.
     *
     * @param host The server host
     * @param port The server port
     * @return true if the connection was successful, false otherwise
     */
    public boolean connect(String host, int port) {
        return connect(host, port, null);
    }

    /**
     * Checks if the server is running at the specified host and port.
     *
     * @param host The server host
     * @param port The server port
     * @return true if the server is running, false otherwise
     */
    private boolean isServerRunning(String host, int port) {
        Socket testSocket = null;
        try {
            // Try to connect to the server
            testSocket = new Socket();
            testSocket.connect(new InetSocketAddress(host, port), 2000); // 2 seconds timeout
            return true;
        } catch (IOException e) {
            // Server is not running or cannot be reached
            lastErrorMessage = "Cannot connect to server: " + e.getMessage();
            System.err.println(lastErrorMessage);
            return false;
        } finally {
            if (testSocket != null && !testSocket.isClosed()) {
                try {
                    testSocket.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
    }

    /**
     * Disconnects from the server.
     */
    public void disconnect() {
        // If not connected, nothing to do
        if (!connected) {
            return;
        }

        connected = false;

        try {
            // Close streams and socket if they exist
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    // Ignore, we're disconnecting anyway
                }
                outputStream = null;
            }

            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // Ignore, we're disconnecting anyway
                }
                inputStream = null;
            }

            if (socket != null && !socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // Ignore, we're disconnecting anyway
                }
                socket = null;
            }
        } catch (Exception e) {
            System.err.println("Error disconnecting from server: " + e.getMessage());
        }

        // Don't shutdown the executor service, just let it finish its tasks
        // If we shutdown here, we won't be able to use it again for reconnection
    }

    /**
     * Sends a text message to the server.
     *
     * @param text The text to send
     * @param key The encryption key
     * @return true if the message was sent successfully, false otherwise
     */
    public boolean sendMessage(String text, String key) {
        if (!connected) {
            return false;
        }

        try {
            // Encrypt the message
            PlayfairCipher cipher = new PlayfairCipher(key);
            String encryptedMessage = cipher.encrypt(text);

            // Create a message object
            Message message = new Message();
            message.setEncryptedMessage(encryptedMessage);
            message.setKey(key);

            // Set user ID if authenticated
            if (currentUser != null) {
                message.setUserId(currentUser.getId());
            }

            // Send the message
            outputStream.writeInt(Constants.MESSAGE_TYPE_TEXT);
            outputStream.writeObject(message);
            outputStream.flush();

            return true;
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
            return false;
        }
    }

    /**
     * Sends a file to the server.
     *
     * @param file The file to send
     * @return true if the file was sent successfully, false otherwise
     */
    public boolean sendFile(File file) {
        if (!connected || !file.exists()) {
            return false;
        }

        try {
            // Read the file
            byte[] fileData = FileUtils.readFileToByteArray(file);

            // Create a file transfer object
            FileTransfer fileTransfer = new FileTransfer();
            fileTransfer.setFileName(file.getName());
            fileTransfer.setFileSize(file.length());
            fileTransfer.setFileType(FileUtils.getFileType(file.getName()));
            fileTransfer.setFileData(fileData);

            // Set user ID if authenticated
            if (currentUser != null) {
                fileTransfer.setUserId(currentUser.getId());
            }

            // Send the file
            outputStream.writeInt(Constants.MESSAGE_TYPE_FILE);
            outputStream.writeObject(fileTransfer);
            outputStream.flush();

            return true;
        } catch (IOException e) {
            System.err.println("Error sending file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Sends a key exchange message to the server.
     *
     * @param key The key to exchange
     * @return true if the key was sent successfully, false otherwise
     */
    public boolean sendKeyExchange(String key) {
        if (!connected) {
            return false;
        }

        try {
            // Send the key
            outputStream.writeInt(Constants.MESSAGE_TYPE_KEY_EXCHANGE);
            outputStream.writeObject(key);
            outputStream.flush();

            // Update the current key
            currentKey = key;

            return true;
        } catch (IOException e) {
            System.err.println("Error sending key exchange: " + e.getMessage());
            return false;
        }
    }

    /**
     * Listens for messages from the server.
     */
    private void listenForMessages() {
        try {
            while (connected && socket != null && !socket.isClosed() && inputStream != null) {
                // Read the message type
                int messageType = inputStream.readInt();

                switch (messageType) {
                    case Constants.MESSAGE_TYPE_PHRASE_POSITIONS:
                        handlePhrasePositions();
                        break;
                    case Constants.MESSAGE_TYPE_KEY_EXCHANGE:
                        handleKeyExchange();
                        break;
                    default:
                        System.err.println("Unknown message type: " + messageType);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            if (connected) {
                System.err.println("Error listening for messages: " + e.getMessage());
                disconnect();
            }
        }
    }

    /**
     * Handles a phrase positions message from the server.
     *
     * @throws IOException If an I/O error occurs
     * @throws ClassNotFoundException If the class of a serialized object cannot be found
     */
    private void handlePhrasePositions() throws IOException, ClassNotFoundException {
        // Read the positions
        String positions = (String) inputStream.readObject();

        // Notify the listener
        if (messageListener != null) {
            messageListener.onPhrasePositionsReceived(positions);
        }
    }

    /**
     * Handles a key exchange message from the server.
     *
     * @throws IOException If an I/O error occurs
     * @throws ClassNotFoundException If the class of a serialized object cannot be found
     */
    private void handleKeyExchange() throws IOException, ClassNotFoundException {
        // Read the key
        String key = (String) inputStream.readObject();

        // Notify the listener
        if (messageListener != null) {
            messageListener.onKeyExchangeReceived(key);
        }
    }

    /**
     * Sets the message listener.
     *
     * @param listener The listener to set
     */
    public void setMessageListener(MessageListener listener) {
        this.messageListener = listener;
    }

    /**
     * Gets the current key.
     *
     * @return The current key
     */
    public String getCurrentKey() {
        return currentKey;
    }

    /**
     * Gets the current user.
     *
     * @return The current user, or null if not authenticated
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Sets the current user.
     *
     * @param user The user to set
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    /**
     * Checks if the client is connected to the server.
     *
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Checks if the client is authenticated.
     *
     * @return true if authenticated, false otherwise
     */
    public boolean isAuthenticated() {
        return currentUser != null;
    }

    /**
     * Interface for listening to messages from the server.
     */
    public interface MessageListener {
        /**
         * Called when phrase positions are received from the server.
         *
         * @param positions The positions as a string
         */
        void onPhrasePositionsReceived(String positions);

        /**
         * Called when a key exchange is received from the server.
         *
         * @param key The key
         */
        void onKeyExchangeReceived(String key);
    }

    /**
     * Gets the chat history.
     *
     * @param limit The maximum number of messages to retrieve
     * @return A list of messages
     */
    public List<Message> getChatHistory(int limit) {
        return messageDAO.getChatHistory(limit);
    }

    /**
     * Gets the chat history for the current user.
     *
     * @param limit The maximum number of messages to retrieve
     * @return A list of messages
     */
    public List<Message> getChatHistoryForCurrentUser(int limit) {
        if (currentUser == null) {
            return java.util.Collections.emptyList();
        }
        return messageDAO.getChatHistoryForUser(currentUser.getId(), limit);
    }
}

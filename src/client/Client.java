package client;

import client.controller.ChatController;
import common.Message;
import common.MessageType;

import java.io.*;
import java.net.*;
import java.util.logging.*;

/**
 * Client class that handles the connection to the server and message processing.
 */
public class Client {
    private static final Logger LOGGER = Logger.getLogger(Client.class.getName());
    
    private final String serverAddress;
    private final int serverPort;
    private final String username;
    private final ChatController controller;
    
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private Thread receiveThread;
    private boolean connected;
    
    /**
     * Constructs a new Client with the specified server details and username.
     * 
     * @param serverAddress The server address
     * @param serverPort The server port
     * @param username The username
     * @param controller The chat controller
     * @throws IOException If an I/O error occurs when creating the socket
     */
    public Client(String serverAddress, int serverPort, String username, ChatController controller) throws IOException {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.username = username;
        this.controller = controller;
        this.connected = false;
    }
    
    /**
     * Starts the client, connecting to the server and starting the receive thread.
     * 
     * @throws IOException If an I/O error occurs when connecting to the server
     */
    public void start() throws IOException {
        // Connect to the server
        socket = new Socket(serverAddress, serverPort);
        
        // Create output stream first to avoid deadlock
        output = new ObjectOutputStream(socket.getOutputStream());
        input = new ObjectInputStream(socket.getInputStream());
        
        // Send username
        Message usernameMessage = Message.createUsernameMessage(username);
        output.writeObject(usernameMessage);
        output.flush();
        
        // Start receive thread
        connected = true;
        receiveThread = new Thread(this::receiveMessages);
        receiveThread.start();
        
        LOGGER.info("Connected to server at " + serverAddress + ":" + serverPort);
    }
    
    /**
     * Sends a text message to the server.
     * 
     * @param encryptedText The encrypted text to send
     */
    public void sendTextMessage(String encryptedText) {
        if (!connected) {
            return;
        }
        
        try {
            Message message = Message.createTextMessage(username, encryptedText);
            output.writeObject(message);
            output.flush();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error sending text message", e);
            handleDisconnect("Error sending message: " + e.getMessage());
        }
    }
    
    /**
     * Sends a file to the server.
     * 
     * @param fileName The name of the file
     * @param fileData The file data as a byte array
     */
    public void sendFile(String fileName, byte[] fileData) {
        if (!connected) {
            return;
        }
        
        try {
            Message message = Message.createFileMessage(username, fileName, fileData);
            output.writeObject(message);
            output.flush();
            LOGGER.info("Sent file: " + fileName + " (" + fileData.length + " bytes)");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error sending file", e);
            handleDisconnect("Error sending file: " + e.getMessage());
        }
    }
    
    /**
     * Disconnects from the server.
     */
    public void disconnect() {
        if (!connected) {
            return;
        }
        
        connected = false;
        
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error closing socket", e);
        }
        
        LOGGER.info("Disconnected from server");
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
     * Receives messages from the server in a loop.
     */
    private void receiveMessages() {
        try {
            while (connected) {
                Message message = (Message) input.readObject();
                
                switch (message.getType()) {
                    case TEXT:
                        controller.handleReceivedTextMessage(message.getSenderUsername(), message.getContent());
                        break;
                    case FILE:
                        controller.handleReceivedFile(message.getSenderUsername(), message.getFileName(), message.getFileData());
                        break;
                    default:
                        LOGGER.warning("Received unknown message type: " + message.getType());
                }
            }
        } catch (EOFException | SocketException e) {
            // Server closed the connection
            handleDisconnect("Server closed the connection");
        } catch (IOException | ClassNotFoundException e) {
            // Other error
            LOGGER.log(Level.SEVERE, "Error receiving messages", e);
            handleDisconnect("Error receiving messages: " + e.getMessage());
        }
    }
    
    /**
     * Handles disconnection from the server.
     * 
     * @param reason The reason for disconnection
     */
    private void handleDisconnect(String reason) {
        if (connected) {
            connected = false;
            
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error closing socket", e);
            }
            
            controller.handleServerDisconnect(reason);
            LOGGER.info("Disconnected from server: " + reason);
        }
    }
}

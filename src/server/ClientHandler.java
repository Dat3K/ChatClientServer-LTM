package server;

import common.Message;
import common.MessageType;

import java.io.*;
import java.net.*;
import java.util.logging.*;

/**
 * Handles communication with a specific client.
 * Each client connection has its own ClientHandler running in a separate thread.
 */
public class ClientHandler implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(ClientHandler.class.getName());
    
    private final Socket socket;
    private final Server server;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private String username;
    private boolean running;
    
    /**
     * Constructs a ClientHandler for the specified client socket.
     * 
     * @param socket The client socket
     * @param server The server instance
     */
    public ClientHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
        this.running = true;
        
        try {
            // Create output stream first to avoid deadlock
            this.output = new ObjectOutputStream(socket.getOutputStream());
            this.input = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error creating streams for client", e);
            running = false;
        }
    }
    
    /**
     * Gets the username of this client.
     * 
     * @return The username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Sends a message to this client.
     * 
     * @param message The message to send
     */
    public void sendMessage(Object message) {
        try {
            output.writeObject(message);
            output.flush();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error sending message to client: " + username, e);
            disconnect();
        }
    }
    
    /**
     * Disconnects this client.
     */
    public void disconnect() {
        running = false;
        
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error closing client connection", e);
        }
        
        server.removeClient(this);
    }
    
    @Override
    public void run() {
        try {
            // Wait for the username message
            Message usernameMessage = (Message) input.readObject();
            if (usernameMessage.getType() == MessageType.USERNAME) {
                username = usernameMessage.getSenderUsername();
                LOGGER.info("Client registered with username: " + username);
            } else {
                LOGGER.warning("First message from client was not a username message");
                disconnect();
                return;
            }
            
            // Process messages from the client
            while (running) {
                try {
                    Message message = (Message) input.readObject();
                    LOGGER.info("Received message from " + username + " of type " + message.getType());
                    
                    // Broadcast the message to all other clients
                    server.broadcast(message, this);
                } catch (EOFException | SocketException e) {
                    // Client disconnected
                    LOGGER.info("Client disconnected: " + username);
                    break;
                } catch (ClassNotFoundException e) {
                    LOGGER.log(Level.SEVERE, "Error reading message from client", e);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Error handling client", e);
        } finally {
            disconnect();
        }
    }
}

package server;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.logging.*;

/**
 * Server class that listens for client connections and manages communication between clients.
 * This class follows the Singleton pattern to ensure only one server instance exists.
 */
public class Server {
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
    private static final int PORT = 12345;
    private static Server instance;
    
    private final CopyOnWriteArrayList<ClientHandler> clients;
    private ServerSocket serverSocket;
    private boolean running;
    
    /**
     * Gets the singleton instance of the Server.
     * 
     * @return The Server instance
     */
    public static synchronized Server getInstance() {
        if (instance == null) {
            instance = new Server();
        }
        return instance;
    }
    
    /**
     * Private constructor to enforce the Singleton pattern.
     */
    private Server() {
        clients = new CopyOnWriteArrayList<>();
        running = false;
    }
    
    /**
     * Starts the server, listening for client connections.
     */
    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            running = true;
            LOGGER.info("Server started on port " + PORT);
            
            // Accept client connections in a loop
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    LOGGER.info("New client connected: " + clientSocket.getInetAddress().getHostAddress());
                    
                    // Create a new handler for this client
                    ClientHandler handler = new ClientHandler(clientSocket, this);
                    clients.add(handler);
                    
                    // Start the handler in a new thread
                    new Thread(handler).start();
                } catch (IOException e) {
                    if (running) {
                        LOGGER.log(Level.SEVERE, "Error accepting client connection", e);
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error starting server", e);
        }
    }
    
    /**
     * Stops the server and disconnects all clients.
     */
    public void stop() {
        running = false;
        
        // Disconnect all clients
        for (ClientHandler client : clients) {
            client.disconnect();
        }
        clients.clear();
        
        // Close the server socket
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error closing server socket", e);
        }
        
        LOGGER.info("Server stopped");
    }
    
    /**
     * Broadcasts a message to all clients except the sender.
     * 
     * @param message The message to broadcast
     * @param sender The sender's ClientHandler (to exclude from broadcast)
     */
    public void broadcast(Object message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }
    
    /**
     * Removes a client from the list of connected clients.
     * 
     * @param client The client to remove
     */
    public void removeClient(ClientHandler client) {
        clients.remove(client);
        LOGGER.info("Client disconnected: " + client.getUsername());
    }
    
    /**
     * Gets the number of connected clients.
     * 
     * @return The number of connected clients
     */
    public int getClientCount() {
        return clients.size();
    }
    
    /**
     * Main method to start the server.
     * 
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        Server.getInstance().start();
    }
}

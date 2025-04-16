package chatclientserver.ltm.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import chatclientserver.ltm.model.FileTransfer;
import chatclientserver.ltm.model.Message;
import chatclientserver.ltm.model.User;
import chatclientserver.ltm.util.Constants;

/**
 * The main server class for the chat application.
 * This class is responsible for accepting client connections and creating handlers for them.
 */
public class ChatServer {
    private static ChatServer instance;
    private ServerSocket serverSocket;
    private boolean running;
    private ExecutorService executorService;
    private List<ClientHandler> clients;
    private List<ServerObserver> observers;
    private int port;

    /**
     * Private constructor to enforce the Singleton pattern.
     */
    private ChatServer() {
        clients = new ArrayList<>();
        observers = new CopyOnWriteArrayList<>();
        executorService = Executors.newCachedThreadPool();
    }

    /**
     * Gets the singleton instance of the ChatServer.
     *
     * @return The ChatServer instance
     */
    public static synchronized ChatServer getInstance() {
        if (instance == null) {
            instance = new ChatServer();
        }
        return instance;
    }

    /**
     * Starts the server on the specified port.
     *
     * @param port The port to listen on
     * @throws IOException If an I/O error occurs when opening the socket
     */
    public void start(int port) throws IOException {
        this.port = port;
        serverSocket = new ServerSocket(port);
        running = true;

        System.out.println("Server started on port " + port);

        // Notify observers that the server has started
        notifyServerStarted(port);

        // Accept client connections in a separate thread
        new Thread(() -> acceptClients()).start();
    }

    /**
     * Accepts client connections and creates handlers for them.
     */
    private void acceptClients() {
        try {
            while (running) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());

                // Create a handler for the client
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandler.setObserver(this);
                clients.add(clientHandler);

                // Notify observers that a client has connected
                notifyClientConnected(clientHandler);

                // Start the handler in the thread pool
                executorService.execute(clientHandler);
            }
        } catch (IOException e) {
            if (running) {
                System.err.println("Error accepting client connection: " + e.getMessage());
                e.printStackTrace(); // Print stack trace for debugging
            }
        }
    }

    /**
     * Stops the server.
     */
    public void stop() {
        running = false;

        // Close all client connections
        for (ClientHandler client : clients) {
            client.close();
        }

        // Shutdown the executor service
        executorService.shutdown();

        // Close the server socket
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing server socket: " + e.getMessage());
            notifyServerError("Error closing server socket", e);
        }

        System.out.println("Server stopped");

        // Notify observers that the server has stopped
        notifyServerStopped();
    }

    /**
     * Removes a client handler from the list of clients.
     *
     * @param clientHandler The client handler to remove
     */
    public void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        System.out.println("Client disconnected. Remaining clients: " + clients.size());

        // Notify observers that a client has disconnected
        notifyClientDisconnected(clientHandler);
    }

    /**
     * Gets the list of connected clients.
     *
     * @return The list of client handlers
     */
    public List<ClientHandler> getClients() {
        return clients;
    }

    /**
     * The main method to start the server.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            ChatServer.getInstance().start(Constants.DEFAULT_SERVER_PORT);
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
            ChatServer.getInstance().notifyServerError("Error starting server", e);
        }
    }

    /**
     * Adds an observer to the server.
     *
     * @param observer The observer to add
     */
    public void addObserver(ServerObserver observer) {
        observers.add(observer);
    }

    /**
     * Removes an observer from the server.
     *
     * @param observer The observer to remove
     */
    public void removeObserver(ServerObserver observer) {
        observers.remove(observer);
    }

    /**
     * Notifies all observers that a client has connected.
     *
     * @param clientHandler The client handler for the connected client
     */
    public void notifyClientConnected(ClientHandler clientHandler) {
        for (ServerObserver observer : observers) {
            observer.onClientConnected(clientHandler);
        }
    }

    /**
     * Notifies all observers that a client has disconnected.
     *
     * @param clientHandler The client handler for the disconnected client
     */
    public void notifyClientDisconnected(ClientHandler clientHandler) {
        for (ServerObserver observer : observers) {
            observer.onClientDisconnected(clientHandler);
        }
    }

    /**
     * Notifies all observers that a message has been received.
     *
     * @param clientHandler The client handler for the client that sent the message
     * @param message The message that was received
     */
    public void notifyMessageReceived(ClientHandler clientHandler, Message message) {
        for (ServerObserver observer : observers) {
            observer.onMessageReceived(clientHandler, message);
        }
    }

    /**
     * Notifies all observers that a file transfer has been received.
     *
     * @param clientHandler The client handler for the client that sent the file
     * @param fileTransfer The file transfer that was received
     */
    public void notifyFileTransferReceived(ClientHandler clientHandler, FileTransfer fileTransfer) {
        for (ServerObserver observer : observers) {
            observer.onFileTransferReceived(clientHandler, fileTransfer);
        }
    }

    /**
     * Notifies all observers that a key exchange has been received.
     *
     * @param clientHandler The client handler for the client that sent the key
     * @param key The key that was received
     */
    public void notifyKeyExchangeReceived(ClientHandler clientHandler, String key) {
        for (ServerObserver observer : observers) {
            observer.onKeyExchangeReceived(clientHandler, key);
        }
    }

    /**
     * Notifies all observers that user information has been received.
     *
     * @param clientHandler The client handler for the client that sent the user info
     * @param user The user information that was received
     */
    public void notifyUserInfoReceived(ClientHandler clientHandler, User user) {
        for (ServerObserver observer : observers) {
            observer.onUserInfoReceived(clientHandler, user);
        }
    }

    /**
     * Notifies all observers that the server has started.
     *
     * @param port The port the server is listening on
     */
    public void notifyServerStarted(int port) {
        for (ServerObserver observer : observers) {
            observer.onServerStarted(port);
        }
    }

    /**
     * Notifies all observers that the server has stopped.
     */
    public void notifyServerStopped() {
        for (ServerObserver observer : observers) {
            observer.onServerStopped();
        }
    }

    /**
     * Notifies all observers that an error has occurred.
     *
     * @param message The error message
     * @param exception The exception that occurred (can be null)
     */
    public void notifyServerError(String message, Exception exception) {
        for (ServerObserver observer : observers) {
            observer.onServerError(message, exception);
        }
    }

    /**
     * Gets the port the server is listening on.
     *
     * @return The port
     */
    public int getPort() {
        return port;
    }
}

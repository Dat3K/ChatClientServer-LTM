package chatclientserver.ltm.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    /**
     * Private constructor to enforce the Singleton pattern.
     */
    private ChatServer() {
        clients = new ArrayList<>();
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
        serverSocket = new ServerSocket(port);
        running = true;

        System.out.println("Server started on port " + port);

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
                clients.add(clientHandler);

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
        }

        System.out.println("Server stopped");
    }

    /**
     * Removes a client handler from the list of clients.
     *
     * @param clientHandler The client handler to remove
     */
    public void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        System.out.println("Client disconnected. Remaining clients: " + clients.size());
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
        }
    }
}

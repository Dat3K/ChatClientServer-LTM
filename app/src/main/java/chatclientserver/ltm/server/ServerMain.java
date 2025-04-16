package chatclientserver.ltm.server;

import java.io.IOException;

import chatclientserver.ltm.util.Constants;

/**
 * Main class for starting the chat server.
 */
public class ServerMain {
    /**
     * The main method to start the server.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            System.out.println("Starting Chat Server...");
            ChatServer.getInstance().start(Constants.DEFAULT_SERVER_PORT);
            System.out.println("Server is running on port " + Constants.DEFAULT_SERVER_PORT);

            // Add a shutdown hook to stop the server gracefully
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Shutting down server...");
                ChatServer.getInstance().stop();
            }));
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        }
    }
}

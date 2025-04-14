/*
 * Main application class for the Chat Client-Server application
 */
package chatclientserver.ltm;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import chatclientserver.ltm.client.ClientGUI;
import chatclientserver.ltm.server.ChatServer;
import chatclientserver.ltm.util.Constants;

/**
 * Main application class that provides entry points for both client and server.
 */
public class App {
    /**
     * The main method that allows starting either the client or the server.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Error setting look and feel: " + e.getMessage());
        }

        // Ask the user whether to start the client or the server
        String[] options = {"Client", "Server", "Both", "Exit"};
        int choice = JOptionPane.showOptionDialog(
            null,
            "Do you want to start the client or the server?",
            "Chat Application",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );

        switch (choice) {
            case 0: // Client
                startClient();
                break;
            case 1: // Server
                startServer();
                break;
            case 2: // Both
                startServer();
                startClient();
                break;
            default: // Exit or closed dialog
                System.exit(0);
        }
    }

    /**
     * Starts the chat client.
     */
    private static void startClient() {
        SwingUtilities.invokeLater(() -> {
            ClientGUI gui = new ClientGUI();
            gui.setVisible(true);
        });
    }

    /**
     * Starts the chat server.
     */
    private static void startServer() {
        try {
            System.out.println("Starting Chat Server...");
            ChatServer.getInstance().start(Constants.SERVER_PORT);
            System.out.println("Server is running on port " + Constants.SERVER_PORT);

            // Add a shutdown hook to stop the server gracefully
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Shutting down server...");
                ChatServer.getInstance().stop();
            }));
        } catch (Exception e) {
            System.err.println("Error starting server: " + e.getMessage());
            JOptionPane.showMessageDialog(
                null,
                "Error starting server: " + e.getMessage(),
                "Server Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}

/*
 * Main application class for the Chat Client-Server application
 */
package chatclientserver.ltm;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import chatclientserver.ltm.client.ClientGUI;
import chatclientserver.ltm.server.ChatServer;
import chatclientserver.ltm.server.ServerGUI;

/**
 * Main application class that provides entry points for both client and server.
 */
public class App {
    /**
     * The main method that allows starting either the client or the server.
     *
     * @param args Command line arguments ("client", "server", or "both")
     */
    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Error setting look and feel: " + e.getMessage());
        }

        // Check if command line arguments were provided
        if (args.length > 0) {
            String arg = args[0].toLowerCase();

            if (arg.equals("client")) {
                startClient();
                return;
            } else if (arg.equals("server")) {
                startServer();
                return;
            } else if (arg.equals("both")) {
                startBoth();
                return;
            }
        }

        // If no valid arguments were provided, show the dialog
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
                startBoth();
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

            // Don't show the GUI yet, only show login dialog
            // GUI will only be visible after successful login and connection

            // Show login dialog automatically
            gui.showLoginDialog();
        });
    }

    /**
     * Starts the chat server with a GUI and automatically opens the port.
     */
    private static void startServer() {
        try {
            System.out.println("Starting Chat Server GUI...");
            ServerGUI serverGUI = new ServerGUI();

            // Automatically start the server
            boolean serverStarted = serverGUI.autoStartServer();

            if (!serverStarted) {
                System.err.println("Failed to automatically start the server. Starting GUI anyway.");
            } else {
                System.out.println("Server automatically started on default port.");
            }

            serverGUI.setVisible(true);

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

    /**
     * Starts both the chat client and server together.
     * The server is automatically started when this method is called.
     */
    private static void startBoth() {
        try {
            System.out.println("Starting both Chat Client and Server...");

            // Create the server GUI and automatically start the server
            ServerGUI serverGUI = new ServerGUI();
            boolean serverStarted = serverGUI.autoStartServer();

            if (!serverStarted) {
                System.err.println("Failed to automatically start the server. Starting GUI anyway.");
            } else {
                System.out.println("Server automatically started on default port.");
            }

            // Make the server GUI visible
            serverGUI.setVisible(true);

            // Start the client
            startClient();

            // Add a shutdown hook to stop the server gracefully
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Shutting down server...");
                ChatServer.getInstance().stop();
            }));
        } catch (Exception e) {
            System.err.println("Error starting both client and server: " + e.getMessage());
            JOptionPane.showMessageDialog(
                null,
                "Error starting both client and server: " + e.getMessage(),
                "Startup Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}

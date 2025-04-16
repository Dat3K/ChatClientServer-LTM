package chatclientserver.ltm.server;

import javax.swing.SwingUtilities;

/**
 * Main class for starting the chat server with a GUI.
 */
public class ServerMain {
    /**
     * The main method to start the server with a GUI.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ServerGUI gui = new ServerGUI();

            // Automatically start the server
            boolean serverStarted = gui.autoStartServer();

            if (!serverStarted) {
                System.err.println("Failed to automatically start the server. Starting GUI anyway.");
            } else {
                System.out.println("Server automatically started on default port.");
            }

            gui.setVisible(true);

            // Add a shutdown hook to stop the server gracefully
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Shutting down server...");
                ChatServer.getInstance().stop();
            }));
        });
    }
}

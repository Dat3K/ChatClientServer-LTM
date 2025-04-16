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
            gui.setVisible(true);

            // Add a shutdown hook to stop the server gracefully
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Shutting down server...");
                ChatServer.getInstance().stop();
            }));
        });
    }
}

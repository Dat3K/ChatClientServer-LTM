package chatclientserver.ltm.client;

import javax.swing.SwingUtilities;

/**
 * Main class for starting the chat client.
 */
public class ClientMain {
    /**
     * The main method to start the client.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClientGUI gui = new ClientGUI();
            gui.setVisible(true);

            // Show login dialog automatically on startup
            gui.showLoginDialog();
        });
    }
}

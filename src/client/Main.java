package client;

import client.controller.ChatController;
import client.model.ChatModel;
import client.view.ChatWindow;

import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class for starting the chat client application.
 */
public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    
    /**
     * Main method to start the client application.
     * 
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        // Set up logging
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.INFO);
        
        // Start the application on the EDT
        SwingUtilities.invokeLater(() -> {
            try {
                // Create the MVC components
                ChatModel model = new ChatModel();
                ChatWindow view = new ChatWindow("Chat Client");
                ChatController controller = new ChatController(model, view);
                
                // Show the connection dialog
                controller.showConnectionDialog();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error starting application", e);
                JOptionPane.showMessageDialog(null, "Error starting application: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}

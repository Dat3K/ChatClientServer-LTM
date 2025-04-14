package client.controller;

import client.Client;
import client.model.ChatModel;
import client.view.ChatWindow;
import client.view.ConnectionDialog;
import crypto.PlayFairCipher;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller class for the chat client, following the MVC pattern.
 * Handles user input and updates the model and view accordingly.
 */
public class ChatController {
    private static final Logger LOGGER = Logger.getLogger(ChatController.class.getName());
    
    private final ChatModel model;
    private final ChatWindow view;
    private final ConnectionDialog connectionDialog;
    private final PlayFairCipher cipher;
    private Client client;
    
    /**
     * Constructs a new ChatController with the specified model and view.
     * 
     * @param model The chat model
     * @param view The chat window
     */
    public ChatController(ChatModel model, ChatWindow view) {
        this.model = model;
        this.view = view;
        this.connectionDialog = new ConnectionDialog(view);
        this.cipher = new PlayFairCipher();
        
        // Register the view as an observer of the model
        model.addObserver(view);
        
        // Set up action listeners
        view.setSendAction(this::handleSendMessage);
        view.setFileAction(this::handleSendFile);
        view.setCloseAction(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleDisconnect();
            }
        });
        
        connectionDialog.setConnectAction(this::handleConnect);
    }
    
    /**
     * Shows the connection dialog and attempts to connect to the server.
     */
    public void showConnectionDialog() {
        connectionDialog.setVisible(true);
        
        // If the user canceled, exit the application
        if (!connectionDialog.isConnected()) {
            System.exit(0);
        }
    }
    
    /**
     * Handles the connect button action.
     * 
     * @param e The action event
     */
    private void handleConnect(ActionEvent e) {
        String serverAddress = connectionDialog.getServerAddress();
        int serverPort = connectionDialog.getServerPort();
        String username = connectionDialog.getUsername();
        
        // Validate input
        if (username.isEmpty()) {
            connectionDialog.showError("Username cannot be empty");
            return;
        }
        
        // Update model
        model.setServerAddress(serverAddress);
        model.setServerPort(serverPort);
        model.setUsername(username);
        
        // Create and start client
        try {
            client = new Client(serverAddress, serverPort, username, this);
            client.start();
            
            // Update UI
            view.setTitle("Chat - " + username);
            connectionDialog.setConnected(true);
            connectionDialog.dispose();
            view.setVisible(true);
            
            // Add welcome message
            model.addMessage("Connected to server at " + serverAddress + ":" + serverPort);
            model.addMessage("Welcome, " + username + "!");
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error connecting to server", ex);
            connectionDialog.showError("Could not connect to server: " + ex.getMessage());
        }
    }
    
    /**
     * Handles the send message button action.
     * 
     * @param e The action event
     */
    private void handleSendMessage(ActionEvent e) {
        String message = view.getMessage();
        if (message.isEmpty()) {
            return;
        }
        
        // Encrypt the message
        String encryptedMessage = cipher.encrypt(message);
        
        // Send the message
        if (client != null && client.isConnected()) {
            client.sendTextMessage(encryptedMessage);
            
            // Add to local chat (unencrypted for display)
            model.addMessage("You: " + message);
            
            // Clear the input field
            view.clearMessage();
        } else {
            view.showError("Not connected to server");
        }
    }
    
    /**
     * Handles the send file button action.
     * 
     * @param e The action event
     */
    private void handleSendFile(ActionEvent e) {
        File file = view.chooseFile();
        if (file == null) {
            return;
        }
        
        // Check file size (optional limit)
        if (file.length() > 100 * 1024 * 1024) { // 100 MB limit
            view.showError("File is too large (max 100 MB)");
            return;
        }
        
        // Send the file
        if (client != null && client.isConnected()) {
            try {
                byte[] fileData = Files.readAllBytes(file.toPath());
                client.sendFile(file.getName(), fileData);
                
                // Add to local chat
                model.addMessage("You sent a file: " + file.getName());
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Error reading file", ex);
                view.showError("Error reading file: " + ex.getMessage());
            }
        } else {
            view.showError("Not connected to server");
        }
    }
    
    /**
     * Handles disconnection from the server.
     */
    private void handleDisconnect() {
        if (client != null) {
            if (view.showConfirmation("Are you sure you want to disconnect?")) {
                client.disconnect();
                view.dispose();
                System.exit(0);
            }
        } else {
            view.dispose();
            System.exit(0);
        }
    }
    
    /**
     * Handles a received text message.
     * 
     * @param sender The sender's username
     * @param encryptedMessage The encrypted message
     */
    public void handleReceivedTextMessage(String sender, String encryptedMessage) {
        // Decrypt the message
        String decryptedMessage = cipher.decrypt(encryptedMessage);
        
        // Add to chat
        model.addMessage(sender + ": " + decryptedMessage);
    }
    
    /**
     * Handles a received file.
     * 
     * @param sender The sender's username
     * @param fileName The file name
     * @param fileData The file data
     */
    public void handleReceivedFile(String sender, String fileName, byte[] fileData) {
        // Create received_files directory if it doesn't exist
        File receivedDir = new File("received_files");
        if (!receivedDir.exists()) {
            receivedDir.mkdir();
        }
        
        // Generate a unique file name to avoid overwriting
        String timestamp = String.valueOf(System.currentTimeMillis());
        String extension = "";
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = fileName.substring(dotIndex);
        }
        
        String uniqueFileName = "received_" + timestamp + extension;
        File outputFile = new File(receivedDir, uniqueFileName);
        
        // Save the file
        try {
            Files.write(outputFile.toPath(), fileData);
            
            // Add to chat
            model.addMessage(sender + " sent a file: " + fileName + " (saved as " + outputFile.getPath() + ")");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error saving received file", e);
            model.addMessage("Error saving file from " + sender + ": " + e.getMessage());
        }
    }
    
    /**
     * Handles disconnection from the server.
     * 
     * @param reason The reason for disconnection
     */
    public void handleServerDisconnect(String reason) {
        SwingUtilities.invokeLater(() -> {
            model.addMessage("Disconnected from server: " + reason);
            view.showError("Disconnected from server: " + reason);
        });
    }
}

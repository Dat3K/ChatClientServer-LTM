package client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * Model class for the chat client, following the MVC pattern.
 * Stores chat history and notifies observers of changes.
 */
public class ChatModel extends Observable {
    private final List<String> chatHistory;
    private String username;
    private String serverAddress;
    private int serverPort;
    
    /**
     * Constructs a new ChatModel.
     */
    public ChatModel() {
        chatHistory = new ArrayList<>();
    }
    
    /**
     * Adds a message to the chat history and notifies observers.
     * 
     * @param message The message to add
     */
    public void addMessage(String message) {
        chatHistory.add(message);
        setChanged();
        notifyObservers(message);
    }
    
    /**
     * Gets the chat history.
     * 
     * @return The chat history as a list of strings
     */
    public List<String> getChatHistory() {
        return new ArrayList<>(chatHistory);
    }
    
    /**
     * Sets the username.
     * 
     * @param username The username
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * Gets the username.
     * 
     * @return The username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Sets the server address.
     * 
     * @param serverAddress The server address
     */
    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }
    
    /**
     * Gets the server address.
     * 
     * @return The server address
     */
    public String getServerAddress() {
        return serverAddress;
    }
    
    /**
     * Sets the server port.
     * 
     * @param serverPort The server port
     */
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }
    
    /**
     * Gets the server port.
     * 
     * @return The server port
     */
    public int getServerPort() {
        return serverPort;
    }
}

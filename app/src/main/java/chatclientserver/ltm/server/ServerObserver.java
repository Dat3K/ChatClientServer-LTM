package chatclientserver.ltm.server;

import chatclientserver.ltm.model.Message;
import chatclientserver.ltm.model.User;
import chatclientserver.ltm.model.FileTransfer;

/**
 * Interface for observing server events.
 * Implements the Observer pattern for server notifications.
 */
public interface ServerObserver {
    /**
     * Called when a client connects to the server.
     * 
     * @param clientHandler The client handler for the connected client
     */
    void onClientConnected(ClientHandler clientHandler);
    
    /**
     * Called when a client disconnects from the server.
     * 
     * @param clientHandler The client handler for the disconnected client
     */
    void onClientDisconnected(ClientHandler clientHandler);
    
    /**
     * Called when a message is received from a client.
     * 
     * @param clientHandler The client handler for the client that sent the message
     * @param message The message that was received
     */
    void onMessageReceived(ClientHandler clientHandler, Message message);
    
    /**
     * Called when a file transfer is received from a client.
     * 
     * @param clientHandler The client handler for the client that sent the file
     * @param fileTransfer The file transfer that was received
     */
    void onFileTransferReceived(ClientHandler clientHandler, FileTransfer fileTransfer);
    
    /**
     * Called when a key exchange is received from a client.
     * 
     * @param clientHandler The client handler for the client that sent the key
     * @param key The key that was received
     */
    void onKeyExchangeReceived(ClientHandler clientHandler, String key);
    
    /**
     * Called when user information is received from a client.
     * 
     * @param clientHandler The client handler for the client that sent the user info
     * @param user The user information that was received
     */
    void onUserInfoReceived(ClientHandler clientHandler, User user);
    
    /**
     * Called when the server starts.
     * 
     * @param port The port the server is listening on
     */
    void onServerStarted(int port);
    
    /**
     * Called when the server stops.
     */
    void onServerStopped();
    
    /**
     * Called when an error occurs on the server.
     * 
     * @param message The error message
     * @param exception The exception that occurred (can be null)
     */
    void onServerError(String message, Exception exception);
}

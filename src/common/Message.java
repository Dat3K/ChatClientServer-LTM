package common;

import java.io.Serializable;

/**
 * Class representing a message that can be sent between client and server.
 * This class follows the protocol defined in the requirements.
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private MessageType type;
    private String senderUsername;
    private String content;
    private String fileName;
    private byte[] fileData;
    
    /**
     * Creates a username message.
     * 
     * @param username The username
     * @return A new Message object
     */
    public static Message createUsernameMessage(String username) {
        Message message = new Message();
        message.type = MessageType.USERNAME;
        message.senderUsername = username;
        return message;
    }
    
    /**
     * Creates a text message.
     * 
     * @param sender The sender's username
     * @param text The text content (already encrypted)
     * @return A new Message object
     */
    public static Message createTextMessage(String sender, String text) {
        Message message = new Message();
        message.type = MessageType.TEXT;
        message.senderUsername = sender;
        message.content = text;
        return message;
    }
    
    /**
     * Creates a file message.
     * 
     * @param sender The sender's username
     * @param fileName The name of the file
     * @param fileData The file data as a byte array
     * @return A new Message object
     */
    public static Message createFileMessage(String sender, String fileName, byte[] fileData) {
        Message message = new Message();
        message.type = MessageType.FILE;
        message.senderUsername = sender;
        message.fileName = fileName;
        message.fileData = fileData;
        return message;
    }
    
    /**
     * Gets the type of this message.
     * 
     * @return The message type
     */
    public MessageType getType() {
        return type;
    }
    
    /**
     * Gets the sender's username.
     * 
     * @return The sender's username
     */
    public String getSenderUsername() {
        return senderUsername;
    }
    
    /**
     * Gets the text content of this message.
     * 
     * @return The text content
     */
    public String getContent() {
        return content;
    }
    
    /**
     * Gets the file name.
     * 
     * @return The file name
     */
    public String getFileName() {
        return fileName;
    }
    
    /**
     * Gets the file data.
     * 
     * @return The file data as a byte array
     */
    public byte[] getFileData() {
        return fileData;
    }
}

package chatclientserver.ltm.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Model class representing a message in the chat application.
 * This class is used for both sending messages between client and server
 * and for storing messages in the database.
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String clientId;
    private int userId;
    private String encryptedMessage;
    private String key;
    private String decryptedMessage;
    private String phrasePositions;
    private Timestamp timestamp;

    /**
     * Default constructor.
     */
    public Message() {
    }

    /**
     * Constructs a Message with the specified parameters.
     *
     * @param clientId The ID of the client
     * @param encryptedMessage The encrypted message
     * @param key The encryption key
     * @param decryptedMessage The decrypted message
     */
    public Message(String clientId, String encryptedMessage, String key, String decryptedMessage) {
        this.clientId = clientId;
        this.encryptedMessage = encryptedMessage;
        this.key = key;
        this.decryptedMessage = decryptedMessage;
    }

    /**
     * Gets the ID of the message.
     *
     * @return The ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the ID of the message.
     *
     * @param id The ID to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the client ID.
     *
     * @return The client ID
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Sets the client ID.
     *
     * @param clientId The client ID to set
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * Gets the encrypted message.
     *
     * @return The encrypted message
     */
    public String getEncryptedMessage() {
        return encryptedMessage;
    }

    /**
     * Sets the encrypted message.
     *
     * @param encryptedMessage The encrypted message to set
     */
    public void setEncryptedMessage(String encryptedMessage) {
        this.encryptedMessage = encryptedMessage;
    }

    /**
     * Gets the encryption key.
     *
     * @return The key
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the encryption key.
     *
     * @param key The key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Gets the decrypted message.
     *
     * @return The decrypted message
     */
    public String getDecryptedMessage() {
        return decryptedMessage;
    }

    /**
     * Sets the decrypted message.
     *
     * @param decryptedMessage The decrypted message to set
     */
    public void setDecryptedMessage(String decryptedMessage) {
        this.decryptedMessage = decryptedMessage;
    }

    /**
     * Gets the phrase positions.
     *
     * @return The phrase positions
     */
    public String getPhrasePositions() {
        return phrasePositions;
    }

    /**
     * Sets the phrase positions.
     *
     * @param phrasePositions The phrase positions to set
     */
    public void setPhrasePositions(String phrasePositions) {
        this.phrasePositions = phrasePositions;
    }

    /**
     * Gets the timestamp.
     *
     * @return The timestamp
     */
    public Timestamp getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp.
     *
     * @param timestamp The timestamp to set
     */
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Gets the user ID.
     *
     * @return The user ID
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets the user ID.
     *
     * @param userId The user ID to set
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Message [id=" + id + ", clientId=" + clientId + ", userId=" + userId + ", encryptedMessage=" + encryptedMessage + ", key=" + key
                + ", decryptedMessage=" + decryptedMessage + ", phrasePositions=" + phrasePositions + ", timestamp="
                + timestamp + "]";
    }
}

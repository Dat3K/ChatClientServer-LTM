package common;

/**
 * Enum representing the types of messages that can be sent between client and server.
 */
public enum MessageType {
    /**
     * Text message (encrypted with PlayFair cipher)
     */
    TEXT(0),
    
    /**
     * File message (not encrypted)
     */
    FILE(1),
    
    /**
     * Username message (sent when client connects)
     */
    USERNAME(2);
    
    private final int code;
    
    /**
     * Constructs a MessageType with the specified code.
     * 
     * @param code The numeric code for this message type
     */
    MessageType(int code) {
        this.code = code;
    }
    
    /**
     * Gets the numeric code for this message type.
     * 
     * @return The numeric code
     */
    public int getCode() {
        return code;
    }
    
    /**
     * Gets a MessageType from its numeric code.
     * 
     * @param code The numeric code
     * @return The corresponding MessageType, or null if not found
     */
    public static MessageType fromCode(int code) {
        for (MessageType type : values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return null;
    }
}

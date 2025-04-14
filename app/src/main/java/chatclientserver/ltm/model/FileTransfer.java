package chatclientserver.ltm.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Model class representing a file transfer in the chat application.
 * This class is used for both sending file metadata between client and server
 * and for storing file transfer records in the database.
 */
public class FileTransfer implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String clientId;
    private String fileName;
    private long fileSize;
    private String fileType;
    private Timestamp timestamp;
    private byte[] fileData; // Not stored in the database, used only for transfer
    
    /**
     * Default constructor.
     */
    public FileTransfer() {
    }
    
    /**
     * Constructs a FileTransfer with the specified parameters.
     * 
     * @param clientId The ID of the client
     * @param fileName The name of the file
     * @param fileSize The size of the file in bytes
     * @param fileType The type of the file
     */
    public FileTransfer(String clientId, String fileName, long fileSize, String fileType) {
        this.clientId = clientId;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileType = fileType;
    }
    
    /**
     * Gets the ID of the file transfer.
     * 
     * @return The ID
     */
    public int getId() {
        return id;
    }
    
    /**
     * Sets the ID of the file transfer.
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
     * Gets the file name.
     * 
     * @return The file name
     */
    public String getFileName() {
        return fileName;
    }
    
    /**
     * Sets the file name.
     * 
     * @param fileName The file name to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    /**
     * Gets the file size.
     * 
     * @return The file size in bytes
     */
    public long getFileSize() {
        return fileSize;
    }
    
    /**
     * Sets the file size.
     * 
     * @param fileSize The file size to set
     */
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
    
    /**
     * Gets the file type.
     * 
     * @return The file type
     */
    public String getFileType() {
        return fileType;
    }
    
    /**
     * Sets the file type.
     * 
     * @param fileType The file type to set
     */
    public void setFileType(String fileType) {
        this.fileType = fileType;
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
     * Gets the file data.
     * 
     * @return The file data as a byte array
     */
    public byte[] getFileData() {
        return fileData;
    }
    
    /**
     * Sets the file data.
     * 
     * @param fileData The file data to set
     */
    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }
    
    @Override
    public String toString() {
        return "FileTransfer [id=" + id + ", clientId=" + clientId + ", fileName=" + fileName + ", fileSize=" + fileSize
                + ", fileType=" + fileType + ", timestamp=" + timestamp + "]";
    }
}

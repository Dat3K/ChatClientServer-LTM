package chatclientserver.ltm.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import chatclientserver.ltm.model.FileTransfer;

/**
 * Data Access Object for FileTransfer entities.
 * Handles database operations related to file transfers.
 */
public class FileTransferDAO {
    private Connection connection;
    
    /**
     * Constructs a FileTransferDAO with a database connection.
     */
    public FileTransferDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    /**
     * Saves a file transfer record to the database.
     * 
     * @param fileTransfer The file transfer to save
     * @return The ID of the saved file transfer, or -1 if the operation failed
     */
    public int saveFileTransfer(FileTransfer fileTransfer) {
        String sql = "INSERT INTO FileTransfers (client_id, file_name, file_size, file_type) " +
                     "VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, fileTransfer.getClientId());
            statement.setString(2, fileTransfer.getFileName());
            statement.setLong(3, fileTransfer.getFileSize());
            statement.setString(4, fileTransfer.getFileType());
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving file transfer: " + e.getMessage());
        }
        
        return -1;
    }
    
    /**
     * Retrieves a file transfer by its ID.
     * 
     * @param id The ID of the file transfer to retrieve
     * @return The file transfer, or null if not found
     */
    public FileTransfer getFileTransferById(int id) {
        String sql = "SELECT * FROM FileTransfers WHERE id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToFileTransfer(resultSet);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving file transfer: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Retrieves all file transfers for a specific client.
     * 
     * @param clientId The ID of the client
     * @return A list of file transfers
     */
    public List<FileTransfer> getFileTransfersByClientId(String clientId) {
        List<FileTransfer> fileTransfers = new ArrayList<>();
        String sql = "SELECT * FROM FileTransfers WHERE client_id = ? ORDER BY timestamp DESC";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, clientId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    fileTransfers.add(mapResultSetToFileTransfer(resultSet));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving file transfers: " + e.getMessage());
        }
        
        return fileTransfers;
    }
    
    /**
     * Maps a ResultSet to a FileTransfer object.
     * 
     * @param resultSet The ResultSet to map
     * @return The mapped FileTransfer
     * @throws SQLException If an error occurs while accessing the ResultSet
     */
    private FileTransfer mapResultSetToFileTransfer(ResultSet resultSet) throws SQLException {
        FileTransfer fileTransfer = new FileTransfer();
        fileTransfer.setId(resultSet.getInt("id"));
        fileTransfer.setClientId(resultSet.getString("client_id"));
        fileTransfer.setFileName(resultSet.getString("file_name"));
        fileTransfer.setFileSize(resultSet.getLong("file_size"));
        fileTransfer.setFileType(resultSet.getString("file_type"));
        fileTransfer.setTimestamp(resultSet.getTimestamp("timestamp"));
        return fileTransfer;
    }
}

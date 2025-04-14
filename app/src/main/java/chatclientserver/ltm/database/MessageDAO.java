package chatclientserver.ltm.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import chatclientserver.ltm.model.Message;

/**
 * Data Access Object for Message entities.
 * Handles database operations related to messages.
 */
public class MessageDAO {
    private Connection connection;
    
    /**
     * Constructs a MessageDAO with a database connection.
     */
    public MessageDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    /**
     * Saves a message to the database.
     * 
     * @param message The message to save
     * @return The ID of the saved message, or -1 if the operation failed
     */
    public int saveMessage(Message message) {
        String sql = "INSERT INTO Messages (client_id, encrypted_message, key, decrypted_message, phrase_positions) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, message.getClientId());
            statement.setString(2, message.getEncryptedMessage());
            statement.setString(3, message.getKey());
            statement.setString(4, message.getDecryptedMessage());
            statement.setString(5, message.getPhrasePositions());
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving message: " + e.getMessage());
        }
        
        return -1;
    }
    
    /**
     * Retrieves a message by its ID.
     * 
     * @param id The ID of the message to retrieve
     * @return The message, or null if not found
     */
    public Message getMessageById(int id) {
        String sql = "SELECT * FROM Messages WHERE id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToMessage(resultSet);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving message: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Retrieves all messages for a specific client.
     * 
     * @param clientId The ID of the client
     * @return A list of messages
     */
    public List<Message> getMessagesByClientId(String clientId) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM Messages WHERE client_id = ? ORDER BY timestamp DESC";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, clientId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    messages.add(mapResultSetToMessage(resultSet));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving messages: " + e.getMessage());
        }
        
        return messages;
    }
    
    /**
     * Updates the phrase positions for a message.
     * 
     * @param messageId The ID of the message
     * @param phrasePositions The phrase positions to set
     * @return true if the update was successful, false otherwise
     */
    public boolean updatePhrasePositions(int messageId, String phrasePositions) {
        String sql = "UPDATE Messages SET phrase_positions = ? WHERE id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, phrasePositions);
            statement.setInt(2, messageId);
            
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating phrase positions: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Maps a ResultSet to a Message object.
     * 
     * @param resultSet The ResultSet to map
     * @return The mapped Message
     * @throws SQLException If an error occurs while accessing the ResultSet
     */
    private Message mapResultSetToMessage(ResultSet resultSet) throws SQLException {
        Message message = new Message();
        message.setId(resultSet.getInt("id"));
        message.setClientId(resultSet.getString("client_id"));
        message.setEncryptedMessage(resultSet.getString("encrypted_message"));
        message.setKey(resultSet.getString("key"));
        message.setDecryptedMessage(resultSet.getString("decrypted_message"));
        message.setPhrasePositions(resultSet.getString("phrase_positions"));
        message.setTimestamp(resultSet.getTimestamp("timestamp"));
        return message;
    }
}

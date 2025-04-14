package chatclientserver.ltm.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import chatclientserver.ltm.model.User;
import chatclientserver.ltm.util.PasswordUtils;

/**
 * Data Access Object for User entities.
 * Handles database operations related to users.
 */
public class UserDAO {
    private Connection connection;
    
    /**
     * Constructs a UserDAO with a database connection.
     */
    public UserDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    /**
     * Registers a new user.
     * 
     * @param username The username
     * @param password The plain text password (will be hashed)
     * @param email The email address
     * @param fullName The full name
     * @return The ID of the registered user, or -1 if registration failed
     */
    public int registerUser(String username, String password, String email, String fullName) {
        String sql = "INSERT INTO Users (username, password_hash, email, full_name) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Hash the password
            String passwordHash = PasswordUtils.hashPassword(password);
            
            statement.setString(1, username);
            statement.setString(2, passwordHash);
            statement.setString(3, email);
            statement.setString(4, fullName);
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
        }
        
        return -1;
    }
    
    /**
     * Authenticates a user.
     * 
     * @param username The username
     * @param password The plain text password
     * @return The authenticated User, or null if authentication failed
     */
    public User authenticateUser(String username, String password) {
        String sql = "SELECT * FROM Users WHERE username = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String storedHash = resultSet.getString("password_hash");
                    
                    // Verify the password
                    if (PasswordUtils.verifyPassword(password, storedHash)) {
                        User user = mapResultSetToUser(resultSet);
                        
                        // Update last login time
                        updateLastLogin(user.getId());
                        
                        return user;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Updates the last login timestamp for a user.
     * 
     * @param userId The ID of the user
     */
    private void updateLastLogin(int userId) {
        String sql = "UPDATE Users SET last_login = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating last login: " + e.getMessage());
        }
    }
    
    /**
     * Gets a user by ID.
     * 
     * @param id The ID of the user
     * @return The user, or null if not found
     */
    public User getUserById(int id) {
        String sql = "SELECT * FROM Users WHERE id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToUser(resultSet);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Gets a user by username.
     * 
     * @param username The username
     * @return The user, or null if not found
     */
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM Users WHERE username = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToUser(resultSet);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving user: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Gets all users.
     * 
     * @return A list of all users
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users ORDER BY username";
        
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                users.add(mapResultSetToUser(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving users: " + e.getMessage());
        }
        
        return users;
    }
    
    /**
     * Updates a user's profile.
     * 
     * @param user The user to update
     * @return true if the update was successful, false otherwise
     */
    public boolean updateUser(User user) {
        String sql = "UPDATE Users SET email = ?, full_name = ? WHERE id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getFullName());
            statement.setInt(3, user.getId());
            
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Changes a user's password.
     * 
     * @param userId The ID of the user
     * @param oldPassword The old password
     * @param newPassword The new password
     * @return true if the password was changed successfully, false otherwise
     */
    public boolean changePassword(int userId, String oldPassword, String newPassword) {
        // First, verify the old password
        User user = getUserById(userId);
        if (user == null || !PasswordUtils.verifyPassword(oldPassword, user.getPasswordHash())) {
            return false;
        }
        
        // Update the password
        String sql = "UPDATE Users SET password_hash = ? WHERE id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            String newPasswordHash = PasswordUtils.hashPassword(newPassword);
            
            statement.setString(1, newPasswordHash);
            statement.setInt(2, userId);
            
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error changing password: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Checks if a username is available.
     * 
     * @param username The username to check
     * @return true if the username is available, false otherwise
     */
    public boolean isUsernameAvailable(String username) {
        String sql = "SELECT COUNT(*) FROM Users WHERE username = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count == 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking username availability: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Checks if an email is available.
     * 
     * @param email The email to check
     * @return true if the email is available, false otherwise
     */
    public boolean isEmailAvailable(String email) {
        String sql = "SELECT COUNT(*) FROM Users WHERE email = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count == 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking email availability: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Maps a ResultSet to a User object.
     * 
     * @param resultSet The ResultSet to map
     * @return The mapped User
     * @throws SQLException If an error occurs while accessing the ResultSet
     */
    private User mapResultSetToUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setUsername(resultSet.getString("username"));
        user.setPasswordHash(resultSet.getString("password_hash"));
        user.setEmail(resultSet.getString("email"));
        user.setFullName(resultSet.getString("full_name"));
        user.setCreatedAt(resultSet.getTimestamp("created_at"));
        user.setLastLogin(resultSet.getTimestamp("last_login"));
        return user;
    }
}

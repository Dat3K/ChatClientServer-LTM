package chatclientserver.ltm.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Model class representing a user in the chat application.
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String username;
    private String passwordHash;
    private String email;
    private String fullName;
    private Timestamp createdAt;
    private Timestamp lastLogin;
    
    /**
     * Default constructor.
     */
    public User() {
    }
    
    /**
     * Constructs a User with the specified parameters.
     * 
     * @param username The username
     * @param passwordHash The hashed password
     * @param email The email address
     * @param fullName The full name
     */
    public User(String username, String passwordHash, String email, String fullName) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.fullName = fullName;
    }
    
    /**
     * Gets the ID of the user.
     * 
     * @return The ID
     */
    public int getId() {
        return id;
    }
    
    /**
     * Sets the ID of the user.
     * 
     * @param id The ID to set
     */
    public void setId(int id) {
        this.id = id;
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
     * Sets the username.
     * 
     * @param username The username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * Gets the hashed password.
     * 
     * @return The hashed password
     */
    public String getPasswordHash() {
        return passwordHash;
    }
    
    /**
     * Sets the hashed password.
     * 
     * @param passwordHash The hashed password to set
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    /**
     * Gets the email address.
     * 
     * @return The email address
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * Sets the email address.
     * 
     * @param email The email address to set
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * Gets the full name.
     * 
     * @return The full name
     */
    public String getFullName() {
        return fullName;
    }
    
    /**
     * Sets the full name.
     * 
     * @param fullName The full name to set
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    /**
     * Gets the creation timestamp.
     * 
     * @return The creation timestamp
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Sets the creation timestamp.
     * 
     * @param createdAt The creation timestamp to set
     */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Gets the last login timestamp.
     * 
     * @return The last login timestamp
     */
    public Timestamp getLastLogin() {
        return lastLogin;
    }
    
    /**
     * Sets the last login timestamp.
     * 
     * @param lastLogin The last login timestamp to set
     */
    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    @Override
    public String toString() {
        return "User [id=" + id + ", username=" + username + ", email=" + email + ", fullName=" + fullName + "]";
    }
}

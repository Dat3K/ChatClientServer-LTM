package chatclientserver.ltm.database;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Tests for the DatabaseConnection class.
 */
public class DatabaseConnectionTest {
    private static DatabaseConnection dbConnection;
    
    @BeforeAll
    public static void setUp() {
        // Get the singleton instance
        dbConnection = DatabaseConnection.getInstance();
    }
    
    @Test
    public void testConnection() {
        // Get the connection
        Connection connection = dbConnection.getConnection();
        
        // Verify that the connection is not null
        assertNotNull(connection);
        
        // Verify that the connection is valid
        try {
            assertTrue(connection.isValid(5));
        } catch (SQLException e) {
            throw new AssertionError("Connection is not valid: " + e.getMessage());
        }
    }
    
    @Test
    public void testSingleton() {
        // Get another instance
        DatabaseConnection anotherInstance = DatabaseConnection.getInstance();
        
        // Verify that it's the same instance
        assertTrue(dbConnection == anotherInstance);
    }
    
    @AfterAll
    public static void tearDown() {
        // Close the connection
        if (dbConnection != null) {
            dbConnection.closeConnection();
        }
    }
}

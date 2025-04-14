package chatclientserver.ltm.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Singleton class for managing database connections.
 * This class ensures that only one database connection is created and reused.
 */
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;

    // Database connection parameters
    private static final String URL = "jdbc:postgresql://ltm-network-basic.k.aivencloud.com:22871/defaultdb?ssl=require";
    private static final String USER = "avnadmin";
    private static final String PASSWORD = "AVNS_G44nLyiiuMnjzmBoq7r";

    /**
     * Private constructor to prevent instantiation from outside.
     */
    private DatabaseConnection() {
        try {
            // Load the PostgreSQL JDBC driver
            Class.forName("org.postgresql.Driver");

            // Create the connection
            connection = DriverManager.getConnection(URL, USER, PASSWORD);

            // Create tables if they don't exist
            createTablesIfNotExist();

            System.out.println("Database connection established successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        }
    }

    /**
     * Gets the singleton instance of the DatabaseConnection.
     *
     * @return The DatabaseConnection instance
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Gets the database connection.
     *
     * @return The Connection object
     */
    public Connection getConnection() {
        try {
            // Check if connection is closed or invalid
            if (connection == null || connection.isClosed()) {
                // Reconnect
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (SQLException e) {
            System.err.println("Error checking database connection: " + e.getMessage());
        }

        return connection;
    }

    /**
     * Closes the database connection.
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }

    /**
     * Creates the necessary tables if they don't exist.
     */
    private void createTablesIfNotExist() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            // Create Users table
            String createUsersTable = "CREATE TABLE IF NOT EXISTS Users (" +
                    "id SERIAL PRIMARY KEY, " +
                    "username VARCHAR(50) UNIQUE NOT NULL, " +
                    "password_hash TEXT NOT NULL, " +
                    "email VARCHAR(100) UNIQUE NOT NULL, " +
                    "full_name VARCHAR(100), " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "last_login TIMESTAMP" +
                    ")";
            statement.execute(createUsersTable);

            // Create Messages table
            String createMessagesTable = "CREATE TABLE IF NOT EXISTS Messages (" +
                    "id SERIAL PRIMARY KEY, " +
                    "client_id VARCHAR(50) NOT NULL, " +
                    "user_id INT REFERENCES Users(id), " +
                    "encrypted_message TEXT NOT NULL, " +
                    "key VARCHAR(100) NOT NULL, " +
                    "decrypted_message TEXT NOT NULL, " +
                    "phrase_positions TEXT, " +
                    "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            statement.execute(createMessagesTable);

            // Create FileTransfers table
            String createFileTransfersTable = "CREATE TABLE IF NOT EXISTS FileTransfers (" +
                    "id SERIAL PRIMARY KEY, " +
                    "client_id VARCHAR(50) NOT NULL, " +
                    "user_id INT REFERENCES Users(id), " +
                    "file_name VARCHAR(255) NOT NULL, " +
                    "file_size BIGINT NOT NULL, " +
                    "file_type VARCHAR(50), " +
                    "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            statement.execute(createFileTransfersTable);

            System.out.println("Database tables created successfully.");
        }
    }
}

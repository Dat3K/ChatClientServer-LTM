package chatclientserver.ltm.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Date;
import java.util.List;

import chatclientserver.ltm.model.Message;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import chatclientserver.ltm.util.UIUtils;
import chatclientserver.ltm.util.PlaceholderTextField;
import chatclientserver.ltm.util.Constants;
import chatclientserver.ltm.client.ChatClient.MessageListener;
import chatclientserver.ltm.encryption.PlayfairCipher;
import chatclientserver.ltm.model.User;

/**
 * Graphical user interface for the chat client.
 */
public class ClientGUI extends JFrame implements MessageListener {
    private static final long serialVersionUID = 1L;

    private ChatClient chatClient;
    private ChatPanel chatPanel;
    private PlaceholderTextField messageField;
    private PlaceholderTextField keyField;
    private JButton sendButton;
    private JButton fileButton;
    private JButton logoutButton;
    private JButton keyExchangeButton;
    private JLabel statusLabel;
    private JLabel positionsLabel;
    private JLabel userLabel;
    private JPanel statusPanel;
    private JPanel inputPanel;

    /**
     * Constructs the client GUI.
     */
    public ClientGUI() {
        // Set up the look and feel
        UIUtils.setupLookAndFeel();

        // Create the chat client
        chatClient = new ChatClient();
        chatClient.setMessageListener(this);

        // Set up the frame
        setTitle("Chat Client");
        setSize(Constants.GUI_WIDTH, Constants.GUI_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(UIUtils.BACKGROUND_COLOR);

        // Add a window listener to disconnect when the window is closed
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (chatClient.isConnected()) {
                    chatClient.disconnect();
                }
            }
        });

        // Create the components
        initComponents();

        // Layout the components
        layoutComponents();

        // Set up event handlers
        setupEventHandlers();
    }

    /**
     * Initializes the GUI components.
     */
    private void initComponents() {
        // Chat panel
        chatPanel = new ChatPanel();

        // Message field
        messageField = UIUtils.createStyledTextField(Constants.TEXT_FIELD_COLS);
        messageField.setEnabled(false);
        messageField.setPlaceholder("Type your message here...");

        // Key field
        keyField = UIUtils.createStyledTextField(10);
        keyField.setText(Constants.DEFAULT_KEY);

        // Buttons
        sendButton = UIUtils.createStyledButton("Send", true);
        sendButton.setEnabled(false);

        fileButton = UIUtils.createStyledButton("Send File", false);
        fileButton.setEnabled(false);

        logoutButton = UIUtils.createStyledButton("Login", true); // Initially shows "Login"
        logoutButton.setEnabled(true); // Enabled by default for login

        keyExchangeButton = UIUtils.createStyledButton("Exchange Key", false);
        keyExchangeButton.setEnabled(false);

        // Status panel
        statusPanel = UIUtils.createCardPanel();

        // Labels
        statusLabel = UIUtils.createStatusLabel("Not connected", Constants.STATUS_ERROR);

        positionsLabel = UIUtils.createStatusLabel("Positions: ", Constants.STATUS_NORMAL);

        userLabel = UIUtils.createStatusLabel("Not logged in", Constants.STATUS_NORMAL);

        // Input panel
        inputPanel = new JPanel();
        inputPanel.setBackground(UIUtils.CARD_BACKGROUND_COLOR);
        inputPanel.setBorder(UIUtils.PANEL_BORDER);
    }

    /**
     * Lays out the GUI components.
     */
    private void layoutComponents() {
        // Main panel with gradient background
        JPanel mainPanel = UIUtils.createGradientPanel(UIUtils.BACKGROUND_COLOR, new Color(235, 235, 235));
        mainPanel.setLayout(new BorderLayout(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));
        mainPanel.setBorder(new EmptyBorder(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM,
                Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));

        // Set the current user for the chat panel
        if (chatClient.getCurrentUser() != null) {
            chatPanel.setCurrentUser(chatClient.getCurrentUser().getUsername());
        }

        // Input panel layout
        inputPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(Constants.PADDING_SMALL, Constants.PADDING_SMALL,
                Constants.PADDING_SMALL, Constants.PADDING_SMALL);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Message label and field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        JLabel messageLabel = UIUtils.createStyledLabel("Message:", false);
        inputPanel.add(messageLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        inputPanel.add(messageField, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        inputPanel.add(sendButton, gbc);

        // Key label and field
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel keyLabel = UIUtils.createStyledLabel("Key:", false);
        inputPanel.add(keyLabel, gbc);

        gbc.gridx = 1;
        inputPanel.add(keyField, gbc);

        gbc.gridx = 2;
        inputPanel.add(keyExchangeButton, gbc);

        // File button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filePanel.setOpaque(false);
        filePanel.add(fileButton);
        inputPanel.add(filePanel, gbc);

        // Status panel layout
        statusPanel.setLayout(new BorderLayout(Constants.PADDING_SMALL, Constants.PADDING_SMALL));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(
                Constants.PADDING_SMALL, Constants.PADDING_SMALL,
                Constants.PADDING_SMALL, Constants.PADDING_SMALL));

        JPanel connectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        connectionPanel.setOpaque(false);
        connectionPanel.add(statusLabel);
        connectionPanel.add(logoutButton);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);
        userPanel.add(userLabel);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(connectionPanel, BorderLayout.WEST);
        topPanel.add(userPanel, BorderLayout.EAST);

        statusPanel.add(topPanel, BorderLayout.NORTH);
        statusPanel.add(positionsLabel, BorderLayout.SOUTH);

        // Add panels to main panel
        mainPanel.add(chatPanel, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);
        mainPanel.add(statusPanel, BorderLayout.NORTH);

        // Add main panel to frame
        setContentPane(mainPanel);
    }

    /**
     * Sets up the event handlers for the GUI components.
     */
    private void setupEventHandlers() {
        // Logout/Login button
        logoutButton.addActionListener(e -> {
            if (chatClient.isConnected()) {
                // Currently logged in - perform logout
                // Disconnect if connected
                disconnect();

                // Reset user info
                chatClient.setCurrentUser(null);
                updateUserLabel();

                // Change button text to "Login"
                logoutButton.setText("Login");
            } else {
                // Currently logged out - perform login
                showLoginDialog(true);

                // If login was successful, button text will be changed to "Logout" in the login method
            }
        });

        // Send button
        sendButton.addActionListener(e -> sendMessage());

        // Message field (send on Enter)
        messageField.addActionListener(e -> sendMessage());

        // File button
        fileButton.addActionListener(e -> sendFile());

        // Key exchange button
        keyExchangeButton.addActionListener(e -> exchangeKey());

        // loginButton removed
    }

    /**
     * Connects to the server.
     */
    // Server connection information
    private String serverHost;
    private int serverPort;

    /**
     * Connects to the server.
     *
     * @return true if connection was successful, false otherwise
     */
    private boolean connect() {
        // If already authenticated, connect with user info
        User currentUser = chatClient.getCurrentUser();

        // Use the server information from login dialog
        boolean connected = chatClient.connect(serverHost, serverPort, currentUser);

        if (connected) {
            statusLabel.setText("Connected to " + serverHost + ":" + serverPort);
            statusLabel.setForeground(Color.GREEN.darker());
            // Don't change logoutButton here, it's handled in the login method
            messageField.setEnabled(true);
            sendButton.setEnabled(true);
            fileButton.setEnabled(true);
            keyExchangeButton.setEnabled(true);

            // Send the initial key
            chatClient.sendKeyExchange(keyField.getText());

            // Update user label if authenticated
            updateUserLabel();

            // Add a welcome message
            appendToChatArea("Connected to server. You can now send messages and files.");

            return true;
        } else {
            // Don't show error message here, it will be handled by the login method
            return false;
        }
    }

    /**
     * Disconnects from the server.
     */
    private void disconnect() {
        chatClient.disconnect();

        statusLabel.setText("Not connected");
        statusLabel.setForeground(Color.RED);
        // Don't change logoutButton here, it's handled in the action listener
        messageField.setEnabled(false);
        sendButton.setEnabled(false);
        fileButton.setEnabled(false);
        keyExchangeButton.setEnabled(false);

        // Add a disconnection message
        appendToChatArea("Disconnected from server.");
    }

    /**
     * Sends a message to the server.
     */
    private void sendMessage() {
        String message = messageField.getText().trim();
        String key = keyField.getText().trim();

        if (message.isEmpty()) {
            return;
        }

        if (key.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a key for encryption.", "Key Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean sent = chatClient.sendMessage(message, key);

        if (sent) {
            // Add the message to the chat panel as a user message
            User currentUser = chatClient.getCurrentUser();
            String username = currentUser != null ? currentUser.getUsername() : "You";
            chatPanel.addMessage(message, username, new Date());

            // Show the encrypted message as a system message
            PlayfairCipher cipher = new PlayfairCipher(key);
            String encrypted = cipher.encrypt(message);
            chatPanel.addSystemMessage("Encrypted: " + encrypted);

            // Clear the message field
            messageField.setText("");
            messageField.requestFocus();
        } else {
            JOptionPane.showMessageDialog(this, "Could not send the message.", "Send Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Sends a file to the server.
     */
    private void sendFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select a file to send");

        // Add filters for common file types
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Images", "jpg", "jpeg", "png", "gif"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Audio", "mp3", "wav", "ogg"));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Video", "mp4", "avi", "mkv"));
        fileChooser.setAcceptAllFileFilterUsed(true);

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            boolean sent = chatClient.sendFile(selectedFile);

            if (sent) {
                appendToChatArea("You sent file: " + selectedFile.getName() + " (" + selectedFile.length() + " bytes)");
            } else {
                JOptionPane.showMessageDialog(this, "Could not send the file.", "Send Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Exchanges a key with the server.
     */
    private void exchangeKey() {
        String key = keyField.getText().trim();

        if (key.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a key to exchange.", "Key Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean sent = chatClient.sendKeyExchange(key);

        if (sent) {
            appendToChatArea("Key exchanged: " + key);
        } else {
            JOptionPane.showMessageDialog(this, "Could not exchange the key.", "Exchange Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Adds a message to the chat panel.
     *
     * @param message The message to add
     */
    private void appendToChatArea(String message) {
        SwingUtilities.invokeLater(() -> {
            // Add as a system message
            chatPanel.addSystemMessage(message);
        });
    }

    @Override
    public void onPhrasePositionsReceived(String positions) {
        SwingUtilities.invokeLater(() -> {
            positionsLabel.setText("Positions of '" + Constants.SEARCH_PHRASE + "': " + positions);
            appendToChatArea("Server found '" + Constants.SEARCH_PHRASE + "' at positions: " + positions);
        });
    }

    @Override
    public void onKeyExchangeReceived(String key) {
        SwingUtilities.invokeLater(() -> {
            appendToChatArea("Server confirmed key: " + key);
        });
    }

    /**
     * Shows the login dialog and handles the login process.
     * If login is successful, automatically connects to the server.
     * If login is cancelled or fails, exits the application unless isRelogin is true.
     * If connection fails, allows the user to try again.
     *
     * @param isRelogin true if this is a relogin after logout, false for initial login
     */
    public void showLoginDialog(boolean isRelogin) {
        boolean loginSuccessful = login();

        if (loginSuccessful) {
            // Only show the GUI if login and connection were successful
            setVisible(true);
        } else if (!isRelogin) {
            // If login was not successful (user cancelled) and this is not a relogin, exit the application
            int option = JOptionPane.showConfirmDialog(
                this,
                "Do you want to exit the application?",
                "Exit Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );

            if (option == JOptionPane.YES_OPTION) {
                System.exit(0);
            } else {
                // User chose not to exit, try login again
                showLoginDialog(isRelogin);
            }
        }
    }

    /**
     * Shows the login dialog and handles the login process.
     * If login is successful, automatically connects to the server.
     * If login is cancelled or fails, exits the application.
     */
    public void showLoginDialog() {
        showLoginDialog(false);
    }

    /**
     * Shows the login dialog and handles the login process.
     * If login is successful, automatically connects to the server.
     *
     * @return true if login was successful, false otherwise
     */
    private boolean login() {
        LoginDialog loginDialog = new LoginDialog(this);
        loginDialog.setVisible(true);

        if (loginDialog.isLoginSuccessful()) {
            User user = loginDialog.getAuthenticatedUser();
            chatClient.setCurrentUser(user);
            updateUserLabel();

            // Change button text to "Logout" and ensure it's enabled
            logoutButton.setText("Logout");
            logoutButton.setEnabled(true);


            // Get server information from login dialog
            serverHost = loginDialog.getServerHost();
            serverPort = loginDialog.getServerPort();

            // Create a loading dialog
            LoadingDialog loadingDialog = new LoadingDialog(this, "Connecting to server at " + serverHost + ":" + serverPort);

            // Create a final result holder (needed for lambda)
            final boolean[] connectionResult = {false};

            // Use a separate thread to connect so the UI doesn't freeze
            Thread connectThread = new Thread(() -> {
                try {
                    // Try to connect
                    boolean success = connect();
                    connectionResult[0] = success;
                } finally {
                    // Close the loading dialog on the EDT
                    SwingUtilities.invokeLater(() -> loadingDialog.dispose());
                }
            });

            // Start the connection thread
            connectThread.start();

            // Show the loading dialog (this will block until the dialog is closed)
            loadingDialog.setVisible(true);

            // Wait for the connection thread to finish
            try {
                connectThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Check the connection result
            if (!connectionResult[0]) {
                // If connection failed, show error message and try again
                String errorMessage = chatClient.getLastErrorMessage();
                if (errorMessage.isEmpty()) {
                    errorMessage = "Connection to server failed.";
                }

                int option = JOptionPane.showConfirmDialog(
                    this,
                    errorMessage + "\n\nWould you like to try again?",
                    "Connection Error",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE
                );


                if (option == JOptionPane.YES_OPTION) {
                    // Try login again
                    return login();
                } else {
                    // User chose not to try again
                    return false;
                }
            }

            // Load and display chat history
            loadChatHistory();

            JOptionPane.showMessageDialog(this, "Login successful. Welcome, " + user.getUsername() + "!", "Login", JOptionPane.INFORMATION_MESSAGE);
            return true;
        }

        return false;
    }

    /**
     * Updates the user label based on the authentication status.
     */
    private void updateUserLabel() {
        User currentUser = chatClient.getCurrentUser();
        if (currentUser != null) {
            userLabel.setText("Logged in as: " + currentUser.getUsername());
            userLabel.setForeground(Color.BLUE);
            // loginButton removed
        } else {
            userLabel.setText("Not logged in");
            userLabel.setForeground(Color.GRAY);
            // loginButton removed
        }
    }

    /**
     * Loads and displays the chat history.
     */
    private void loadChatHistory() {
        // Clear the chat panel first
        chatPanel.clearMessages();

        // Get chat history for current user only (last 50 messages)
        User currentUser = chatClient.getCurrentUser();
        if (currentUser == null) {
            chatPanel.addSystemMessage("No user logged in.");
            return;
        }

        List<Message> chatHistory = chatClient.getChatHistoryForCurrentUser(50);

        if (chatHistory.isEmpty()) {
            chatPanel.addSystemMessage("No chat history available.");
            return;
        }

        // Display a header
        chatPanel.addSystemMessage("--- Your Chat History ---");

        // Display each message
        for (Message message : chatHistory) {
            // Add the message to the chat panel
            chatPanel.addMessage(
                message.getDecryptedMessage(),
                currentUser.getUsername(),
                message.getTimestamp() != null ? message.getTimestamp() : new Date()
            );
        }

        // Add a separator
        chatPanel.addSystemMessage("--- End of History ---");
    }

    /**
     * The main method to start the client GUI.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClientGUI gui = new ClientGUI();
            gui.setVisible(true);
        });
    }
}

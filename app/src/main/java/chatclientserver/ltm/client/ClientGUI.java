package chatclientserver.ltm.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.formdev.flatlaf.FlatLightLaf;

import chatclientserver.ltm.client.ChatClient.MessageListener;
import chatclientserver.ltm.encryption.PlayfairCipher;
import chatclientserver.ltm.model.User;
import chatclientserver.ltm.util.Constants;

/**
 * Graphical user interface for the chat client.
 */
public class ClientGUI extends JFrame implements MessageListener {
    private static final long serialVersionUID = 1L;

    private ChatClient chatClient;
    private JTextArea chatArea;
    private JTextField messageField;
    private JTextField keyField;
    private JButton sendButton;
    private JButton fileButton;
    private JButton connectButton;
    private JButton keyExchangeButton;
    private JButton loginButton;
    private JLabel statusLabel;
    private JLabel positionsLabel;
    private JLabel userLabel;

    /**
     * Constructs the client GUI.
     */
    public ClientGUI() {
        // Set up the look and feel
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            System.err.println("Failed to initialize LaF: " + e.getMessage());
        }

        // Create the chat client
        chatClient = new ChatClient();
        chatClient.setMessageListener(this);

        // Set up the frame
        setTitle("Chat Client");
        setSize(Constants.GUI_WIDTH, Constants.GUI_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

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
        // Chat area
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // Message field
        messageField = new JTextField(Constants.TEXT_FIELD_COLS);
        messageField.setEnabled(false);

        // Key field
        keyField = new JTextField(10);
        keyField.setText(Constants.DEFAULT_KEY);

        // Buttons
        sendButton = new JButton("Send");
        sendButton.setEnabled(false);

        fileButton = new JButton("Send File");
        fileButton.setEnabled(false);

        connectButton = new JButton("Connect");
        connectButton.setEnabled(false); // Disabled until login

        keyExchangeButton = new JButton("Exchange Key");
        keyExchangeButton.setEnabled(false);

        loginButton = new JButton("Login");
        loginButton.setEnabled(true);

        // Labels
        statusLabel = new JLabel("Not connected");
        statusLabel.setForeground(Color.RED);

        positionsLabel = new JLabel("Positions: ");

        userLabel = new JLabel("Not logged in");
        userLabel.setForeground(Color.GRAY);
    }

    /**
     * Lays out the GUI components.
     */
    private void layoutComponents() {
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Chat panel
        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        chatScrollPane.setPreferredSize(new Dimension(400, 300));

        // Input panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Message label and field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        inputPanel.add(new JLabel("Message:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        inputPanel.add(messageField, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        inputPanel.add(sendButton, gbc);

        // Key label and field
        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Key:"), gbc);

        gbc.gridx = 1;
        inputPanel.add(keyField, gbc);

        gbc.gridx = 2;
        inputPanel.add(keyExchangeButton, gbc);

        // File button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filePanel.add(fileButton);
        inputPanel.add(filePanel, gbc);

        // Status panel
        JPanel statusPanel = new JPanel(new BorderLayout(5, 5));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel connectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        connectionPanel.add(statusLabel);
        connectionPanel.add(connectButton);
        connectionPanel.add(loginButton);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.add(userLabel);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(connectionPanel, BorderLayout.WEST);
        topPanel.add(userPanel, BorderLayout.EAST);

        statusPanel.add(topPanel, BorderLayout.NORTH);
        statusPanel.add(positionsLabel, BorderLayout.SOUTH);

        // Add panels to main panel
        mainPanel.add(chatScrollPane, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);
        mainPanel.add(statusPanel, BorderLayout.NORTH);

        // Add main panel to frame
        setContentPane(mainPanel);
    }

    /**
     * Sets up the event handlers for the GUI components.
     */
    private void setupEventHandlers() {
        // Connect button
        connectButton.addActionListener(e -> {
            if (!chatClient.isConnected()) {
                connect();
            } else {
                disconnect();
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

        // Login button
        loginButton.addActionListener(e -> login());
    }

    /**
     * Connects to the server.
     */
    private void connect() {
        // If already authenticated, connect with user info
        User currentUser = chatClient.getCurrentUser();
        boolean connected = chatClient.connect(Constants.SERVER_HOST, Constants.SERVER_PORT, currentUser);

        if (connected) {
            statusLabel.setText("Connected to " + Constants.SERVER_HOST + ":" + Constants.SERVER_PORT);
            statusLabel.setForeground(Color.GREEN.darker());
            connectButton.setText("Disconnect");
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
        } else {
            JOptionPane.showMessageDialog(this, "Could not connect to the server.", "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Disconnects from the server.
     */
    private void disconnect() {
        chatClient.disconnect();

        statusLabel.setText("Not connected");
        statusLabel.setForeground(Color.RED);
        connectButton.setText("Connect");
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
            // Show the message in the chat area
            appendToChatArea("You: " + message);

            // Show the encrypted message
            PlayfairCipher cipher = new PlayfairCipher(key);
            String encrypted = cipher.encrypt(message);
            appendToChatArea("Encrypted: " + encrypted);

            // Clear the message field
            messageField.setText("");
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
     * Appends a message to the chat area.
     *
     * @param message The message to append
     */
    private void appendToChatArea(String message) {
        SwingUtilities.invokeLater(() -> {
            // Add timestamp
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String timestamp = sdf.format(new Date());

            chatArea.append("[" + timestamp + "] " + message + "\n");

            // Scroll to the bottom
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
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
     * If login is cancelled or fails, exits the application.
     */
    public void showLoginDialog() {
        boolean loginSuccessful = login();

        // If login was not successful, exit the application
        if (!loginSuccessful) {
            System.exit(0);
        }
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

            // Enable connect button after successful login
            connectButton.setEnabled(true);

            JOptionPane.showMessageDialog(this, "Login successful. Welcome, " + user.getUsername() + "!", "Login", JOptionPane.INFORMATION_MESSAGE);

            // Automatically connect to the server if not already connected
            if (!chatClient.isConnected()) {
                connect();
            }

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
            loginButton.setText("Change User");
        } else {
            userLabel.setText("Not logged in");
            userLabel.setForeground(Color.GRAY);
            loginButton.setText("Login");
        }
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

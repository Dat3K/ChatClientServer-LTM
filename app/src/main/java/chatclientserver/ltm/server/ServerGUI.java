package chatclientserver.ltm.server;

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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.formdev.flatlaf.FlatLightLaf;

import chatclientserver.ltm.model.FileTransfer;
import chatclientserver.ltm.model.Message;
import chatclientserver.ltm.model.User;
import chatclientserver.ltm.util.Constants;

/**
 * Graphical user interface for the chat server.
 * This class displays server status, connected clients, and message logs.
 */
public class ServerGUI extends JFrame implements ServerObserver {
    private static final long serialVersionUID = 1L;

    // Server components
    private ChatServer server;
    private int port;

    // GUI components
    private JLabel statusLabel;
    private JLabel portLabel;
    private JLabel clientsLabel;
    private JButton startButton;
    private JButton stopButton;
    private JTextField portField;
    private JTextArea logArea;
    private JList<String> clientList;
    private DefaultListModel<String> clientListModel;
    private JTabbedPane tabbedPane;
    private JTextArea messagesArea;
    private JTextArea searchResultsArea;
    private JTextArea fileTransfersArea;
    private JTextArea clientDetailsArea;

    // Data storage
    private Map<String, ClientHandler> clientHandlers;

    /**
     * Constructs the server GUI.
     */
    public ServerGUI() {
        // Set up the look and feel
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            System.err.println("Failed to initialize LaF: " + e.getMessage());
        }

        // Initialize data storage
        clientHandlers = new HashMap<>();

        // Set up the frame
        setTitle("Chat Server");
        setSize(Constants.GUI_WIDTH, Constants.GUI_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Add a window listener to stop the server when the window is closed
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (server != null && isServerRunning()) {
                    server.stop();
                }
            }
        });

        // Create the components
        initComponents();

        // Layout the components
        layoutComponents();

        // Set up event handlers
        setupEventHandlers();

        // Initialize the server
        server = ChatServer.getInstance();
        server.addObserver(this);

        // Set the default port
        port = Constants.DEFAULT_SERVER_PORT;
        portField.setText(String.valueOf(port));
    }

    /**
     * Initializes the GUI components.
     */
    private void initComponents() {
        // Status components
        statusLabel = new JLabel("Server is stopped");
        statusLabel.setForeground(Color.RED);

        portLabel = new JLabel("Port:");
        portField = new JTextField(5);
        portField.setText(String.valueOf(Constants.DEFAULT_SERVER_PORT));

        clientsLabel = new JLabel("Connected clients: 0");

        startButton = new JButton("Start Server");
        stopButton = new JButton("Stop Server");
        stopButton.setEnabled(false);

        // Log area
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        // Client list
        clientListModel = new DefaultListModel<>();
        clientList = new JList<>(clientListModel);
        clientList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        // Tabbed pane for details
        tabbedPane = new JTabbedPane();

        // Messages tab
        messagesArea = new JTextArea();
        messagesArea.setEditable(false);
        messagesArea.setLineWrap(true);
        messagesArea.setWrapStyleWord(true);
        messagesArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        // Search results tab
        searchResultsArea = new JTextArea();
        searchResultsArea.setEditable(false);
        searchResultsArea.setLineWrap(true);
        searchResultsArea.setWrapStyleWord(true);
        searchResultsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        // File transfers tab
        fileTransfersArea = new JTextArea();
        fileTransfersArea.setEditable(false);
        fileTransfersArea.setLineWrap(true);
        fileTransfersArea.setWrapStyleWord(true);
        fileTransfersArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        // Client details area
        clientDetailsArea = new JTextArea();
        clientDetailsArea.setEditable(false);
        clientDetailsArea.setLineWrap(true);
        clientDetailsArea.setWrapStyleWord(true);
        clientDetailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
    }

    /**
     * Lays out the GUI components.
     */
    private void layoutComponents() {
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Status panel
        JPanel statusPanel = new JPanel(new GridBagLayout());
        statusPanel.setBorder(BorderFactory.createTitledBorder("Server Status"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Status label
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        statusPanel.add(statusLabel, gbc);

        // Port label and field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        statusPanel.add(portLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        statusPanel.add(portField, gbc);

        // Clients label
        gbc.gridx = 2;
        gbc.weightx = 0.0;
        statusPanel.add(clientsLabel, gbc);

        // Start and stop buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        statusPanel.add(buttonPanel, gbc);

        // Log panel
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logScrollPane.setPreferredSize(new Dimension(400, 150));
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("Server Log"));
        logPanel.add(logScrollPane, BorderLayout.CENTER);

        // Client list panel
        JScrollPane clientListScrollPane = new JScrollPane(clientList);
        clientListScrollPane.setPreferredSize(new Dimension(200, 300));
        JPanel clientListPanel = new JPanel(new BorderLayout());
        clientListPanel.setBorder(BorderFactory.createTitledBorder("Connected Clients"));
        clientListPanel.add(clientListScrollPane, BorderLayout.CENTER);

        // Client details panel
        JScrollPane clientDetailsScrollPane = new JScrollPane(clientDetailsArea);
        JPanel clientDetailsPanel = new JPanel(new BorderLayout());
        clientDetailsPanel.setBorder(BorderFactory.createTitledBorder("Client Details"));
        clientDetailsPanel.add(clientDetailsScrollPane, BorderLayout.CENTER);

        // Messages tab
        JScrollPane messagesScrollPane = new JScrollPane(messagesArea);
        tabbedPane.addTab("Messages", messagesScrollPane);

        // Search results tab
        JScrollPane searchResultsScrollPane = new JScrollPane(searchResultsArea);
        tabbedPane.addTab("Search Results", searchResultsScrollPane);

        // File transfers tab
        JScrollPane fileTransfersScrollPane = new JScrollPane(fileTransfersArea);
        tabbedPane.addTab("File Transfers", fileTransfersScrollPane);

        // Split pane for client list and details
        JSplitPane clientSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, clientListPanel, clientDetailsPanel);
        clientSplitPane.setResizeWeight(0.3);

        // Split pane for client info and tabbed pane
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, clientSplitPane, tabbedPane);
        mainSplitPane.setResizeWeight(0.4);

        // Add panels to main panel
        mainPanel.add(statusPanel, BorderLayout.NORTH);
        mainPanel.add(logPanel, BorderLayout.SOUTH);
        mainPanel.add(mainSplitPane, BorderLayout.CENTER);

        // Add main panel to frame
        setContentPane(mainPanel);
    }

    /**
     * Sets up the event handlers for the GUI components.
     */
    private void setupEventHandlers() {
        // Start button
        startButton.addActionListener(e -> startServer());

        // Stop button
        stopButton.addActionListener(e -> stopServer());

        // Client list selection
        clientList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    String selectedClient = clientList.getSelectedValue();
                    if (selectedClient != null) {
                        showClientDetails(selectedClient);
                    }
                }
            }
        });
    }

    /**
     * Starts the server.
     */
    private void startServer() {
        try {
            // Get the port from the text field
            try {
                port = Integer.parseInt(portField.getText().trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid port number. Using default port " + Constants.DEFAULT_SERVER_PORT, "Invalid Port", JOptionPane.WARNING_MESSAGE);
                port = Constants.DEFAULT_SERVER_PORT;
                portField.setText(String.valueOf(port));
            }

            // Start the server
            server.start(port);

            // Update the UI
            statusLabel.setText("Server is running on port " + port);
            statusLabel.setForeground(Color.GREEN.darker());
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            portField.setEnabled(false);

            // Log the event
            logMessage("Server started on port " + port);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error starting server: " + e.getMessage(), "Server Error", JOptionPane.ERROR_MESSAGE);
            logMessage("Error starting server: " + e.getMessage());
        }
    }

    /**
     * Automatically starts the server without user interaction.
     * This is used when starting both client and server together.
     *
     * @return true if the server was started successfully, false otherwise
     */
    public boolean autoStartServer() {
        try {
            // Use the default port
            port = Constants.DEFAULT_SERVER_PORT;
            portField.setText(String.valueOf(port));

            // Start the server
            server.start(port);

            // Update the UI
            statusLabel.setText("Server is running on port " + port);
            statusLabel.setForeground(Color.GREEN.darker());
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            portField.setEnabled(false);

            // Log the event
            logMessage("Server automatically started on port " + port);

            return true;
        } catch (IOException e) {
            logMessage("Error automatically starting server: " + e.getMessage());
            return false;
        }
    }

    /**
     * Stops the server.
     */
    private void stopServer() {
        // Stop the server
        server.stop();

        // Update the UI
        statusLabel.setText("Server is stopped");
        statusLabel.setForeground(Color.RED);
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        portField.setEnabled(true);

        // Clear the client list
        clientListModel.clear();
        clientHandlers.clear();
        clientDetailsArea.setText("");

        // Update the clients label
        clientsLabel.setText("Connected clients: 0");

        // Log the event
        logMessage("Server stopped");
    }

    /**
     * Checks if the server is running.
     *
     * @return true if the server is running, false otherwise
     */
    private boolean isServerRunning() {
        return stopButton.isEnabled();
    }

    /**
     * Shows the details of the selected client.
     *
     * @param clientId The ID of the client to show details for
     */
    private void showClientDetails(String clientId) {
        ClientHandler clientHandler = clientHandlers.get(clientId);
        if (clientHandler != null) {
            StringBuilder details = new StringBuilder();
            details.append("Client ID: ").append(clientHandler.getClientId()).append("\n");
            details.append("IP Address: ").append(clientHandler.getClientIpAddress()).append("\n");

            User user = clientHandler.getCurrentUser();
            if (user != null) {
                details.append("\nUser Information:\n");
                details.append("  Username: ").append(user.getUsername()).append("\n");
                details.append("  Full Name: ").append(user.getFullName()).append("\n");
                details.append("  Email: ").append(user.getEmail()).append("\n");
                details.append("  Created: ").append(user.getCreatedAt()).append("\n");
                details.append("  Last Login: ").append(user.getLastLogin()).append("\n");
            } else {
                details.append("\nUser not authenticated\n");
            }

            clientDetailsArea.setText(details.toString());
        } else {
            clientDetailsArea.setText("Client not found");
        }
    }

    /**
     * Logs a message to the log area.
     *
     * @param message The message to log
     */
    private void logMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            // Add timestamp
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String timestamp = sdf.format(new Date());

            logArea.append("[" + timestamp + "] " + message + "\n");

            // Scroll to the bottom
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    @Override
    public void onClientConnected(ClientHandler clientHandler) {
        SwingUtilities.invokeLater(() -> {
            // Add the client to the list
            String clientId = clientHandler.getClientId();
            String clientInfo = clientId + " (" + clientHandler.getClientIpAddress() + ")";
            clientListModel.addElement(clientInfo);
            clientHandlers.put(clientInfo, clientHandler);

            // Update the clients label
            clientsLabel.setText("Connected clients: " + clientListModel.size());

            // Log the event
            logMessage("Client connected: " + clientHandler.getClientIpAddress());
        });
    }

    @Override
    public void onClientDisconnected(ClientHandler clientHandler) {
        SwingUtilities.invokeLater(() -> {
            // Remove the client from the list
            String clientId = clientHandler.getClientId();
            String clientToRemove = null;

            for (int i = 0; i < clientListModel.size(); i++) {
                String clientInfo = clientListModel.getElementAt(i);
                if (clientInfo.startsWith(clientId)) {
                    clientToRemove = clientInfo;
                    break;
                }
            }

            if (clientToRemove != null) {
                clientListModel.removeElement(clientToRemove);
                clientHandlers.remove(clientToRemove);

                // Clear the details area if this client was selected
                if (clientList.getSelectedValue() == null) {
                    clientDetailsArea.setText("");
                }
            }

            // Update the clients label
            clientsLabel.setText("Connected clients: " + clientListModel.size());

            // Log the event
            logMessage("Client disconnected: " + clientHandler.getClientIpAddress());
        });
    }

    @Override
    public void onMessageReceived(ClientHandler clientHandler, Message message) {
        SwingUtilities.invokeLater(() -> {
            // Add the message to the messages area
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String timestamp = sdf.format(new Date());

            StringBuilder messageInfo = new StringBuilder();
            messageInfo.append("[").append(timestamp).append("] ");

            User user = clientHandler.getCurrentUser();
            if (user != null) {
                messageInfo.append(user.getUsername());
            } else {
                messageInfo.append("Anonymous");
            }

            messageInfo.append(" (").append(clientHandler.getClientIpAddress()).append("): ");
            messageInfo.append("\n  Encrypted: ").append(message.getEncryptedMessage());
            messageInfo.append("\n  Key: ").append(message.getKey());
            messageInfo.append("\n  Decrypted: ").append(message.getDecryptedMessage());
            messageInfo.append("\n  Positions: ").append(message.getPhrasePositions());
            messageInfo.append("\n");

            messagesArea.append(messageInfo.toString());

            // Scroll to the bottom
            messagesArea.setCaretPosition(messagesArea.getDocument().getLength());

            // Log the event
            logMessage("Message received from " + clientHandler.getClientIpAddress());

            // Add to search results if positions were found
            String positions = message.getPhrasePositions();
            if (positions != null && !positions.equals("Not found")) {
                StringBuilder searchInfo = new StringBuilder();
                searchInfo.append("[").append(timestamp).append("] ");

                if (user != null) {
                    searchInfo.append(user.getUsername());
                } else {
                    searchInfo.append("Anonymous");
                }

                searchInfo.append(" (").append(clientHandler.getClientIpAddress()).append("): ");
                searchInfo.append("\n  Message: ").append(message.getDecryptedMessage());
                searchInfo.append("\n  Positions of '").append(Constants.SEARCH_PHRASE).append("': ").append(positions);
                searchInfo.append("\n");

                searchResultsArea.append(searchInfo.toString());

                // Scroll to the bottom
                searchResultsArea.setCaretPosition(searchResultsArea.getDocument().getLength());
            }
        });
    }

    @Override
    public void onFileTransferReceived(ClientHandler clientHandler, FileTransfer fileTransfer) {
        SwingUtilities.invokeLater(() -> {
            // Add the file transfer to the file transfers area
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String timestamp = sdf.format(new Date());

            StringBuilder fileInfo = new StringBuilder();
            fileInfo.append("[").append(timestamp).append("] ");

            User user = clientHandler.getCurrentUser();
            if (user != null) {
                fileInfo.append(user.getUsername());
            } else {
                fileInfo.append("Anonymous");
            }

            fileInfo.append(" (").append(clientHandler.getClientIpAddress()).append("): ");
            fileInfo.append("\n  File: ").append(fileTransfer.getFileName());
            fileInfo.append("\n  Size: ").append(fileTransfer.getFileSize()).append(" bytes");
            fileInfo.append("\n");

            fileTransfersArea.append(fileInfo.toString());

            // Scroll to the bottom
            fileTransfersArea.setCaretPosition(fileTransfersArea.getDocument().getLength());

            // Log the event
            logMessage("File received from " + clientHandler.getClientIpAddress() + ": " + fileTransfer.getFileName() + " (" + fileTransfer.getFileSize() + " bytes)");
        });
    }

    @Override
    public void onKeyExchangeReceived(ClientHandler clientHandler, String key) {
        SwingUtilities.invokeLater(() -> {
            // Log the event
            logMessage("Key exchange received from " + clientHandler.getClientIpAddress() + ": " + key);
        });
    }

    @Override
    public void onUserInfoReceived(ClientHandler clientHandler, User user) {
        SwingUtilities.invokeLater(() -> {
            // Update the client list if this client is already in the list
            String clientId = clientHandler.getClientId();
            String clientToUpdate = null;

            for (int i = 0; i < clientListModel.size(); i++) {
                String clientInfo = clientListModel.getElementAt(i);
                if (clientInfo.startsWith(clientId)) {
                    clientToUpdate = clientInfo;
                    break;
                }
            }

            if (clientToUpdate != null) {
                int index = clientListModel.indexOf(clientToUpdate);
                String newClientInfo = clientId + " (" + clientHandler.getClientIpAddress() + ") - " + user.getUsername();
                clientListModel.set(index, newClientInfo);
                clientHandlers.remove(clientToUpdate);
                clientHandlers.put(newClientInfo, clientHandler);

                // Update the details area if this client was selected
                if (clientList.getSelectedValue() != null && clientList.getSelectedValue().equals(clientToUpdate)) {
                    showClientDetails(newClientInfo);
                }
            }

            // Log the event
            logMessage("User authenticated: " + user.getUsername() + " from " + clientHandler.getClientIpAddress());
        });
    }

    @Override
    public void onServerStarted(int port) {
        SwingUtilities.invokeLater(() -> {
            // Update the UI
            statusLabel.setText("Server is running on port " + port);
            statusLabel.setForeground(Color.GREEN.darker());
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            portField.setEnabled(false);

            // Log the event
            logMessage("Server started on port " + port);
        });
    }

    @Override
    public void onServerStopped() {
        SwingUtilities.invokeLater(() -> {
            // Update the UI
            statusLabel.setText("Server is stopped");
            statusLabel.setForeground(Color.RED);
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            portField.setEnabled(true);

            // Clear the client list
            clientListModel.clear();
            clientHandlers.clear();
            clientDetailsArea.setText("");

            // Update the clients label
            clientsLabel.setText("Connected clients: 0");

            // Log the event
            logMessage("Server stopped");
        });
    }

    @Override
    public void onServerError(String message, Exception exception) {
        SwingUtilities.invokeLater(() -> {
            // Log the error
            if (exception != null) {
                logMessage("ERROR: " + message + " - " + exception.getMessage());
            } else {
                logMessage("ERROR: " + message);
            }
        });
    }

    /**
     * The main method to start the server GUI.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ServerGUI gui = new ServerGUI();
            gui.setVisible(true);
        });
    }
}

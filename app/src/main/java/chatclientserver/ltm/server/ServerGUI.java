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
import chatclientserver.ltm.util.UIUtils;

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
        UIUtils.setupLookAndFeel();

        // Initialize data storage
        clientHandlers = new HashMap<>();

        // Set up the frame
        setTitle("Chat Server");
        setSize(Constants.GUI_WIDTH, Constants.GUI_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(UIUtils.BACKGROUND_COLOR);

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
        statusLabel = UIUtils.createStatusLabel("Server is stopped", Constants.STATUS_ERROR);

        portLabel = UIUtils.createStyledLabel("Port:", false);
        portField = UIUtils.createStyledTextField(5);
        portField.setText(String.valueOf(Constants.DEFAULT_SERVER_PORT));

        clientsLabel = UIUtils.createStyledLabel("Connected clients: 0", false);

        startButton = UIUtils.createStyledButton("Start Server", true);
        stopButton = UIUtils.createStyledButton("Stop Server", false);
        stopButton.setEnabled(false);

        // Log area
        logArea = UIUtils.createStyledTextArea(10, 50);
        logArea.setEditable(false);
        logArea.setFont(UIUtils.MONOSPACED_FONT);

        // Client list
        clientListModel = new DefaultListModel<>();
        clientList = new JList<>(clientListModel);
        clientList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        clientList.setFont(UIUtils.BODY_FONT);
        clientList.setBackground(UIUtils.CARD_BACKGROUND_COLOR);
        clientList.setBorder(UIUtils.FIELD_BORDER);

        // Tabbed pane for details
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIUtils.BODY_FONT);

        // Messages tab
        messagesArea = UIUtils.createStyledTextArea(15, 50);
        messagesArea.setEditable(false);
        messagesArea.setFont(UIUtils.MONOSPACED_FONT);

        // Search results tab
        searchResultsArea = UIUtils.createStyledTextArea(15, 50);
        searchResultsArea.setEditable(false);
        searchResultsArea.setFont(UIUtils.MONOSPACED_FONT);

        // File transfers tab
        fileTransfersArea = UIUtils.createStyledTextArea(15, 50);
        fileTransfersArea.setEditable(false);
        fileTransfersArea.setFont(UIUtils.MONOSPACED_FONT);

        // Client details area
        clientDetailsArea = UIUtils.createStyledTextArea(15, 50);
        clientDetailsArea.setEditable(false);
        clientDetailsArea.setFont(UIUtils.MONOSPACED_FONT);
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

        // Status panel
        JPanel statusPanel = UIUtils.createCardPanel();
        statusPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(Constants.PADDING_SMALL, Constants.PADDING_SMALL,
                Constants.PADDING_SMALL, Constants.PADDING_SMALL);
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
        buttonPanel.setOpaque(false);
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        statusPanel.add(buttonPanel, gbc);

        // Log panel
        JScrollPane logScrollPane = UIUtils.createStyledScrollPane(logArea);
        logScrollPane.setPreferredSize(new Dimension(400, 150));
        JPanel logPanel = UIUtils.createCardPanel();
        logPanel.setLayout(new BorderLayout());
        logPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Server Log"),
                BorderFactory.createEmptyBorder(Constants.PADDING_SMALL, Constants.PADDING_SMALL,
                        Constants.PADDING_SMALL, Constants.PADDING_SMALL)));
        logPanel.add(logScrollPane, BorderLayout.CENTER);

        // Client list panel
        JScrollPane clientListScrollPane = UIUtils.createStyledScrollPane(clientList);
        clientListScrollPane.setPreferredSize(new Dimension(200, 300));
        JPanel clientListPanel = UIUtils.createCardPanel();
        clientListPanel.setLayout(new BorderLayout());
        clientListPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Connected Clients"),
                BorderFactory.createEmptyBorder(Constants.PADDING_SMALL, Constants.PADDING_SMALL,
                        Constants.PADDING_SMALL, Constants.PADDING_SMALL)));
        clientListPanel.add(clientListScrollPane, BorderLayout.CENTER);

        // Client details panel
        JScrollPane clientDetailsScrollPane = UIUtils.createStyledScrollPane(clientDetailsArea);
        JPanel clientDetailsPanel = UIUtils.createCardPanel();
        clientDetailsPanel.setLayout(new BorderLayout());
        clientDetailsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Client Details"),
                BorderFactory.createEmptyBorder(Constants.PADDING_SMALL, Constants.PADDING_SMALL,
                        Constants.PADDING_SMALL, Constants.PADDING_SMALL)));
        clientDetailsPanel.add(clientDetailsScrollPane, BorderLayout.CENTER);

        // Messages tab
        JScrollPane messagesScrollPane = UIUtils.createStyledScrollPane(messagesArea);
        tabbedPane.addTab("Messages", messagesScrollPane);

        // Search results tab
        JScrollPane searchResultsScrollPane = UIUtils.createStyledScrollPane(searchResultsArea);
        tabbedPane.addTab("Search Results", searchResultsScrollPane);

        // File transfers tab
        JScrollPane fileTransfersScrollPane = UIUtils.createStyledScrollPane(fileTransfersArea);
        tabbedPane.addTab("File Transfers", fileTransfersScrollPane);

        // Split pane for client list and details
        JSplitPane clientSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, clientListPanel, clientDetailsPanel);
        clientSplitPane.setResizeWeight(0.3);
        clientSplitPane.setBorder(null);
        clientSplitPane.setDividerSize(5);

        // Split pane for client info and tabbed pane
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, clientSplitPane, tabbedPane);
        mainSplitPane.setResizeWeight(0.4);
        mainSplitPane.setBorder(null);
        mainSplitPane.setDividerSize(5);

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
            statusLabel.setForeground(UIUtils.SUCCESS_COLOR);
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
            statusLabel.setForeground(UIUtils.SUCCESS_COLOR);
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
            statusLabel.setForeground(UIUtils.ERROR_COLOR);
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

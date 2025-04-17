package chatclientserver.ltm.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;

import chatclientserver.ltm.database.UserDAO;
import chatclientserver.ltm.model.User;
import chatclientserver.ltm.util.Constants;
import chatclientserver.ltm.util.PlaceholderTextField;
import chatclientserver.ltm.util.UIUtils;

/**
 * Dialog for user login.
 */
public class LoginDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private PlaceholderTextField usernameField;
    private JPasswordField passwordField;
    private PlaceholderTextField serverHostField;
    private PlaceholderTextField serverPortField;
    private JButton loginButton;
    private JButton registerButton;
    private JButton cancelButton;

    private UserDAO userDAO;
    private User authenticatedUser;
    private boolean loginSuccessful;
    private String serverHost;
    private int serverPort;

    /**
     * Constructs a LoginDialog.
     *
     * @param parent The parent frame
     */
    public LoginDialog(JFrame parent) {
        super(parent, "Login", true);

        userDAO = new UserDAO();

        // Prevent closing the dialog with the X button
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        // Set up the look and feel
        UIUtils.setupLookAndFeel();
        setBackground(UIUtils.BACKGROUND_COLOR);

        initComponents();
        layoutComponents();
        setupEventHandlers();

        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    /**
     * Initializes the dialog components.
     */
    private void initComponents() {
        usernameField = UIUtils.createStyledTextField(20);
        usernameField.setPlaceholder("Enter your username");

        passwordField = new JPasswordField(20);
        passwordField.setFont(UIUtils.BODY_FONT);
        passwordField.setBorder(UIUtils.FIELD_BORDER);
        passwordField.setPreferredSize(new java.awt.Dimension(passwordField.getPreferredSize().width, Constants.INPUT_HEIGHT));

        // Server connection fields with default values
        serverHostField = UIUtils.createStyledTextField(20);
        serverHostField.setText("localhost");
        serverHostField.setPlaceholder("Enter server address");

        serverPortField = UIUtils.createStyledTextField(5);
        serverPortField.setText("8888");
        serverPortField.setPlaceholder("Port");

        loginButton = UIUtils.createStyledButton("Login", true);
        registerButton = UIUtils.createStyledButton("Register", false);
        cancelButton = UIUtils.createStyledButton("Cancel", false);
    }

    /**
     * Lays out the dialog components.
     */
    private void layoutComponents() {
        // Main panel with gradient background
        JPanel mainPanel = UIUtils.createGradientPanel(UIUtils.BACKGROUND_COLOR, new Color(235, 235, 235));
        mainPanel.setLayout(new BorderLayout(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM));
        mainPanel.setBorder(new EmptyBorder(Constants.PADDING_LARGE, Constants.PADDING_LARGE,
                Constants.PADDING_LARGE, Constants.PADDING_LARGE));

        // Card panel for input fields
        JPanel cardPanel = UIUtils.createCardPanel();
        cardPanel.setLayout(new GridBagLayout());

        // Input panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM,
                Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = UIUtils.createStyledLabel("Login to Chat", true);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        inputPanel.add(titleLabel, gbc);

        // Username
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        inputPanel.add(UIUtils.createStyledLabel("Username:", false), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        inputPanel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        inputPanel.add(UIUtils.createStyledLabel("Password:", false), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        inputPanel.add(passwordField, gbc);

        // Server Host
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        inputPanel.add(UIUtils.createStyledLabel("Server Host:", false), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        inputPanel.add(serverHostField, gbc);

        // Server Port
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        inputPanel.add(UIUtils.createStyledLabel("Server Port:", false), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        inputPanel.add(serverPortField, gbc);

        // Add input panel to card panel
        cardPanel.add(inputPanel);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);

        // Add panels to main panel
        mainPanel.add(cardPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Set content pane
        setContentPane(mainPanel);
    }

    /**
     * Sets up the event handlers.
     */
    private void setupEventHandlers() {
        // Login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        // Register button
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                register();
            }
        });

        // Cancel button - exit application
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = JOptionPane.showConfirmDialog(
                    LoginDialog.this,
                    "Are you sure you want to exit? You must log in to use the application.",
                    "Exit Confirmation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );

                if (option == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        // Enter key in password field
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
    }

    /**
     * Attempts to log in the user.
     */
    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String host = serverHostField.getText().trim();
        String portStr = serverPortField.getText().trim();

        // Validate login credentials
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate server information
        if (host.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a server host.", "Connection Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int port;
        try {
            port = Integer.parseInt(portStr);
            if (port <= 0 || port > 65535) {
                throw new NumberFormatException("Port must be between 1 and 65535");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid port number (1-65535).", "Connection Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Authenticate user
        authenticatedUser = userDAO.authenticateUser(username, password);

        if (authenticatedUser != null) {
            // Store server information
            serverHost = host;
            serverPort = port;

            loginSuccessful = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Error", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }

    /**
     * Opens the registration dialog.
     */
    private void register() {
        RegisterDialog registerDialog = new RegisterDialog(this);
        registerDialog.setVisible(true);

        if (registerDialog.isRegistrationSuccessful()) {
            User registeredUser = registerDialog.getRegisteredUser();
            usernameField.setText(registeredUser.getUsername());
            passwordField.setText("");
            JOptionPane.showMessageDialog(this, "Registration successful. You can now log in.", "Registration", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Checks if the login was successful.
     *
     * @return true if the login was successful, false otherwise
     */
    public boolean isLoginSuccessful() {
        return loginSuccessful;
    }

    /**
     * Gets the authenticated user.
     *
     * @return The authenticated user, or null if authentication failed
     */
    public User getAuthenticatedUser() {
        return authenticatedUser;
    }

    /**
     * Gets the server host entered by the user.
     *
     * @return The server host
     */
    public String getServerHost() {
        return serverHost;
    }

    /**
     * Gets the server port entered by the user.
     *
     * @return The server port
     */
    public int getServerPort() {
        return serverPort;
    }
}

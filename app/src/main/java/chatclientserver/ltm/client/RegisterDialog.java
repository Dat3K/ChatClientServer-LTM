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
 * Dialog for user registration.
 */
public class RegisterDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private PlaceholderTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private PlaceholderTextField emailField;
    private PlaceholderTextField fullNameField;
    private JButton registerButton;
    private JButton cancelButton;

    private UserDAO userDAO;
    private User registeredUser;
    private boolean registrationSuccessful;

    /**
     * Constructs a RegisterDialog.
     *
     * @param parent The parent dialog
     */
    public RegisterDialog(JDialog parent) {
        super(parent, "Register", true);

        userDAO = new UserDAO();

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

        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(UIUtils.BODY_FONT);
        confirmPasswordField.setBorder(UIUtils.FIELD_BORDER);
        confirmPasswordField.setPreferredSize(new java.awt.Dimension(confirmPasswordField.getPreferredSize().width, Constants.INPUT_HEIGHT));

        emailField = UIUtils.createStyledTextField(20);
        emailField.setPlaceholder("Enter your email address");

        fullNameField = UIUtils.createStyledTextField(20);
        fullNameField.setPlaceholder("Enter your full name (optional)");

        registerButton = UIUtils.createStyledButton("Register", true);
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
        JLabel titleLabel = UIUtils.createStyledLabel("Create New Account", true);
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

        // Confirm Password
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        inputPanel.add(UIUtils.createStyledLabel("Confirm Password:", false), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        inputPanel.add(confirmPasswordField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        inputPanel.add(UIUtils.createStyledLabel("Email:", false), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        inputPanel.add(emailField, gbc);

        // Full Name
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0;
        inputPanel.add(UIUtils.createStyledLabel("Full Name:", false), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        inputPanel.add(fullNameField, gbc);

        // Add input panel to card panel
        cardPanel.add(inputPanel);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
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
        // Register button
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                register();
            }
        });

        // Cancel button
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    /**
     * Attempts to register a new user.
     */
    private void register() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String email = emailField.getText().trim();
        String fullName = fullNameField.getText().trim();

        // Validate input
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Registration Error", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
            confirmPasswordField.setText("");
            return;
        }

        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.", "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check if username is available
        if (!userDAO.isUsernameAvailable(username)) {
            JOptionPane.showMessageDialog(this, "Username is already taken.", "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check if email is available
        if (!userDAO.isEmailAvailable(email)) {
            JOptionPane.showMessageDialog(this, "Email is already registered.", "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Register the user
        int userId = userDAO.registerUser(username, password, email, fullName);

        if (userId > 0) {
            registeredUser = userDAO.getUserById(userId);
            registrationSuccessful = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed. Please try again.", "Registration Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Validates an email address.
     *
     * @param email The email address to validate
     * @return true if the email is valid, false otherwise
     */
    private boolean isValidEmail(String email) {
        // Simple email validation
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    /**
     * Checks if the registration was successful.
     *
     * @return true if the registration was successful, false otherwise
     */
    public boolean isRegistrationSuccessful() {
        return registrationSuccessful;
    }

    /**
     * Gets the registered user.
     *
     * @return The registered user, or null if registration failed
     */
    public User getRegisteredUser() {
        return registeredUser;
    }
}

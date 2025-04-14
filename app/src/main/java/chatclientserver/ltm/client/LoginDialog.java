package chatclientserver.ltm.client;

import java.awt.BorderLayout;
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
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import chatclientserver.ltm.database.UserDAO;
import chatclientserver.ltm.model.User;

/**
 * Dialog for user login.
 */
public class LoginDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JButton cancelButton;

    private UserDAO userDAO;
    private User authenticatedUser;
    private boolean loginSuccessful;

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
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);

        loginButton = new JButton("Login");
        registerButton = new JButton("Register");
        cancelButton = new JButton("Cancel");
    }

    /**
     * Lays out the dialog components.
     */
    private void layoutComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Input panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        inputPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        inputPanel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        inputPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        inputPanel.add(passwordField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);

        // Add panels to main panel
        mainPanel.add(inputPanel, BorderLayout.CENTER);
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

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        authenticatedUser = userDAO.authenticateUser(username, password);

        if (authenticatedUser != null) {
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
}

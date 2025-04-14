package client.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Dialog for entering server connection details and username.
 */
public class ConnectionDialog extends JDialog {
    private final JTextField serverField;
    private final JTextField portField;
    private final JTextField usernameField;
    private final JButton connectButton;
    private final JButton cancelButton;
    
    private boolean connected;
    
    /**
     * Constructs a new ConnectionDialog.
     * 
     * @param parent The parent frame
     */
    public ConnectionDialog(Frame parent) {
        super(parent, "Connect to Server", true);
        
        // Create components
        JLabel serverLabel = new JLabel("Server Address:");
        serverField = new JTextField("localhost", 20);
        
        JLabel portLabel = new JLabel("Server Port:");
        portField = new JTextField("12345", 5);
        
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(15);
        
        connectButton = new JButton("Connect");
        cancelButton = new JButton("Cancel");
        
        // Set up layout
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(serverLabel, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(serverField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(portLabel, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(portField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(usernameLabel, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(usernameField, gbc);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(connectButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(buttonPanel, gbc);
        
        // Set up default button and cancel action
        getRootPane().setDefaultButton(connectButton);
        cancelButton.addActionListener(e -> {
            connected = false;
            dispose();
        });
        
        // Set up dialog properties
        setContentPane(panel);
        setResizable(false);
        pack();
        setLocationRelativeTo(parent);
    }
    
    /**
     * Sets the action listener for the connect button.
     * 
     * @param listener The action listener
     */
    public void setConnectAction(ActionListener listener) {
        connectButton.addActionListener(listener);
    }
    
    /**
     * Gets the server address entered by the user.
     * 
     * @return The server address
     */
    public String getServerAddress() {
        return serverField.getText().trim();
    }
    
    /**
     * Gets the server port entered by the user.
     * 
     * @return The server port
     */
    public int getServerPort() {
        try {
            return Integer.parseInt(portField.getText().trim());
        } catch (NumberFormatException e) {
            return 12345; // Default port
        }
    }
    
    /**
     * Gets the username entered by the user.
     * 
     * @return The username
     */
    public String getUsername() {
        return usernameField.getText().trim();
    }
    
    /**
     * Sets the connected flag.
     * 
     * @param connected The connected flag
     */
    public void setConnected(boolean connected) {
        this.connected = connected;
    }
    
    /**
     * Checks if the user successfully connected.
     * 
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        return connected;
    }
    
    /**
     * Shows an error message.
     * 
     * @param message The error message
     */
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Connection Error", JOptionPane.ERROR_MESSAGE);
    }
}

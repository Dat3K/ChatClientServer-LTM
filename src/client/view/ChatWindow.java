package client.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Observable;
import java.util.Observer;

/**
 * Main chat window for the client, displaying chat history and providing controls for sending messages and files.
 */
public class ChatWindow extends JFrame implements Observer {
    private final JTextArea chatArea;
    private final JTextField messageField;
    private final JButton sendButton;
    private final JButton fileButton;
    private final JScrollPane scrollPane;
    
    /**
     * Constructs a new ChatWindow.
     * 
     * @param title The window title
     */
    public ChatWindow(String title) {
        super(title);
        
        // Create components
        chatArea = new JTextArea(20, 50);
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        
        scrollPane = new JScrollPane(chatArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        messageField = new JTextField(40);
        sendButton = new JButton("Send");
        fileButton = new JButton("Send File");
        
        // Set up layout
        JPanel inputPanel = new JPanel();
        inputPanel.add(messageField);
        inputPanel.add(sendButton);
        inputPanel.add(fileButton);
        
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
        
        // Set up window properties
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        
        // Set default button
        getRootPane().setDefaultButton(sendButton);
    }
    
    /**
     * Sets the action listener for the send button.
     * 
     * @param listener The action listener
     */
    public void setSendAction(ActionListener listener) {
        sendButton.addActionListener(listener);
        messageField.addActionListener(listener); // Allow pressing Enter to send
    }
    
    /**
     * Sets the action listener for the file button.
     * 
     * @param listener The action listener
     */
    public void setFileAction(ActionListener listener) {
        fileButton.addActionListener(listener);
    }
    
    /**
     * Sets the window close listener.
     * 
     * @param listener The window adapter
     */
    public void setCloseAction(WindowAdapter listener) {
        addWindowListener(listener);
    }
    
    /**
     * Gets the message entered by the user.
     * 
     * @return The message text
     */
    public String getMessage() {
        return messageField.getText();
    }
    
    /**
     * Clears the message field.
     */
    public void clearMessage() {
        messageField.setText("");
        messageField.requestFocus();
    }
    
    /**
     * Adds a message to the chat area.
     * 
     * @param message The message to add
     */
    public void addMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(message + "\n");
            // Scroll to the bottom
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }
    
    /**
     * Shows a file chooser dialog for selecting a file to send.
     * 
     * @return The selected file, or null if canceled
     */
    public java.io.File chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }
    
    /**
     * Shows an error message.
     * 
     * @param message The error message
     */
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Shows a confirmation dialog.
     * 
     * @param message The confirmation message
     * @return true if confirmed, false otherwise
     */
    public boolean showConfirmation(String message) {
        int result = JOptionPane.showConfirmDialog(this, message, "Confirmation", JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }
    
    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof String) {
            addMessage((String) arg);
        }
    }
}

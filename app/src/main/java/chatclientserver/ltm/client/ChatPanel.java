package chatclientserver.ltm.client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Date;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import chatclientserver.ltm.util.UIUtils;

/**
 * A custom panel that displays chat messages with bubbles.
 */
public class ChatPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private JPanel messagesPanel;
    private JScrollPane scrollPane;
    private String currentUser;
    
    /**
     * Constructs a ChatPanel.
     */
    public ChatPanel() {
        setLayout(new BorderLayout());
        
        // Create the messages panel
        messagesPanel = new JPanel();
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        messagesPanel.setBackground(UIUtils.BACKGROUND_COLOR);
        
        // Add some padding at the bottom for aesthetics
        messagesPanel.add(Box.createVerticalStrut(10));
        
        // Create the scroll pane
        scrollPane = new JScrollPane(messagesPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(UIUtils.BACKGROUND_COLOR);
        
        // Add the scroll pane to this panel
        add(scrollPane, BorderLayout.CENTER);
    }
    
    /**
     * Sets the current user's name.
     * 
     * @param username The current user's name
     */
    public void setCurrentUser(String username) {
        this.currentUser = username;
    }
    
    /**
     * Adds a message to the chat panel.
     * 
     * @param message The message text
     * @param sender The sender's name
     * @param timestamp The timestamp
     */
    public void addMessage(String message, String sender, Date timestamp) {
        boolean isCurrentUser = sender.equals(currentUser);
        
        // Create a panel to hold the message bubble
        JPanel bubbleContainer = new JPanel(new FlowLayout(
                isCurrentUser ? FlowLayout.RIGHT : FlowLayout.LEFT, 10, 5));
        bubbleContainer.setOpaque(false);
        
        // Create the message bubble
        MessageBubble bubble = new MessageBubble(
                message, sender, timestamp, isCurrentUser, 500);
        
        // Add the bubble to the container
        bubbleContainer.add(bubble);
        
        // Add the container to the messages panel
        messagesPanel.add(bubbleContainer);
        
        // Revalidate and repaint
        messagesPanel.revalidate();
        messagesPanel.repaint();
        
        // Scroll to the bottom
        SwingUtilities.invokeLater(() -> {
            scrollPane.getVerticalScrollBar().setValue(
                    scrollPane.getVerticalScrollBar().getMaximum());
        });
    }
    
    /**
     * Adds a system message to the chat panel.
     * 
     * @param message The system message
     */
    public void addSystemMessage(String message) {
        // Create a panel to hold the system message
        JPanel systemMessagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        systemMessagePanel.setOpaque(false);
        
        // Create the system message component
        SystemMessageComponent systemMessage = new SystemMessageComponent(message);
        
        // Add the system message to the panel
        systemMessagePanel.add(systemMessage);
        
        // Add the panel to the messages panel
        messagesPanel.add(systemMessagePanel);
        
        // Revalidate and repaint
        messagesPanel.revalidate();
        messagesPanel.repaint();
        
        // Scroll to the bottom
        SwingUtilities.invokeLater(() -> {
            scrollPane.getVerticalScrollBar().setValue(
                    scrollPane.getVerticalScrollBar().getMaximum());
        });
    }
    
    /**
     * Clears all messages from the chat panel.
     */
    public void clearMessages() {
        messagesPanel.removeAll();
        messagesPanel.add(Box.createVerticalStrut(10));
        messagesPanel.revalidate();
        messagesPanel.repaint();
    }
    
    /**
     * A component that displays a system message.
     */
    private class SystemMessageComponent extends JPanel {
        private static final long serialVersionUID = 1L;
        
        /**
         * Constructs a SystemMessageComponent.
         * 
         * @param message The system message
         */
        public SystemMessageComponent(String message) {
            setLayout(new BorderLayout());
            setOpaque(false);
            
            // Create a label for the message
            javax.swing.JLabel label = new javax.swing.JLabel(message);
            label.setFont(UIUtils.SMALL_FONT);
            label.setForeground(UIUtils.TEXT_SECONDARY_COLOR);
            
            // Add the label to this panel
            add(label, BorderLayout.CENTER);
            
            // Set the preferred size
            setPreferredSize(new Dimension(label.getPreferredSize().width + 20, 
                    label.getPreferredSize().height + 10));
        }
    }
}

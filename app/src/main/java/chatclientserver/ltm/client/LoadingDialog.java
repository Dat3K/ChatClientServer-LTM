package chatclientserver.ltm.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * A dialog that displays a loading indicator and message.
 */
public class LoadingDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    
    private JLabel messageLabel;
    private JProgressBar progressBar;
    
    /**
     * Constructs a LoadingDialog.
     * 
     * @param parent The parent frame
     * @param message The message to display
     */
    public LoadingDialog(JFrame parent, String message) {
        super(parent, "Loading", true);
        
        // Set up the dialog
        setUndecorated(true); // No title bar
        setSize(300, 100);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // Cannot be closed
        
        // Create components
        messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setPreferredSize(new Dimension(250, 20));
        
        // Layout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        JPanel messagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        messagePanel.add(messageLabel);
        
        JPanel progressPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        progressPanel.add(progressBar);
        
        mainPanel.add(messagePanel, BorderLayout.CENTER);
        mainPanel.add(progressPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    /**
     * Updates the message displayed in the dialog.
     * 
     * @param message The new message to display
     */
    public void setMessage(String message) {
        messageLabel.setText(message);
    }
}

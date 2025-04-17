package chatclientserver.ltm.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import chatclientserver.ltm.util.Constants;
import chatclientserver.ltm.util.UIUtils;

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
        setSize(350, 120);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // Cannot be closed

        // Create components
        messageLabel = UIUtils.createStyledLabel(message, false);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setPreferredSize(new Dimension(300, 20));
        progressBar.setForeground(UIUtils.PRIMARY_COLOR);
        progressBar.setBackground(Color.WHITE);
        progressBar.setBorder(BorderFactory.createEmptyBorder());

        // Create a custom panel with gradient background
        JPanel mainPanel = new JPanel(new BorderLayout(Constants.PADDING_MEDIUM, Constants.PADDING_MEDIUM)) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                // Create a gradient from top to bottom
                GradientPaint gp = new GradientPaint(
                    0, 0, UIUtils.BACKGROUND_COLOR,
                    0, getHeight(), new Color(235, 235, 235));

                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        // Add a subtle shadow border
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(Constants.PADDING_LARGE, Constants.PADDING_LARGE,
                    Constants.PADDING_LARGE, Constants.PADDING_LARGE)
        ));

        JPanel messagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        messagePanel.setOpaque(false);
        messagePanel.add(messageLabel);

        JPanel progressPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        progressPanel.setOpaque(false);
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

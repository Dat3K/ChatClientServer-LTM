package chatclientserver.ltm.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JPanel;

import chatclientserver.ltm.util.UIUtils;

/**
 * A custom component that displays a message in a chat bubble.
 */
public class MessageBubble extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private String message;
    private String timestamp;
    private String sender;
    private boolean isCurrentUser;
    private List<String> lines;
    private int maxWidth;
    private int padding = 10;
    private int cornerRadius = 15;
    private Color bubbleColor;
    private Color textColor;
    
    /**
     * Constructs a MessageBubble.
     * 
     * @param message The message text
     * @param sender The sender's name
     * @param timestamp The timestamp
     * @param isCurrentUser Whether the message is from the current user
     * @param maxWidth The maximum width of the bubble
     */
    public MessageBubble(String message, String sender, Date timestamp, boolean isCurrentUser, int maxWidth) {
        this.message = message;
        this.sender = sender;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        this.timestamp = sdf.format(timestamp);
        this.isCurrentUser = isCurrentUser;
        this.maxWidth = maxWidth;
        
        // Set colors based on sender
        if (isCurrentUser) {
            bubbleColor = UIUtils.PRIMARY_COLOR;
            textColor = Color.WHITE;
        } else {
            bubbleColor = new Color(240, 240, 240);
            textColor = UIUtils.TEXT_COLOR;
        }
        
        // Set opaque to false for transparent background
        setOpaque(false);
        
        // Calculate the lines and preferred size
        calculateLines();
    }
    
    /**
     * Calculates the lines of text to display and sets the preferred size.
     */
    private void calculateLines() {
        lines = new ArrayList<>();
        
        // Get font metrics
        Font font = UIUtils.BODY_FONT;
        FontMetrics fm = getFontMetrics(font);
        
        // Calculate the maximum width for the text
        int textMaxWidth = maxWidth - (padding * 2);
        
        // Split the message into words
        String[] words = message.split("\\s+");
        
        // Build lines
        StringBuilder currentLine = new StringBuilder();
        for (String word : words) {
            // Check if adding this word would exceed the max width
            String testLine = currentLine.length() > 0 ? currentLine + " " + word : word;
            int testWidth = fm.stringWidth(testLine);
            
            if (testWidth <= textMaxWidth) {
                // Add the word to the current line
                if (currentLine.length() > 0) {
                    currentLine.append(" ");
                }
                currentLine.append(word);
            } else {
                // Add the current line to the list and start a new line
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                } else {
                    // The word itself is too long, split it
                    lines.add(word);
                }
            }
        }
        
        // Add the last line
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }
        
        // Calculate the preferred size
        int width = 0;
        for (String line : lines) {
            int lineWidth = fm.stringWidth(line);
            if (lineWidth > width) {
                width = lineWidth;
            }
        }
        
        // Add padding and ensure minimum width for timestamp
        width = Math.max(width, fm.stringWidth(sender + " • " + timestamp)) + (padding * 2);
        
        // Calculate height (lines + header + padding)
        int lineHeight = fm.getHeight();
        int height = (lines.size() * lineHeight) + lineHeight + (padding * 2);
        
        // Set the preferred size
        setPreferredSize(new Dimension(width, height));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        
        // Draw the bubble
        g2d.setColor(bubbleColor);
        RoundRectangle2D bubble = new RoundRectangle2D.Float(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);
        g2d.fill(bubble);
        
        // Draw the text
        g2d.setColor(textColor);
        g2d.setFont(UIUtils.BODY_FONT);
        
        FontMetrics fm = g2d.getFontMetrics();
        int lineHeight = fm.getHeight();
        
        // Draw the header (sender and timestamp)
        g2d.setFont(UIUtils.SMALL_FONT.deriveFont(Font.BOLD));
        FontMetrics headerFm = g2d.getFontMetrics();
        String header = sender + " • " + timestamp;
        g2d.drawString(header, padding, padding + headerFm.getAscent());
        
        // Draw the message lines
        g2d.setFont(UIUtils.BODY_FONT);
        fm = g2d.getFontMetrics();
        int y = padding + headerFm.getHeight() + 5;
        
        for (String line : lines) {
            g2d.drawString(line, padding, y + fm.getAscent());
            y += lineHeight;
        }
        
        g2d.dispose();
    }
}

package chatclientserver.ltm.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

/**
 * A text field that displays a placeholder text when empty.
 */
public class PlaceholderTextField extends JTextField implements FocusListener {
    private static final long serialVersionUID = 1L;
    
    private String placeholder;
    private boolean showPlaceholder;
    private Color placeholderColor = new Color(160, 160, 160);
    
    /**
     * Constructs a PlaceholderTextField.
     */
    public PlaceholderTextField() {
        super();
        setupPlaceholder();
    }
    
    /**
     * Constructs a PlaceholderTextField with the specified number of columns.
     * 
     * @param columns The number of columns
     */
    public PlaceholderTextField(int columns) {
        super(columns);
        setupPlaceholder();
    }
    
    /**
     * Constructs a PlaceholderTextField with the specified text and number of columns.
     * 
     * @param text The initial text
     * @param columns The number of columns
     */
    public PlaceholderTextField(String text, int columns) {
        super(text, columns);
        setupPlaceholder();
    }
    
    /**
     * Sets up the placeholder functionality.
     */
    private void setupPlaceholder() {
        showPlaceholder = true;
        addFocusListener(this);
    }
    
    /**
     * Sets the placeholder text.
     * 
     * @param placeholder The placeholder text
     */
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }
    
    /**
     * Gets the placeholder text.
     * 
     * @return The placeholder text
     */
    public String getPlaceholder() {
        return placeholder;
    }
    
    /**
     * Sets the placeholder text color.
     * 
     * @param color The placeholder text color
     */
    public void setPlaceholderColor(Color color) {
        this.placeholderColor = color;
        repaint();
    }
    
    /**
     * Gets the placeholder text color.
     * 
     * @return The placeholder text color
     */
    public Color getPlaceholderColor() {
        return placeholderColor;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // If the text field is empty and not focused, and a placeholder is set, draw the placeholder
        if (showPlaceholder && getText().isEmpty() && placeholder != null && !placeholder.isEmpty() && !hasFocus()) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(placeholderColor);
            g2d.setFont(getFont().deriveFont(Font.ITALIC));
            
            int padding = (getHeight() - g2d.getFontMetrics().getHeight()) / 2 + g2d.getFontMetrics().getAscent();
            g2d.drawString(placeholder, getInsets().left, padding);
            
            g2d.dispose();
        }
    }
    
    @Override
    public void focusGained(FocusEvent e) {
        showPlaceholder = false;
        repaint();
    }
    
    @Override
    public void focusLost(FocusEvent e) {
        showPlaceholder = true;
        repaint();
    }
}

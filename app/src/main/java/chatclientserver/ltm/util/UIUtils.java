package chatclientserver.ltm.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

/**
 * Utility class for UI-related functionality.
 * Provides methods for consistent styling and UI enhancements.
 */
public class UIUtils {
    // Color scheme
    public static final Color PRIMARY_COLOR = new Color(25, 118, 210);
    public static final Color PRIMARY_DARK_COLOR = new Color(21, 101, 192);
    public static final Color PRIMARY_LIGHT_COLOR = new Color(66, 165, 245);
    public static final Color ACCENT_COLOR = new Color(255, 171, 64);
    public static final Color TEXT_COLOR = new Color(33, 33, 33);
    public static final Color TEXT_SECONDARY_COLOR = new Color(117, 117, 117);
    public static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    public static final Color CARD_BACKGROUND_COLOR = Color.WHITE;
    public static final Color SUCCESS_COLOR = new Color(76, 175, 80);
    public static final Color ERROR_COLOR = new Color(244, 67, 54);
    public static final Color WARNING_COLOR = new Color(255, 152, 0);
    public static final Color INFO_COLOR = new Color(33, 150, 243);

    // Fonts
    public static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 18);
    public static final Font SUBTITLE_FONT = new Font("SansSerif", Font.BOLD, 16);
    public static final Font BODY_FONT = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font SMALL_FONT = new Font("SansSerif", Font.PLAIN, 12);
    public static final Font MONOSPACED_FONT = new Font("Monospaced", Font.PLAIN, 13);

    // Borders
    public static final Border PANEL_BORDER = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10));

    public static final Border FIELD_BORDER = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8));

    /**
     * Creates a styled button with hover effects.
     *
     * @param text The button text
     * @param primary Whether to use primary styling
     * @return A styled JButton
     */
    public static JButton createStyledButton(String text, boolean primary) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(primary ? PRIMARY_DARK_COLOR : Color.LIGHT_GRAY);
                } else if (getModel().isRollover()) {
                    g2.setColor(primary ? PRIMARY_COLOR : new Color(240, 240, 240));
                } else {
                    g2.setColor(primary ? PRIMARY_COLOR : Color.WHITE);
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(primary ? Color.WHITE : TEXT_COLOR);
                g2.drawString(getText(), getWidth() / 2 - g2.getFontMetrics().stringWidth(getText()) / 2,
                        getHeight() / 2 + g2.getFontMetrics().getAscent() / 2 - 1);
                g2.dispose();
            }
        };

        button.setForeground(primary ? Color.WHITE : TEXT_COLOR);
        button.setBackground(primary ? PRIMARY_COLOR : Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setFont(BODY_FONT);
        button.setPreferredSize(new Dimension(button.getPreferredSize().width + 20, 36));

        return button;
    }

    /**
     * Creates a styled text field with placeholder support.
     *
     * @param columns The number of columns
     * @return A styled PlaceholderTextField
     */
    public static PlaceholderTextField createStyledTextField(int columns) {
        PlaceholderTextField textField = new PlaceholderTextField(columns);
        textField.setFont(BODY_FONT);
        textField.setBorder(FIELD_BORDER);
        textField.setPreferredSize(new Dimension(textField.getPreferredSize().width, 36));
        return textField;
    }

    /**
     * Creates a styled text area.
     *
     * @param rows The number of rows
     * @param columns The number of columns
     * @return A styled JTextArea
     */
    public static JTextArea createStyledTextArea(int rows, int columns) {
        JTextArea textArea = new JTextArea(rows, columns);
        textArea.setFont(BODY_FONT);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        return textArea;
    }

    /**
     * Creates a styled scroll pane.
     *
     * @param component The component to scroll
     * @return A styled JScrollPane
     */
    public static JScrollPane createStyledScrollPane(Component component) {
        JScrollPane scrollPane = new JScrollPane(component);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    /**
     * Creates a styled label.
     *
     * @param text The label text
     * @param isTitle Whether this is a title label
     * @return A styled JLabel
     */
    public static JLabel createStyledLabel(String text, boolean isTitle) {
        JLabel label = new JLabel(text);
        label.setFont(isTitle ? TITLE_FONT : BODY_FONT);
        label.setForeground(isTitle ? PRIMARY_COLOR : TEXT_COLOR);
        return label;
    }

    /**
     * Creates a styled status label.
     *
     * @param text The label text
     * @param status The status type (0: normal, 1: success, 2: error, 3: warning, 4: info)
     * @return A styled status JLabel
     */
    public static JLabel createStatusLabel(String text, int status) {
        JLabel label = new JLabel(text);
        label.setFont(BODY_FONT);

        switch (status) {
            case 1: // Success
                label.setForeground(SUCCESS_COLOR);
                break;
            case 2: // Error
                label.setForeground(ERROR_COLOR);
                break;
            case 3: // Warning
                label.setForeground(WARNING_COLOR);
                break;
            case 4: // Info
                label.setForeground(INFO_COLOR);
                break;
            default: // Normal
                label.setForeground(TEXT_COLOR);
        }

        return label;
    }

    /**
     * Creates a panel with a gradient background.
     *
     * @param startColor The start color of the gradient
     * @param endColor The end color of the gradient
     * @return A JPanel with a gradient background
     */
    public static JPanel createGradientPanel(Color startColor, Color endColor) {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, startColor, 0, getHeight(), endColor);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
    }

    /**
     * Creates a card panel with shadow effect.
     *
     * @return A JPanel styled as a card
     */
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        return panel;
    }

    /**
     * Adds hover effect to a component.
     *
     * @param component The component to add hover effect to
     * @param hoverColor The color to use on hover
     */
    public static void addHoverEffect(JComponent component, Color hoverColor) {
        Color originalColor = component.getBackground();
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                component.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                component.setBackground(originalColor);
            }
        });
    }

    /**
     * Sets up a consistent look and feel for the application.
     */
    public static void setupLookAndFeel() {
        try {
            // Set system properties for FlatLaf
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("ProgressBar.arc", 10);
            UIManager.put("TextComponent.arc", 10);

            // Colors
            UIManager.put("Button.background", Color.WHITE);
            UIManager.put("Button.foreground", TEXT_COLOR);
            UIManager.put("Button.focusedBackground", new Color(240, 240, 240));
            UIManager.put("Button.hoverBackground", new Color(245, 245, 245));
            UIManager.put("Button.pressedBackground", new Color(230, 230, 230));

            UIManager.put("TabbedPane.selectedBackground", PRIMARY_LIGHT_COLOR);
            UIManager.put("TabbedPane.selectedForeground", Color.WHITE);
            UIManager.put("TabbedPane.underlineColor", PRIMARY_COLOR);

            UIManager.put("TextField.background", Color.WHITE);
            UIManager.put("TextField.foreground", TEXT_COLOR);
            UIManager.put("TextField.caretForeground", PRIMARY_COLOR);
            UIManager.put("TextField.selectionBackground", PRIMARY_LIGHT_COLOR);
            UIManager.put("TextField.selectionForeground", Color.WHITE);

            UIManager.put("TextArea.background", Color.WHITE);
            UIManager.put("TextArea.foreground", TEXT_COLOR);
            UIManager.put("TextArea.caretForeground", PRIMARY_COLOR);
            UIManager.put("TextArea.selectionBackground", PRIMARY_LIGHT_COLOR);
            UIManager.put("TextArea.selectionForeground", Color.WHITE);

            // Apply FlatLaf
            com.formdev.flatlaf.FlatLightLaf.setup();
        } catch (Exception e) {
            System.err.println("Failed to initialize LaF: " + e.getMessage());
        }
    }
}

package com.rednetty.voicerecorder.util;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Utility methods for UI creation and styling.
 */
public class UIHelper {
    // Common colors
    public static final Color PRIMARY_COLOR = new Color(66, 133, 244);
    public static final Color ACCENT_COLOR = new Color(219, 68, 55);
    public static final Color SUCCESS_COLOR = new Color(15, 157, 88);
    public static final Color BACKGROUND_COLOR = new Color(250, 250, 250);
    public static final Color TEXT_COLOR = new Color(33, 33, 33);

    /**
     * Creates a styled button with proper colors and hover effects.
     */
    public static JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(100, 40));

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });

        return button;
    }

    /**
     * Creates a card-like panel with a border and padding.
     */
    public static Border createCardBorder() {
        return new CompoundBorder(
                new EmptyBorder(10, 10, 10, 10),
                new CompoundBorder(
                        new LineBorder(new Color(220, 220, 220), 1, true),
                        new EmptyBorder(15, 15, 15, 15)
                )
        );
    }

    /**
     * Creates a title label with proper styling.
     */
    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setForeground(TEXT_COLOR);
        return label;
    }

    /**
     * Shows a styled error dialog.
     */
    public static void showErrorDialog(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(
                parent,
                message,
                title,
                JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * Shows a styled info dialog.
     */
    public static void showInfoDialog(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(
                parent,
                message,
                title,
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Set up the system look and feel.
     */
    public static void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Failed to set look and feel: " + e.getMessage());
        }
    }
}
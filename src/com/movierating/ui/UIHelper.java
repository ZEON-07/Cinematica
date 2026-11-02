package com.movierating.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class UIHelper {

    public static final Color COLOR_PRIMARY = new Color(26, 115, 232);
    public static final Color COLOR_SUCCESS = new Color(46, 125, 50);
    public static final Color COLOR_DANGER  = new Color(211, 47, 47);

    public static Color getBgColor() {
        return new Color(245, 247, 250);
    }

    public static Color getCardBgColor() {
        return Color.WHITE;
    }

    public static Color getHeaderBgColor() {
        return new Color(33, 43, 54);
    }

    public static Color getTextColor() {
        return new Color(33, 33, 33);
    }

    public static Color getMutedTextColor() {
        return new Color(100, 110, 120);
    }

    public static Color getBorderColor() {
        return new Color(207, 216, 220);
    }

    public static Color getInputBgColor() {
        return Color.WHITE;
    }

    public static Color getInputTextColor() {
        return new Color(33, 33, 33);
    }

    public static Color getTableBgColor() {
        return Color.WHITE;
    }

    public static Color getTableFgColor() {
        return new Color(33, 33, 33);
    }

    public static Color getTableHeaderBg() {
        return new Color(236, 240, 245);
    }

    public static Color getTableHeaderFg() {
        return new Color(33, 33, 33);
    }

    public static Color getTableGridColor() {
        return new Color(230, 233, 238);
    }

    public static Color getTableSelectionBg() {
        return new Color(218, 234, 254);
    }

    public static Color getTableSelectionFg() {
        return new Color(15, 23, 42);
    }

    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_STAR = new Font("Segoe UI Symbol", Font.PLAIN, 13);

    public static JButton createCustomButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (!isEnabled()) {
                    g2.setColor(new Color(180, 180, 180));
                } else if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        button.setFont(FONT_BOLD);
        button.setForeground(fgColor);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return button;
    }

    public static JButton createSecondaryButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color currentBg = Color.WHITE;
                Color hoverBg = new Color(240, 243, 246);
                Color pressedBg = new Color(225, 230, 235);

                if (getModel().isPressed()) {
                    g2.setColor(pressedBg);
                } else if (getModel().isRollover()) {
                    g2.setColor(hoverBg);
                } else {
                    g2.setColor(currentBg);
                }

                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.setColor(getBorderColor());
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        button.setFont(FONT_BOLD);
        button.setForeground(getTextColor());
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return button;
    }

    public static void linkEnterKeyToButton(JComponent inputComponent, JButton targetButton) {
        inputComponent.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enterAction");
        inputComponent.getActionMap().put("enterAction", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                targetButton.doClick();
            }
        });
    }
}

package com.i08.styles;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class LoginStyles {

    public static final Color COLOR_BG = new Color(240, 243, 248);
    public static final Color COLOR_CARD = Color.WHITE;
    public static final Color COLOR_PRIMARY = new Color(37, 99, 235);
    public static final Color COLOR_TEXT = new Color(33, 33, 33);
    public static final Color COLOR_SUBTEXT = new Color(120, 120, 120);
    public static final Color COLOR_BORDER = new Color(210, 210, 210);

    public static void applyCard(JPanel panel) {
        panel.setBackground(COLOR_CARD);
        panel.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER),
                new EmptyBorder(24, 24, 24, 24)
        ));
    }

    public static void applyTitle(JLabel label) {
        label.setFont(new Font("Segoe UI", Font.BOLD, 20));
        label.setForeground(COLOR_TEXT);
    }

    public static void applyLabel(JLabel label) {
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(COLOR_SUBTEXT);
    }

    public static void applyInput(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER),
                new EmptyBorder(10, 10, 10, 10)
        ));
    }

    public static void applyPrimaryButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(COLOR_PRIMARY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(12, 0, 12, 0));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
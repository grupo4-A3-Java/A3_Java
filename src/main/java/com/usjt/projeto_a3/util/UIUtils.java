package com.usjt.projeto_a3.util;

import com.usjt.projeto_a3.view.TelaPrincipal;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * Utilitários centralizados para construção de componentes UI.
 * Consolida código duplicado em TelaLogin, TelaCadastro, Painéis, etc.
 */
public class UIUtils {

    // ─── TextField com placeholder ────────────────────────────────────────────
    public static JTextField buildTextFieldWithPlaceholder(String placeholder) {
        JTextField f = new JTextField();
        estilizarCampo(f);
        f.setForeground(TelaPrincipal.MUTED);
        f.setText(placeholder);
        f.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (f.getText().equals(placeholder)) {
                    f.setText("");
                    f.setForeground(TelaPrincipal.TEXT);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (f.getText().isEmpty()) {
                    f.setForeground(TelaPrincipal.MUTED);
                    f.setText(placeholder);
                }
            }
        });
        return f;
    }

    // ─── TextField simples ────────────────────────────────────────────────────
    public static JTextField buildTextField() {
        JTextField f = new JTextField();
        estilizarCampo(f);
        return f;
    }

    // ─── PasswordField com placeholder ────────────────────────────────────────
    public static JPasswordField buildPasswordFieldWithPlaceholder(String placeholder) {
        JPasswordField f = new JPasswordField();
        estilizarCampo(f);
        f.setEchoChar((char) 0); // mostra placeholder como texto
        f.setForeground(TelaPrincipal.MUTED);
        f.setText(placeholder);
        f.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (new String(f.getPassword()).equals(placeholder)) {
                    f.setText("");
                    f.setForeground(TelaPrincipal.TEXT);
                    f.setEchoChar('●');
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (f.getPassword().length == 0) {
                    f.setEchoChar((char) 0);
                    f.setForeground(TelaPrincipal.MUTED);
                    f.setText(placeholder);
                }
            }
        });
        return f;
    }

    // ─── PasswordField simples ───────────────────────────────────────────────
    public static JPasswordField buildPasswordField() {
        JPasswordField f = new JPasswordField();
        f.setEchoChar('●');
        estilizarCampo(f);
        return f;
    }

    // ─── Estilo de campo reutilizável ────────────────────────────────────────
    public static void estilizarCampo(JComponent f) {
        f.setBackground(TelaPrincipal.BG);
        f.setForeground(TelaPrincipal.TEXT);
        f.setFont(TelaPrincipal.F_BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TelaPrincipal.BORDER, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        f.setPreferredSize(new Dimension(0, 42));
    }

    // ─── Bloco label + campo (consolidado) ────────────────────────────────────
    public static JPanel buildFieldBlock(String label, JComponent campo) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        JLabel lbl = new JLabel(label);
        lbl.setFont(TelaPrincipal.F_SUB);
        lbl.setForeground(TelaPrincipal.TEXT);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        p.add(lbl);
        p.add(Box.createVerticalStrut(6));
        p.add(campo);
        return p;
    }

    // ─── Header padrão para painéis ───────────────────────────────────────────
    public static JPanel buildHeaderPanel(String title, JButton rightButton) {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.add(TelaPrincipal.sectionTitle(title));

        header.add(titleBlock, BorderLayout.WEST);

        if (rightButton != null) {
            rightButton.setPreferredSize(new Dimension(150, 34));
            header.add(rightButton, BorderLayout.EAST);
        }

        return header;
    }

    // ─── Estilo de tabela reutilizável ───────────────────────────────────────
    public static void estilizarTabela(JTable tabela) {
        // Header
        tabela.getTableHeader().setBackground(new Color(40, 47, 56));
        tabela.getTableHeader().setForeground(new Color(200, 210, 220));
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabela.getTableHeader().setPreferredSize(new Dimension(0, 32));

        // Alternância de cores
        tabela.setDefaultRenderer(Object.class, (TableCellRenderer) new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (isSelected) {
                    c.setBackground(new Color(60, 70, 85));
                    c.setForeground(new Color(200, 210, 220));
                } else {
                    c.setBackground(row % 2 == 0 ? new Color(33, 38, 45) : new Color(45, 52, 62));
                    c.setForeground(new Color(180, 190, 200));
                }

                if (c instanceof JLabel) {
                    ((JLabel) c).setHorizontalAlignment(SwingConstants.LEFT);
                    ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                }

                return c;
            }
        });

        // Sem grid
        tabela.setShowGrid(false);
        tabela.setIntercellSpacing(new Dimension(0, 0));
        tabela.setRowHeight(30);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    }
}

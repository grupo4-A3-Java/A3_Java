package com.usjt.projeto_a3.view;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

/**
 * Painel Operações — histórico de compras e vendas em tabela.
 * Dados mockados; conecte ao seu service para dados reais.
 */
public class PainelOperacoes extends JPanel {

    private static final String[] COLUNAS = {
        "Data", "Ativo", "Tipo", "Quantidade", "Preço Unitário", "Total"
    };

    private static final Object[][] DADOS = {
        {"20/05/2026", "PETR4",  "Compra", "200",  "R$ 35,00",       "R$ 7.000,00" },
        {"18/05/2026", "HGLG11", "Compra",  "50",  "R$ 160,00",      "R$ 8.000,00" },
        {"15/05/2026", "VALE3",  "Compra", "100",  "R$ 65,00",       "R$ 6.500,00" },
        {"10/05/2026", "BTC",    "Compra", "0,01", "R$ 320.000,00",  "R$ 3.200,00" },
        {"05/05/2026", "PETR4",  "Venda",   "50",  "R$ 36,00",       "R$ 1.800,00" },
        {"01/04/2026", "VALE3",  "Compra",  "50",  "R$ 68,00",       "R$ 3.400,00" },
    };

    public PainelOperacoes() {
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));
        build();
    }

    private void build() {
        // Cabeçalho
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.add(TelaPrincipal.sectionTitle("Histórico de Operações"));
        JLabel sub = new JLabel("Registro completo de compras e vendas");
        sub.setFont(TelaPrincipal.F_SMALL);
        sub.setForeground(TelaPrincipal.MUTED);
        titleBlock.add(sub);
        header.add(titleBlock, BorderLayout.WEST);

        JButton btnNova = TelaPrincipal.btnPrimario("+ Nova Operação");
        btnNova.setPreferredSize(new Dimension(155, 34));
        header.add(btnNova, BorderLayout.EAST);

        // Tabela
        DefaultTableModel model = new DefaultTableModel(DADOS, COLUNAS) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabela = new JTable(model);
        estilizarTabela(tabela);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setOpaque(false);
        scroll.getViewport().setBackground(TelaPrincipal.CARD);
        scroll.setBorder(BorderFactory.createLineBorder(TelaPrincipal.BORDER, 1, true));
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(header, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    private void estilizarTabela(JTable t) {
        t.setBackground(TelaPrincipal.CARD);
        t.setForeground(TelaPrincipal.TEXT);
        t.setGridColor(TelaPrincipal.BORDER);
        t.setRowHeight(42);
        t.setFont(TelaPrincipal.F_BODY);
        t.setShowVerticalLines(false);
        t.setFillsViewportHeight(true);
        t.setSelectionBackground(TelaPrincipal.ACCENT);
        t.setSelectionForeground(Color.WHITE);
        t.setIntercellSpacing(new Dimension(0, 1));

        // Cabeçalho
        JTableHeader th = t.getTableHeader();
        th.setBackground(TelaPrincipal.SIDEBAR);
        th.setForeground(TelaPrincipal.MUTED);
        th.setFont(TelaPrincipal.F_SUB);
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, TelaPrincipal.BORDER));
        th.setReorderingAllowed(false);

        // Coluna "Tipo" com cor por Compra/Venda
        t.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(table, value, sel, foc, row, col);
                setHorizontalAlignment(CENTER);
                setBackground(sel ? TelaPrincipal.ACCENT : TelaPrincipal.CARD);
                if (!sel) {
                    setForeground("Compra".equals(value) ? TelaPrincipal.GREEN : TelaPrincipal.RED);
                }
                return this;
            }
        });

        // Demais colunas com background correto
        DefaultTableCellRenderer base = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(table, value, sel, foc, row, col);
                setBackground(sel ? TelaPrincipal.ACCENT : TelaPrincipal.CARD);
                if (!sel) setForeground(TelaPrincipal.TEXT);
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                return this;
            }
        };
        for (int i = 0; i < t.getColumnCount(); i++) {
            if (i != 2) t.getColumnModel().getColumn(i).setCellRenderer(base);
        }
    }
}

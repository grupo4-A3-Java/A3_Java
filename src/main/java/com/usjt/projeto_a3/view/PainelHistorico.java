package com.usjt.projeto_a3.view;

import com.usjt.projeto_a3.factory.AppFactory;
import com.usjt.projeto_a3.service.FinanceiroService;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class PainelHistorico extends JPanel {

    private final int usuarioId;
    private DefaultTableModel modeloTabela;
    private static final NumberFormat BRL = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private static final DateTimeFormatter DATA_FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public PainelHistorico(int usuarioId) {
        this.usuarioId = usuarioId;
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));
        buildEstrutura();
        carregarHistorico(); // Busca os dados em segundo plano ao abrir
    }

    private void buildEstrutura() {
        // ── Cabeçalho ──
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.add(TelaPrincipal.sectionTitle("Extrato de Operações"));
        JLabel sub = new JLabel("Registo imutável de todas as compras e vendas realizadas.");
        sub.setFont(TelaPrincipal.F_SMALL);
        sub.setForeground(TelaPrincipal.MUTED);
        titleBlock.add(sub);
        header.add(titleBlock, BorderLayout.WEST);

        JButton btnAtualizar = TelaPrincipal.btnPrimario("↺  Atualizar");
        btnAtualizar.setPreferredSize(new Dimension(150, 34));
        btnAtualizar.addActionListener(e -> carregarHistorico());
        header.add(btnAtualizar, BorderLayout.EAST);

        // ── Tabela ──
        String[] colunas = {"Data e Hora", "Ticker", "Ativo", "Operação", "Quantidade", "Preço Unitário", "Total da Ordem"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable tabela = new JTable(modeloTabela);
        estilizarTabela(tabela);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setOpaque(false);
        scroll.getViewport().setBackground(TelaPrincipal.CARD);
        scroll.setBorder(BorderFactory.createLineBorder(TelaPrincipal.BORDER, 1, true));

        add(header, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    private void estilizarTabela(JTable t) {
        t.setBackground(TelaPrincipal.CARD);
        t.setForeground(TelaPrincipal.TEXT);
        t.setGridColor(TelaPrincipal.BORDER);
        t.setRowHeight(40);
        t.setFont(TelaPrincipal.F_BODY);
        t.setShowVerticalLines(false);
        t.setFillsViewportHeight(true);
        t.setSelectionBackground(TelaPrincipal.ACCENT);
        t.setSelectionForeground(Color.WHITE);
        t.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader th = t.getTableHeader();
        th.setBackground(TelaPrincipal.SIDEBAR);
        th.setForeground(TelaPrincipal.MUTED);
        th.setFont(TelaPrincipal.F_SUB);
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, TelaPrincipal.BORDER));
        th.setReorderingAllowed(false);

        DefaultTableCellRenderer base = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(table, value, sel, foc, row, col);
                setBackground(sel ? TelaPrincipal.ACCENT : TelaPrincipal.CARD);
                if (!sel) setForeground(TelaPrincipal.TEXT);
                setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
                return this;
            }
        };

        for (int i = 0; i < t.getColumnCount(); i++) {
            t.getColumnModel().getColumn(i).setCellRenderer(base);
        }

        // ── Colorir a coluna "Operação" (Coluna 3) ──
        t.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(table, value, sel, foc, row, col);
                setBackground(sel ? TelaPrincipal.ACCENT : TelaPrincipal.CARD);
                setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
                if (!sel && value != null) {
                    String op = value.toString();
                    setForeground(op.equals("COMPRA") ? TelaPrincipal.GREEN : TelaPrincipal.RED);
                }
                return this;
            }
        });
    }

    private void carregarHistorico() {
        modeloTabela.setRowCount(0); 

        new SwingWorker<List<Object[]>, Void>() {
            @Override
            protected List<Object[]> doInBackground() {
                FinanceiroService financeiroService = AppFactory.getInstance().getFinanceiroService();
                return financeiroService.buscarHistoricoOperacoes(usuarioId);
            }

            @Override
            protected void done() {
                try {
                    List<Object[]> operacoes = get();
                    for (Object[] op : operacoes) {
                        LocalDateTime data = (LocalDateTime) op[0];
                        String ticker      = (String) op[1];
                        String nome        = (String) op[2];
                        String tipoOp      = (String) op[3];
                        double qtd         = (Double) op[4];
                        double preco       = (Double) op[5];
                        double total       = qtd * preco;

                        String dataFormatada = data.format(DATA_FORMATO);
                        // Cripto tem casas decimais, ações não
                        java.text.DecimalFormat dfQtd = new java.text.DecimalFormat("0.########");
                        String qtdFormatada = dfQtd.format(qtd);

                        modeloTabela.addRow(new Object[]{
                            dataFormatada, ticker, nome, tipoOp, qtdFormatada, BRL.format(preco), BRL.format(total)
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(PainelHistorico.this, "Erro ao carregar o histórico.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
}

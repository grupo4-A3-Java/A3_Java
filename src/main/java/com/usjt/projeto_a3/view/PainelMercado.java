package com.usjt.projeto_a3.view;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.*;
import java.text.NumberFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import com.usjt.projeto_a3.factory.AppFactory;
import com.usjt.projeto_a3.service.AtivoService;
import com.usjt.projeto_a3.service.FinanceiroService;
import com.usjt.projeto_a3.model.Ativo;

public class PainelMercado extends JPanel {

    // ─── Ativos a buscar ──────────────────────────────────────────────────────
    private static final String[] B3_SYMBOLS = {
        "PETR4", "VALE3", "ITUB4", "BBDC4", "ABEV3", "MGLU3", "HGLG11", "IVVB11"
    };
    private static final String[][] CRIPTO = {
        {"bitcoin",  "BTC", "Bitcoin"},
        {"ethereum", "ETH", "Ethereum"},
        {"solana",   "SOL", "Solana"},
        {"cardano",  "ADA", "Cardano"},
    };

    // ─── Formatador BRL ───────────────────────────────────────────────────────
    private static final NumberFormat BRL =
        NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    // ─── Componentes ──────────────────────────────────────────────────────────
    private DefaultTableModel modelB3;
    private DefaultTableModel modelCripto;
    private JLabel            statusLabel;
    private JButton           btnAtualizar;

    public PainelMercado() {
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));
        build();
        carregarDados(); // busca automática ao abrir
    }

    private void build() {
        // ── Cabeçalho ──────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.add(TelaPrincipal.sectionTitle("Mercado ao Vivo"));
        JLabel sub = new JLabel("Cotações via Brapi.dev (B3) e Binance (Cripto)");
        sub.setFont(TelaPrincipal.F_SMALL);
        sub.setForeground(TelaPrincipal.MUTED);
        titleBlock.add(sub);
        header.add(titleBlock, BorderLayout.WEST);

        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightHeader.setOpaque(false);
        statusLabel = new JLabel("Aguardando...");
        statusLabel.setFont(TelaPrincipal.F_SMALL);
        statusLabel.setForeground(TelaPrincipal.MUTED);
        btnAtualizar = TelaPrincipal.btnPrimario("↺  Atualizar");
        btnAtualizar.setPreferredSize(new Dimension(115, 34));
        btnAtualizar.addActionListener(e -> carregarDados());
        rightHeader.add(statusLabel);
        rightHeader.add(btnAtualizar);

        JPanel rightWrap = new JPanel(new GridBagLayout());
        rightWrap.setOpaque(false);
        rightWrap.add(rightHeader);
        header.add(rightWrap, BorderLayout.EAST);

        // ── Tabela B3 ──────────────────────────────────────────────────────
        JLabel lblB3 = TelaPrincipal.sectionSubtitle("📈  Ações — B3");
        lblB3.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        String[] colsB3 = {"Código", "Nome", "Preço Atual", "Variação 24h", "Tipo", "Ação"};
        modelB3 = new DefaultTableModel(colsB3, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tableB3 = new JTable(modelB3);
        estilizarTabela(tableB3, 3); 

        // OUVINTE DE CLIQUE DA B3
        tableB3.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tableB3.rowAtPoint(e.getPoint());
                int col = tableB3.columnAtPoint(e.getPoint());
                if (col == 5 && row >= 0) { 
                    String ticker = tableB3.getValueAt(row, 0).toString();
                    String precoStr = tableB3.getValueAt(row, 2).toString();
                    efetuarCompra(ticker, parsePrecoDaTabela(precoStr));
                }
            }
        });

        JScrollPane scrollB3 = wrapScroll(tableB3);
        scrollB3.setPreferredSize(new Dimension(0, 230));

        // ── Tabela Cripto ──────────────────────────────────────────────────
        JLabel lblCripto = TelaPrincipal.sectionSubtitle("₿  Criptomoedas");
        lblCripto.setBorder(BorderFactory.createEmptyBorder(22, 0, 10, 0));

        String[] colsCripto = {"Símbolo", "Nome", "Preço (BRL)", "Variação 24h", "Tipo", "Ação"};
        modelCripto = new DefaultTableModel(colsCripto, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tableCripto = new JTable(modelCripto);
        estilizarTabela(tableCripto, 3);

        // OUVINTE DE CLIQUE DA CRIPTO
        tableCripto.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tableCripto.rowAtPoint(e.getPoint());
                int col = tableCripto.columnAtPoint(e.getPoint());
                if (col == 5 && row >= 0) {
                    String ticker = tableCripto.getValueAt(row, 0).toString();
                    String precoStr = tableCripto.getValueAt(row, 2).toString();
                    efetuarCompra(ticker, parsePrecoDaTabela(precoStr));
                }
            }
        });

        JScrollPane scrollCripto = wrapScroll(tableCripto);
        scrollCripto.setPreferredSize(new Dimension(0, 180));

        // ── Layout scrollável ──────────────────────────────────────────────
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.add(lblB3);
        body.add(scrollB3);
        body.add(lblCripto);
        body.add(scrollCripto);

        JScrollPane mainScroll = new JScrollPane(body);
        mainScroll.setOpaque(false);
        mainScroll.getViewport().setOpaque(false);
        mainScroll.setBorder(BorderFactory.createEmptyBorder());
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);

        add(header,      BorderLayout.NORTH);
        add(mainScroll, BorderLayout.CENTER);
    }

    private JScrollPane wrapScroll(JTable t) {
        JScrollPane s = new JScrollPane(t);
        s.setOpaque(false);
        s.getViewport().setBackground(TelaPrincipal.CARD);
        s.setBorder(BorderFactory.createLineBorder(TelaPrincipal.BORDER, 1, true));
        s.setAlignmentX(Component.LEFT_ALIGNMENT);
        return s;
    }

    private void estilizarTabela(JTable t, int colVariacao) {
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

        // Coluna de variação — verde/vermelho
        t.getColumnModel().getColumn(colVariacao).setCellRenderer(
            new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                        boolean sel, boolean foc, int row, int col) {
                    super.getTableCellRendererComponent(table, value, sel, foc, row, col);
                    setBackground(sel ? TelaPrincipal.ACCENT : TelaPrincipal.CARD);
                    setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
                    if (!sel && value != null) {
                        String s = value.toString().trim();
                        setForeground(s.startsWith("+") ? TelaPrincipal.GREEN : TelaPrincipal.RED);
                    }
                    return this;
                }
            }
        );

        // Estilização da Coluna "Ação" (Azul clicável)
        t.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(table, value, sel, foc, row, col);
                setBackground(sel ? TelaPrincipal.ACCENT : TelaPrincipal.CARD);
                setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
                if (!sel) setForeground(TelaPrincipal.ACCENT); 
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                return this;
            }
        });
    }

    private void carregarDados() {
        btnAtualizar.setEnabled(false);
        statusLabel.setText("Buscando dados...");
        modelB3.setRowCount(0);
        modelCripto.setRowCount(0);

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                buscarB3();
                buscarCripto();
                return null;
            }

            @Override
            protected void done() {
                btnAtualizar.setEnabled(true);
                String hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                statusLabel.setText("Atualizado às " + hora);
            }
        }.execute();
    }

    private void buscarB3() {
        try {
            String symbols = String.join(",", B3_SYMBOLS);
            URL url = new URL("https://brapi.dev/api/quote/" + symbols + "?token=demo");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            if (conn.getResponseCode() == 200) {
                String json = lerStream(conn.getInputStream());
                parseRespostaB3(json);
            } else {
                fallbackB3();
            }
            conn.disconnect();
        } catch (Exception ex) {
            fallbackB3();
        }
    }

    // ─── NOVO MOTOR BINANCE PARA CRIPTOMOEDAS ───
    private void buscarCripto() {
        List<Object[]> linhasParaAdicionar = new ArrayList<>();
        
        try {
            for (String[] cripto : CRIPTO) {
                String symbol = cripto[1]; // Ex: BTC
                String name = cripto[2];   // Ex: Bitcoin
                String binanceSymbol = symbol + "BRL"; // Ex: BTCBRL
                
                URL url = new URL("https://api.binance.com/api/v3/ticker/24hr?symbol=" + binanceSymbol);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                conn.setConnectTimeout(5000);
                
                if (conn.getResponseCode() == 200) {
                    String json = lerStream(conn.getInputStream());
                    
                    // A Binance envia o preço atual ("lastPrice") e a variação ("priceChangePercent")
                    double price = extrairDouble(json, "lastPrice");
                    double change = extrairDouble(json, "priceChangePercent");

                    String sPreco = formatarBRL(price);
                    String sChange = String.format("%+.2f%%", change);
                    
                    linhasParaAdicionar.add(new Object[]{symbol, name, sPreco, sChange, "Cripto", "🛒 Comprar"});
                }
                conn.disconnect();
            }
            
            // Se encontrou as criptos com sucesso, atualiza a tabela
            if (!linhasParaAdicionar.isEmpty()) {
                SwingUtilities.invokeLater(() -> {
                    for (Object[] row : linhasParaAdicionar) {
                        modelCripto.addRow(row);
                    }
                });
            } else {
                fallbackCripto();
            }
            
        } catch (Exception e) {
            fallbackCripto();
        }
    }

    private String lerStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            String linha;
            while ((linha = br.readLine()) != null) sb.append(linha);
        }
        return sb.toString();
    }

    private void parseRespostaB3(String json) {
        try {
            int arrStart = json.indexOf('[');
            int arrEnd   = json.lastIndexOf(']');
            if (arrStart < 0 || arrEnd < 0) { fallbackB3(); return; }

            List<String> objetos = extrairObjetos(json.substring(arrStart + 1, arrEnd));
            if (objetos.isEmpty()) { fallbackB3(); return; }

            for (String obj : objetos) {
                String symbol = extrairString(obj, "symbol");
                String name   = extrairString(obj, "shortName");
                double price  = extrairDouble(obj, "regularMarketPrice");
                double change = extrairDouble(obj, "regularMarketChangePercent");

                if (symbol == null) continue;
                if (name == null) name = symbol;
                if (name.length() > 22) name = name.substring(0, 22) + "…";

                String sPreco  = BRL.format(price);
                String sChange = String.format("%+.2f%%", change);
                String sTipo   = symbol.matches(".*11$") ? "FII"
                               : symbol.matches(".*B11$") ? "ETF"
                               : "Ação";

                final String fS = symbol, fN = name, fP = sPreco, fC = sChange, fT = sTipo;
                SwingUtilities.invokeLater(() ->
                    modelB3.addRow(new Object[]{fS, fN, fP, fC, fT, "🛒 Comprar"}));
            }
        } catch (Exception e) {
            fallbackB3();
        }
    }

    private static String extrairString(String json, String chave) {
        String busca = "\"" + chave + "\":\"";
        int idx = json.indexOf(busca);
        if (idx < 0) return null;
        idx += busca.length();
        int fim = json.indexOf('"', idx);
        return (fim > idx) ? json.substring(idx, fim) : null;
    }

    // ── O SEGREDO ESTAVA AQUI! ──
    // A API da Binance manda os números entre aspas (ex: "lastPrice": "350000.00"). 
    // O Java agora sabe ignorar essas aspas antes de ler o número!
    private static double extrairDouble(String json, String chave) {
        String busca = "\"" + chave + "\":";
        int idx = json.indexOf(busca);
        if (idx < 0) return 0.0;
        idx += busca.length();
        
        // Pula os espaços em branco ou aspas que vêm imediatamente a seguir
        while (idx < json.length() && (json.charAt(idx) == ' ' || json.charAt(idx) == '"')) idx++;
        
        int fim = idx;
        // Lê os números e pontos
        while (fim < json.length() && "0123456789.-".indexOf(json.charAt(fim)) >= 0) fim++;
        
        try { return Double.parseDouble(json.substring(idx, fim)); }
        catch (NumberFormatException e) { return 0.0; }
    }

    private static List<String> extrairObjetos(String arrayContent) {
        List<String> lista = new ArrayList<>();
        int profundidade = 0, inicio = -1;
        for (int i = 0; i < arrayContent.length(); i++) {
            char c = arrayContent.charAt(i);
            if (c == '{') {
                if (profundidade == 0) inicio = i;
                profundidade++;
            } else if (c == '}') {
                profundidade--;
                if (profundidade == 0 && inicio >= 0) {
                    lista.add(arrayContent.substring(inicio, i + 1));
                    inicio = -1;
                }
            }
        }
        return lista;
    }

    private static String formatarBRL(double valor) {
        if (valor >= 1_000_000) {
            return String.format("R$ %,.2f M", valor / 1_000_000);
        }
        return BRL.format(valor);
    }

    // ─── UTILS DE CONVERSÃO PARA COMPRA ─────────────────────────────────────────
    private double parsePrecoDaTabela(String precoStr) {
        try {
            boolean isMilhao = precoStr.contains("M");
            
            // Magia aqui: Remove TUDO o que não for número (0-9) ou vírgula
            String limpo = precoStr.replaceAll("[^0-9,]", ""); 
            limpo = limpo.replace(",", "."); // Transforma a vírgula do Brasil no ponto do Java
            
            double valor = Double.parseDouble(limpo);
            if (isMilhao) valor *= 1_000_000;
            return valor;
            
        } catch (Exception e) {
            return 0.0;
        }
    }

    private void fallbackB3() {
        SwingUtilities.invokeLater(() -> {
            if (modelB3.getRowCount() > 0) return; 
            Object[][] dados = {
                {"PETR4",  "Petrobras PN",        "R$ 38,50",  "+1,23%", "Ação", "🛒 Comprar"},
                {"VALE3",  "Vale ON",             "R$ 62,30",  "-0,48%", "Ação", "🛒 Comprar"},
                {"ITUB4",  "Itaú Unibanco PN",    "R$ 35,20",  "+0,57%", "Ação", "🛒 Comprar"},
                {"BBDC4",  "Bradesco PN",         "R$ 14,80",  "-1,06%", "Ação", "🛒 Comprar"},
                {"ABEV3",  "Ambev ON",            "R$ 11,90",  "+0,17%", "Ação", "🛒 Comprar"},
                {"MGLU3",  "Magazine Luiza ON",   "R$  4,32",  "-2,50%", "Ação", "🛒 Comprar"},
                {"HGLG11", "CSHG Logística",      "R$ 165,20", "+3,25%", "FII",  "🛒 Comprar"},
                {"IVVB11", "iShares S&P 500",     "R$ 291,00", "+0,83%", "ETF",  "🛒 Comprar"},
            };
            for (Object[] row : dados) modelB3.addRow(row);
            statusLabel.setText("Dados locais (API indisponível)");
        });
    }

    private void fallbackCripto() {
        SwingUtilities.invokeLater(() -> {
            if (modelCripto.getRowCount() > 0) return;
            Object[][] dados = {
                {"BTC", "Bitcoin",  "R$ 350.000,00", "+2,50%",  "Cripto", "🛒 Comprar"},
                {"ETH", "Ethereum", "R$  18.500,00",  "+1,30%",  "Cripto", "🛒 Comprar"},
                {"SOL", "Solana",   "R$     820,00",  "+4,12%",  "Cripto", "🛒 Comprar"},
                {"ADA", "Cardano",  "R$       2,45",  "-0,88%",  "Cripto", "🛒 Comprar"},
            };
            for (Object[] row : dados) modelCripto.addRow(row);
        });
    }

    // ─── LÓGICA DO BANCO DE DADOS ─────────────────────────────────────────────
    private void efetuarCompra(String ticker, double precoAtual) {
        try {
            String input = JOptionPane.showInputDialog(this, 
                "Quantas unidades de " + ticker + " deseja comprar?\nPreço unitário: R$ " + precoAtual, 
                "Comprar Ativo", JOptionPane.QUESTION_MESSAGE);
                
            if (input == null || input.trim().isEmpty()) return; 
            
            double quantidade = Double.parseDouble(input.replace(",", "."));
            
            if (quantidade <= 0) {
                JOptionPane.showMessageDialog(this, "A quantidade deve ser maior que zero.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            AtivoService ativoService = AppFactory.getInstance().getAtivoService();
            Ativo ativo = ativoService.buscarPorTicker(ticker);
            
            if (ativo == null) {
                JOptionPane.showMessageDialog(this, "Erro: Este ativo não está registado no catálogo do sistema.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            TelaPrincipal janelaPrincipal = (TelaPrincipal) SwingUtilities.getWindowAncestor(this);
            int usuarioId = janelaPrincipal.getUsuarioIdLogado();

            FinanceiroService financeiroService = AppFactory.getInstance().getFinanceiroService();
            financeiroService.comprarAtivo(usuarioId, ativo.getId(), quantidade, precoAtual);

            JOptionPane.showMessageDialog(this, 
                "Compra de " + quantidade + "x " + ticker + " realizada com sucesso!\nO seu saldo foi atualizado.", 
                "Operação Concluída", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, introduza um valor numérico válido.", "Erro de Digitação", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao processar compra: " + ex.getMessage(), "Erro no Sistema", JOptionPane.ERROR_MESSAGE);
        }
    }
}
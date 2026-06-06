package com.usjt.projeto_a3.view;

import com.usjt.projeto_a3.factory.AppFactory;
import com.usjt.projeto_a3.service.FinanceiroService;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

public class PainelDashboard extends JPanel {

    private final int usuarioId;
    private static final NumberFormat BRL = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    private JPanel panelEstatisticas;
    private JPanel panelComposicao;
    private JLabel lblStatus;

    public PainelDashboard(int usuarioId) {
        this.usuarioId = usuarioId;
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));
        buildSkeleton();
        carregarDashboard();
    }

    private void buildSkeleton() {
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        // ── Título ──
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        titleRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));
        
        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setOpaque(false);
        titleBlock.add(TelaPrincipal.sectionTitle("Resumo da Conta"));
        lblStatus = new JLabel("A carregar dados do mercado...");
        lblStatus.setFont(TelaPrincipal.F_SMALL);
        lblStatus.setForeground(TelaPrincipal.MUTED);
        titleBlock.add(lblStatus);
        
        titleRow.add(titleBlock, BorderLayout.WEST);
        
        JButton btnAtualizar = TelaPrincipal.btnPrimario("↺ Atualizar");
        btnAtualizar.addActionListener(e -> carregarDashboard());
        titleRow.add(btnAtualizar, BorderLayout.EAST);
        
        content.add(titleRow);

        // ── Espaços para os painéis dinâmicos ──
        panelEstatisticas = new JPanel(new GridLayout(1, 4, 16, 0));
        panelEstatisticas.setOpaque(false);
        panelEstatisticas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        content.add(panelEstatisticas);
        
        content.add(Box.createVerticalStrut(28));

        panelComposicao = new JPanel(new BorderLayout());
        panelComposicao.setOpaque(false);
        content.add(panelComposicao);

        add(content, BorderLayout.NORTH);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // MOTOR DE CÁLCULO DO DASHBOARD
    // ═══════════════════════════════════════════════════════════════════════════
    private void carregarDashboard() {
        lblStatus.setText("A calcular património...");
        panelEstatisticas.removeAll();
        panelComposicao.removeAll();
        revalidate();
        repaint();

        new SwingWorker<Map<String, Object>, Void>() {
            @Override
            protected Map<String, Object> doInBackground() {
                Map<String, Object> resultado = new HashMap<>();
                
                FinanceiroService financeiroService = AppFactory.getInstance().getFinanceiroService();
                List<Object[]> ativosBD = financeiroService.buscarCarteiraDoUsuario(usuarioId);
                
                double totalInvestido = 0.0;
                double patrimonioTotal = 0.0;
                int qtdAtivos = ativosBD.size();
                
                if (qtdAtivos == 0) {
                    resultado.put("vazio", true);
                    return resultado;
                }

                // Prepara a busca nas APIs
                Set<String> tickersB3 = new HashSet<>();
                Set<String> idsCripto = new HashSet<>();
                for (Object[] a : ativosBD) {
                    String ticker = (String) a[0];
                    if ("Cripto".equals(a[2])) idsCripto.add(mapearParaCoinGecko(ticker));
                    else tickersB3.add(ticker);
                }

                Map<String, Double> precosAoVivo = new HashMap<>();
                if (!tickersB3.isEmpty()) precosAoVivo.putAll(buscarPrecosB3(tickersB3));
                if (!idsCripto.isEmpty()) precosAoVivo.putAll(buscarPrecosCripto(idsCripto));

                List<Object[]> listaParaComposicao = new ArrayList<>();

                // Calcula os totais
                for (Object[] bd : ativosBD) {
                    String ticker = (String) bd[0];
                    String tipo = (String) bd[2];
                    double qtd = (Double) bd[3];
                    double precoMedio = (Double) bd[4];
                    double precoAtual = precosAoVivo.getOrDefault(ticker, precoMedio);
                    
                    double valorInvestidoAtivo = qtd * precoMedio;
                    double valorAtualAtivo = qtd * precoAtual;
                    
                    totalInvestido += valorInvestidoAtivo;
                    patrimonioTotal += valorAtualAtivo;
                    
                    listaParaComposicao.add(new Object[]{ticker, tipo, valorAtualAtivo});
                }
                
                // Ordena a lista do maior património para o menor
                listaParaComposicao.sort((a, b) -> Double.compare((Double) b[2], (Double) a[2]));

                resultado.put("vazio", false);
                resultado.put("totalInvestido", totalInvestido);
                resultado.put("patrimonioTotal", patrimonioTotal);
                resultado.put("qtdAtivos", qtdAtivos);
                resultado.put("lista", listaParaComposicao);
                
                return resultado;
            }

            @Override
            protected void done() {
                try {
                    Map<String, Object> res = get();
                    
                    if ((Boolean) res.get("vazio")) {
                        panelEstatisticas.add(statCard("Patrimônio Total", "R$ 0,00", null, false));
                        panelEstatisticas.add(statCard("Total Investido", "R$ 0,00", null, false));
                        panelEstatisticas.add(statCard("Lucro / Prejuízo", "R$ 0,00", null, false));
                        panelEstatisticas.add(statCard("Quantidade de Ativos", "0", null, false));
                        
                        JLabel vazio = new JLabel("Você ainda não realizou nenhum investimento.");
                        vazio.setForeground(TelaPrincipal.MUTED);
                        panelComposicao.add(vazio);
                    } else {
                        double inv = (Double) res.get("totalInvestido");
                        double pat = (Double) res.get("patrimonioTotal");
                        int qtd = (Integer) res.get("qtdAtivos");
                        
                        double lucro = pat - inv;
                        double varPct = (inv > 0) ? (lucro / inv) * 100 : 0;
                        boolean positivo = lucro >= 0;
                        
                        String sinalStr = positivo ? "+" : "";
                        String varStr = sinalStr + String.format("%.2f%%", varPct) + (positivo ? " ↑" : " ↓");

                        // ── 1. Desenha os Cards Superiores ──
                        panelEstatisticas.add(statCard("Patrimônio Total", BRL.format(pat), null, false));
                        panelEstatisticas.add(statCard("Total Investido", BRL.format(inv), null, false));
                        panelEstatisticas.add(statCard("Lucro / Prejuízo", BRL.format(lucro), varStr, positivo));
                        panelEstatisticas.add(statCard("Quantidade de Ativos", String.valueOf(qtd), null, false));

                        // ── 2. Desenha a Composição da Carteira ──
                        JLabel title = TelaPrincipal.sectionSubtitle("Composição da Carteira (Top Ativos)");
                        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
                        panelComposicao.add(title, BorderLayout.NORTH);

                        TelaPrincipal.CardPanel cardComp = new TelaPrincipal.CardPanel(12);
                        cardComp.setLayout(new BoxLayout(cardComp, BoxLayout.Y_AXIS));
                        cardComp.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));

                        List<Object[]> lista = (List<Object[]>) res.get("lista");
                        for (int i = 0; i < lista.size(); i++) {
                            Object[] item = lista.get(i);
                            String ticker = (String) item[0];
                            String tipo = (String) item[1];
                            double valor = (Double) item[2];
                            
                            double pctCarteira = (pat > 0) ? (valor / pat) * 100 : 0;
                            String sigla = ticker.substring(0, Math.min(2, ticker.length())).toUpperCase();
                            
                            cardComp.add(portfolioRow(sigla, ticker, tipo, BRL.format(valor), String.format("%.1f%% da carteira", pctCarteira)));
                            
                            if (i < lista.size() - 1) cardComp.add(divider()); // Adiciona o risco divisório se não for o último
                        }
                        
                        panelComposicao.add(cardComp, BorderLayout.CENTER);
                    }
                    
                    lblStatus.setText("Atualizado.");
                    revalidate();
                    repaint();
                    
                } catch (Exception e) {
                    lblStatus.setText("Erro ao carregar dados.");
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    // ─── Cards de Estatísticas UI ────────────────────────────────────────────────
    private JPanel statCard(String titulo, String valor, String extra, boolean positivo) {
        TelaPrincipal.CardPanel card = new TelaPrincipal.CardPanel(12);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(TelaPrincipal.F_SMALL);
        lblTitulo.setForeground(TelaPrincipal.MUTED);

        JPanel valorRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        valorRow.setOpaque(false);

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblValor.setForeground(TelaPrincipal.TEXT);
        valorRow.add(lblValor);

        if (extra != null) {
            JLabel badge = new JLabel("   " + extra);
            badge.setFont(TelaPrincipal.F_SMALL);
            badge.setForeground(positivo ? TelaPrincipal.GREEN : TelaPrincipal.RED);
            valorRow.add(badge);
        }

        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(valorRow,  BorderLayout.CENTER);
        return card;
    }

    // ─── Composição da Carteira UI ───────────────────────────────────────────────
    private JPanel portfolioRow(String sigla, String ticker, String tipo, String valor, String pct) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JPanel avatar = buildAvatar(sigla);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        JLabel lblTicker = new JLabel(ticker);
        lblTicker.setFont(TelaPrincipal.F_SUB);
        lblTicker.setForeground(TelaPrincipal.TEXT);
        JLabel lblTipo = new JLabel(tipo);
        lblTipo.setFont(TelaPrincipal.F_SMALL);
        lblTipo.setForeground(TelaPrincipal.MUTED);
        info.add(lblTicker);
        info.add(lblTipo);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);
        left.add(avatar);
        left.add(info);

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(TelaPrincipal.F_SUB);
        lblValor.setForeground(TelaPrincipal.TEXT);
        lblValor.setAlignmentX(Component.RIGHT_ALIGNMENT);
        JLabel lblPct = new JLabel(pct);
        lblPct.setFont(TelaPrincipal.F_SMALL);
        lblPct.setForeground(TelaPrincipal.MUTED);
        lblPct.setAlignmentX(Component.RIGHT_ALIGNMENT);
        right.add(lblValor);
        right.add(lblPct);

        JPanel rightWrap = new JPanel(new GridBagLayout());
        rightWrap.setOpaque(false);
        rightWrap.add(right);

        row.add(left,      BorderLayout.WEST);
        row.add(rightWrap, BorderLayout.EAST);
        return row;
    }

    private JPanel buildAvatar(String sigla) {
        JPanel av = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(TelaPrincipal.ACCENT);
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth()  - fm.stringWidth(sigla)) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(sigla, x, y);
                g2.dispose();
            }
        };
        av.setOpaque(false);
        av.setPreferredSize(new Dimension(40, 40));
        return av;
    }

    private JSeparator divider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(TelaPrincipal.BORDER);
        sep.setBackground(TelaPrincipal.BORDER);
        return sep;
    }
    
    // ═══════════════════════════════════════════════════════════════════════════
    // APIS DE COTAÇÃO
    // ═══════════════════════════════════════════════════════════════════════════
    private Map<String, Double> buscarPrecosB3(Set<String> tickers) {
        Map<String, Double> precos = new HashMap<>();
        try {
            String symbols = String.join(",", tickers);
            URL url = new URL("https://brapi.dev/api/quote/" + symbols + "?token=demo");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(8000);
            if (conn.getResponseCode() == 200) {
                String json = lerStream(conn.getInputStream());
                List<String> objetos = extrairObjetos(json);
                for (String obj : objetos) {
                    String symbol = extrairString(obj, "symbol");
                    double price = extrairDouble(obj, "regularMarketPrice");
                    if (symbol != null) precos.put(symbol, price);
                }
            }
            conn.disconnect();
        } catch (Exception e) {}
        return precos;
    }

    private Map<String, Double> buscarPrecosCripto(Set<String> idsCripto) {
        Map<String, Double> precos = new HashMap<>();
        try {
            String ids = String.join(",", idsCripto);
            URL url = new URL("https://api.coingecko.com/api/v3/simple/price?ids=" + ids + "&vs_currencies=brl");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(8000);
            if (conn.getResponseCode() == 200) {
                String json = lerStream(conn.getInputStream());
                for (String id : idsCripto) {
                    String obj = extrairStringBloco(json, id);
                    if (obj != null) {
                        double price = extrairDouble(obj, "brl");
                        precos.put(mapearParaTicker(id), price);
                    }
                }
            }
            conn.disconnect();
        } catch (Exception e) {}
        return precos;
    }

    private String lerStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            String linha;
            while ((linha = br.readLine()) != null) sb.append(linha);
        }
        return sb.toString();
    }

    private String mapearParaCoinGecko(String ticker) {
        switch (ticker.toUpperCase()) {
            case "BTC": return "bitcoin";
            case "ETH": return "ethereum";
            case "SOL": return "solana";
            case "ADA": return "cardano";
            default: return ticker.toLowerCase();
        }
    }
    
    private String mapearParaTicker(String idCoinGecko) {
        switch (idCoinGecko.toLowerCase()) {
            case "bitcoin": return "BTC";
            case "ethereum": return "ETH";
            case "solana": return "SOL";
            case "cardano": return "ADA";
            default: return idCoinGecko.toUpperCase();
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

    private static double extrairDouble(String json, String chave) {
        String busca = "\"" + chave + "\":";
        int idx = json.indexOf(busca);
        if (idx < 0) return 0.0;
        idx += busca.length();
        int fim = idx;
        while (fim < json.length() && "0123456789.-".indexOf(json.charAt(fim)) >= 0) fim++;
        try { return Double.parseDouble(json.substring(idx, fim)); }
        catch (NumberFormatException e) { return 0.0; }
    }

    private static List<String> extrairObjetos(String json) {
        List<String> lista = new ArrayList<>();
        int profundidade = 0, inicio = -1;
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') {
                if (profundidade == 0) inicio = i;
                profundidade++;
            } else if (c == '}') {
                profundidade--;
                if (profundidade == 0 && inicio >= 0) {
                    lista.add(json.substring(inicio, i + 1));
                    inicio = -1;
                }
            }
        }
        return lista;
    }

    private static String extrairStringBloco(String json, String chaveObj) {
        int idx = json.indexOf("\"" + chaveObj + "\"");
        if (idx < 0) return null;
        int objStart = json.indexOf('{', idx);
        int objEnd = json.indexOf('}', objStart);
        if (objStart > 0 && objEnd > 0) return json.substring(objStart, objEnd + 1);
        return null;
    }
}
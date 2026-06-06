package com.usjt.projeto_a3.view;

import com.usjt.projeto_a3.dao.AtivoDAO;
import com.usjt.projeto_a3.dao.FinanceiroDAO;
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
import com.usjt.projeto_a3.model.Ativo;

public class PainelCarteira extends JPanel {

    private final int usuarioId;
    private static final NumberFormat BRL = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    
    // Paineis dinâmicos
    private JPanel gridCartoes;
    private JLabel lblStatus;

    public PainelCarteira(int usuarioId) {
        this.usuarioId = usuarioId;
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));
        buildEstruturaBase();
        carregarDadosEAtualizar(); // Inicia a magia em segundo plano
    }

    private void buildEstruturaBase() {
        // ── Cabeçalho ──
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.add(TelaPrincipal.sectionTitle("Minha Carteira"));
        
        lblStatus = new JLabel("A carregar os seus investimentos...");
        lblStatus.setFont(TelaPrincipal.F_SMALL);
        lblStatus.setForeground(TelaPrincipal.MUTED);
        titleBlock.add(lblStatus);
        header.add(titleBlock, BorderLayout.WEST);

        JButton btnAtualizar = TelaPrincipal.btnPrimario("↺  Atualizar");
        btnAtualizar.setPreferredSize(new Dimension(150, 34));
        btnAtualizar.addActionListener(e -> carregarDadosEAtualizar());
        header.add(btnAtualizar, BorderLayout.EAST);

        // ── Grid onde os cartões vão aparecer ──
        gridCartoes = new JPanel(new GridLayout(0, 2, 16, 16));
        gridCartoes.setOpaque(false);

        JScrollPane scroll = new JScrollPane(gridCartoes);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(header, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // MOTOR DE BUSCA (BASE DE DADOS + API)
    // ═══════════════════════════════════════════════════════════════════════════
    private void carregarDadosEAtualizar() {
        lblStatus.setText("A sincronizar com a base de dados e mercado ao vivo...");
        gridCartoes.removeAll();
        gridCartoes.revalidate();
        gridCartoes.repaint();

        new SwingWorker<List<Object[]>, Void>() {
            @Override
            protected List<Object[]> doInBackground() {
                // 1. Busca na Base de Dados (MySQL)
                FinanceiroService financeiroService = AppFactory.getInstance().getFinanceiroService();
                List<Object[]> ativosBD = financeiroService.buscarCarteiraDoUsuario(usuarioId);
                
                if (ativosBD.isEmpty()) return ativosBD;

                // 2. Prepara listas para a API
                Set<String> tickersB3 = new HashSet<>();
                Set<String> idsCripto = new HashSet<>();
                
                for (Object[] a : ativosBD) {
                    String ticker = (String) a[0];
                    String tipo = (String) a[2];
                    if (tipo.equals("Cripto")) {
                        idsCripto.add(ticker); // Com a Binance, usamos o ticker direto (ex: BTC)
                    } else {
                        tickersB3.add(ticker);
                    }
                }

                // 3. Busca preços ao vivo
                Map<String, Double> precosAoVivo = new HashMap<>();
                if (!tickersB3.isEmpty()) precosAoVivo.putAll(buscarPrecosB3(tickersB3));
                if (!idsCripto.isEmpty()) precosAoVivo.putAll(buscarPrecosCripto(idsCripto));

                // 4. Junta os dados (Adiciona o Preço Atual como o 6º elemento do array)
                List<Object[]> carteiraCompleta = new ArrayList<>();
                for (Object[] bd : ativosBD) {
                    String ticker = (String) bd[0];
                    double precoMedio = (Double) bd[4];
                    // Se a API falhar, assume o preço médio (0% variação)
                    double precoAtual = precosAoVivo.getOrDefault(ticker, precoMedio); 
                    
                    Object[] completo = new Object[]{
                        bd[0], // Ticker
                        bd[1], // Nome
                        bd[2], // Tipo
                        bd[3], // Qtd
                        bd[4], // Preco Medio
                        precoAtual // Preco Atual da API
                    };
                    carteiraCompleta.add(completo);
                }
                return carteiraCompleta;
            }

            @Override
            protected void done() {
                try {
                    List<Object[]> carteira = get();
                    
                    if (carteira.isEmpty()) {
                        JLabel vazio = new JLabel("Ainda não possui nenhum ativo na carteira.");
                        vazio.setForeground(TelaPrincipal.MUTED);
                        gridCartoes.add(vazio);
                    } else {
                        for (Object[] ativo : carteira) {
                            gridCartoes.add(buildCard(ativo));
                        }
                    }
                    lblStatus.setText("Visão consolidada atualizada ao vivo.");
                    gridCartoes.revalidate();
                    gridCartoes.repaint();
                } catch (Exception e) {
                    lblStatus.setText("Erro ao carregar carteira.");
                    e.printStackTrace();
                }
            }
        }.execute();
    }
    
    private void efetuarVenda(String ticker, double quantidadePossuida, double precoAtual) {
        try {
            java.text.DecimalFormat dfQtd = new java.text.DecimalFormat("0.########");
            String input = JOptionPane.showInputDialog(this, 
                "Vender " + ticker + "\nQuantidade disponível: " + dfQtd.format(quantidadePossuida) + 
                "\nCotação atual: " + BRL.format(precoAtual) + 
                "\n\nQuantas unidades deseja vender?", 
                "Ordem de Venda", JOptionPane.WARNING_MESSAGE);
                
            if (input == null || input.trim().isEmpty()) return; 
            
            double quantidadeVenda = Double.parseDouble(input.replace(",", "."));
            
            // Validações cruciais de segurança
            if (quantidadeVenda <= 0) {
                JOptionPane.showMessageDialog(this, "A quantidade deve ser maior que zero.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (quantidadeVenda > quantidadePossuida) {
                JOptionPane.showMessageDialog(this, "Saldo insuficiente. Só possui " + dfQtd.format(quantidadePossuida) + " unidades.", "Erro de Saldo", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Traduzir Ticker para ID
            AtivoDAO ativoDAO = new AtivoDAO();
            Ativo ativo = ativoDAO.buscarPorTicker(ticker);
            
            if (ativo == null) {
                JOptionPane.showMessageDialog(this, "Erro: Ativo não encontrado no sistema.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Efetuar a venda na base de dados
            FinanceiroDAO financeiroDAO = new FinanceiroDAO();
            financeiroDAO.venderAtivo(this.usuarioId, ativo.getId(), quantidadeVenda, precoAtual);

            JOptionPane.showMessageDialog(this, 
                "Venda de " + dfQtd.format(quantidadeVenda) + "x " + ticker + " realizada com sucesso!\nO seu saldo foi atualizado.", 
                "Ordem Executada", JOptionPane.INFORMATION_MESSAGE);

            // Atualiza o ecrã automaticamente para refletir o novo saldo
            carregarDadosEAtualizar();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, introduza um valor numérico válido.", "Erro de Digitação", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao processar venda: " + ex.getMessage(), "Erro no Sistema", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // CONSTRUÇÃO DO CARTÃO COM MATEMÁTICA FINANCEIRA
    // ═══════════════════════════════════════════════════════════════════════════
    private JPanel buildCard(Object[] a) {
        String ticker = (String) a[0];
        String nome   = (String) a[1];
        String tipo   = (String) a[2];
        double qtd    = (Double) a[3];
        double medio  = (Double) a[4];
        double atual  = (Double) a[5];

        // Cálculos protegidos
        double totalInvestido = qtd * medio;
        double saldoAtual     = qtd * atual;
        double lucroValor     = saldoAtual - totalInvestido;
        
        // Proteção contra divisão por zero se o preço médio por algum erro for 0
        double variacaoPct = (medio > 0) ? ((atual / medio) - 1) * 100 : 0.0;
        
        boolean emAlta = variacaoPct >= 0;
        
        // Formatação inteligente e universal para Ações e Cripto
        java.text.DecimalFormat dfQtd = new java.text.DecimalFormat("0.########");
        String qtdStr = dfQtd.format(qtd);

        TelaPrincipal.CardPanel card = new TelaPrincipal.CardPanel(12);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        // ── Cabeçalho ──
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel tickerInfo = new JPanel();
        tickerInfo.setOpaque(false);
        tickerInfo.setLayout(new BoxLayout(tickerInfo, BoxLayout.Y_AXIS));

        JLabel lblTicker = new JLabel(ticker);
        lblTicker.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTicker.setForeground(TelaPrincipal.TEXT);

        JLabel lblNome = new JLabel(nome);
        lblNome.setFont(TelaPrincipal.F_SMALL);
        lblNome.setForeground(TelaPrincipal.MUTED);

        tickerInfo.add(lblTicker);
        tickerInfo.add(lblNome);

        // Badge de Variação (% Verde ou Vermelho)
        String sinal = emAlta ? "+" : "";
        JLabel badgeVar = new JLabel("  " + sinal + String.format("%.2f%%", Double.isNaN(variacaoPct) ? 0 : variacaoPct) + "  ");
        badgeVar.setFont(TelaPrincipal.F_SMALL);
        badgeVar.setForeground(emAlta ? TelaPrincipal.GREEN : TelaPrincipal.RED);
        badgeVar.setOpaque(true);
        badgeVar.setBackground(emAlta ? new Color(35, 197, 94, 35) : new Color(218, 54, 51, 35));
        badgeVar.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));

        JPanel badgeWrap = new JPanel(new GridBagLayout());
        badgeWrap.setOpaque(false);
        badgeWrap.add(badgeVar);

        header.add(tickerInfo, BorderLayout.WEST);
        header.add(badgeWrap,  BorderLayout.EAST);

        // ── Detalhes Matemáticos ──
        JPanel details = new JPanel(new GridLayout(4, 2, 6, 8));
        details.setOpaque(false);
        details.setBorder(BorderFactory.createEmptyBorder(16, 0, 16, 0));

        addDetailRow(details, "Quantidade", qtdStr);
        addDetailRow(details, "Preço Médio", BRL.format(medio));
        addDetailRow(details, "Cotação Atual", BRL.format(atual));
        
        // Linha de Lucro/Prejuízo colorida
        JLabel lblResultadoDesc = new JLabel(emAlta ? "Lucro Atual" : "Prejuízo Atual");
        lblResultadoDesc.setFont(TelaPrincipal.F_SMALL);
        lblResultadoDesc.setForeground(TelaPrincipal.MUTED);
        
        JLabel lblResultadoValor = new JLabel(sinal + BRL.format(lucroValor));
        lblResultadoValor.setFont(TelaPrincipal.F_BODY);
        lblResultadoValor.setForeground(emAlta ? TelaPrincipal.GREEN : TelaPrincipal.RED);
        
        details.add(lblResultadoDesc);
        details.add(lblResultadoValor);

        // ── Botões ──
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btns.setOpaque(false);

        JButton vender = TelaPrincipal.btnPerigo("Vender");
        vender.setPreferredSize(new Dimension(90, 32));
        
        // A Ação do Botão: chama o método passando o Ticker, a Quantidade Atual e o Preço da API
        vender.addActionListener(e -> efetuarVenda(ticker, qtd, atual));
        
        btns.add(vender);

        card.add(header,  BorderLayout.NORTH);
        card.add(details, BorderLayout.CENTER);
        card.add(btns,    BorderLayout.SOUTH);
        return card;
    }

    private void addDetailRow(JPanel panel, String label, String value) {
        JLabel l = new JLabel(label);
        l.setFont(TelaPrincipal.F_SMALL);
        l.setForeground(TelaPrincipal.MUTED);

        JLabel v = new JLabel(value);
        v.setFont(TelaPrincipal.F_BODY);
        v.setForeground(TelaPrincipal.TEXT);

        panel.add(l);
        panel.add(v);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // INTEGRAÇÃO COM AS APIS
    // ═══════════════════════════════════════════════════════════════════════════
    private Map<String, Double> buscarPrecosB3(Set<String> tickers) {
        Map<String, Double> precos = new HashMap<>();
        try {
            String symbols = String.join(",", tickers);
            URL url = new URL("https://brapi.dev/api/quote/" + symbols + "?token=demo");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
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

    // ─── NOVO MOTOR BINANCE PARA CRIPTOMOEDAS ───
    private Map<String, Double> buscarPrecosCripto(Set<String> tickersCripto) {
        Map<String, Double> precos = new HashMap<>();
        
        for (String ticker : tickersCripto) {
            try {
                String symbol = ticker.toUpperCase() + "BRL"; 
                
                URL url = new URL("https://api.binance.com/api/v3/ticker/price?symbol=" + symbol);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                conn.setConnectTimeout(5000);
                
                if (conn.getResponseCode() == 200) {
                    String json = lerStream(conn.getInputStream());
                    double price = extrairDouble(json, "price");
                    
                    if (price > 0) {
                        precos.put(ticker.toUpperCase(), price);
                    }
                }
                conn.disconnect();
                
            } catch (Exception e) {
                // Erro ao buscar preço — ignorado silenciosamente
            }
        }
        return precos;
    }

    // ─── Utilitários de Parsing ───
    private String lerStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            String linha;
            while ((linha = br.readLine()) != null) sb.append(linha);
        }
        return sb.toString();
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
        
        // Pula espaços ou aspas (Crucial para a API da Binance)
        while (idx < json.length() && (json.charAt(idx) == ' ' || json.charAt(idx) == '"')) idx++;
        
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
}
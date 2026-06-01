package com.usjt.projeto_a3.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Janela principal do sistema. Contém a sidebar de navegação e o
 * CardLayout que troca entre os painéis.
 *
 * Para abrir a partir da tela de login:
 * new TelaPrincipal(idLogado, "Administrador", true).setVisible(true);
 */
public class TelaPrincipal extends JFrame {

    // ─── Design Tokens ────────────────────────────────────────────────────────
    public static final Color BG      = new Color(13,  17,  23);
    public static final Color SIDEBAR = new Color(22,  27,  34);
    public static final Color CARD    = new Color(33,  38,  45);
    public static final Color BORDER  = new Color(48,  54,  61);
    public static final Color ACCENT  = new Color(37,  99, 235);
    public static final Color TEXT    = new Color(230, 237, 243);
    public static final Color MUTED   = new Color(125, 133, 144);
    public static final Color GREEN   = new Color(35,  197,  94);
    public static final Color RED     = new Color(218,  54,  51);
    public static final Color YELLOW  = new Color(210, 153,  34);

    public static final Font F_TITLE = new Font("Segoe UI", Font.BOLD,  20);
    public static final Font F_SUB   = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font F_BODY  = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font F_SMALL = new Font("Segoe UI", Font.PLAIN, 11);

    // ─── Chaves de Navegação ──────────────────────────────────────────────────
    static final String NAV_DASHBOARD = "dashboard";
    static final String NAV_CARTEIRA  = "carteira";
    static final String NAV_HISTORICO = "historico"; // Chave atualizada para o Histórico
    static final String NAV_USUARIOS  = "usuarios";
    static final String NAV_MERCADO   = "mercado";

    // ─── Estado ───────────────────────────────────────────────────────────────
    private final String  nomeUsuario;
    private final boolean isAdmin;
    private CardLayout cards;
    private JPanel     contentArea;
    private JButton    ativo; // botão da nav atualmente selecionado
    private int usuarioIdLogado; 

    private JButton btnDash, btnCart, btnHist, btnUsers, btnMarket;

    // ─── Construtor ───────────────────────────────────────────────────────────
    public TelaPrincipal(int idUsuario, String nomeUsuario, boolean isAdmin) {
        this.nomeUsuario = nomeUsuario;
        this.isAdmin     = isAdmin;
        this.usuarioIdLogado = idUsuario;
        build();
    }

    private void build() {
        setTitle("InvestSys – Sistema de Gestão de Investimentos");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 720);
        setMinimumSize(new Dimension(1000, 620));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        root.add(buildTopBar(),   BorderLayout.NORTH);
        root.add(buildMain(),     BorderLayout.CENTER);
        root.add(buildFooter(),   BorderLayout.SOUTH);
        setContentPane(root);

        // Seleciona o Dashboard por padrão
        navigate(NAV_DASHBOARD, btnDash);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // BARRA SUPERIOR
    // ═══════════════════════════════════════════════════════════════════════════
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(SIDEBAR);
        bar.setPreferredSize(new Dimension(0, 56));
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));

        JLabel welcome = new JLabel("   Bem-vindo(a) ao sistema");
        welcome.setFont(F_BODY);
        welcome.setForeground(MUTED);

        // ── Painel do usuário (direita) ──────────────────────────────────────
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        right.setOpaque(false);

        // Avatar circular com inicial do nome
        JLabel avatar = new JLabel(String.valueOf(nomeUsuario.charAt(0)).toUpperCase()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ACCENT);
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatar.setPreferredSize(new Dimension(36, 36));
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 15));
        avatar.setForeground(Color.WHITE);

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        JLabel lblNome = new JLabel(nomeUsuario);
        lblNome.setFont(F_SUB);
        lblNome.setForeground(TEXT);
        JLabel lblRole = new JLabel(isAdmin ? "ADMIN" : "USER");
        lblRole.setFont(F_SMALL);
        lblRole.setForeground(isAdmin ? ACCENT : MUTED);
        info.add(lblNome);
        info.add(lblRole);

        JButton btnSair = new JButton("Sair →");
        btnSair.setFont(F_BODY);
        btnSair.setForeground(RED);
        btnSair.setContentAreaFilled(false);
        btnSair.setBorderPainted(false);
        btnSair.setFocusPainted(false);
        btnSair.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSair.addActionListener(e -> confirmarSaida());

        right.add(avatar);
        right.add(info);
        right.add(btnSair);

        // Wrapper para centralizar verticalmente
        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setOpaque(false);
        wrap.add(right);

        bar.add(welcome, BorderLayout.WEST);
        bar.add(wrap,    BorderLayout.EAST);
        return bar;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // LAYOUT PRINCIPAL (sidebar + conteúdo)
    // ═══════════════════════════════════════════════════════════════════════════
    private JPanel buildMain() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);
        p.add(buildSidebar(), BorderLayout.WEST);
        p.add(buildContent(), BorderLayout.CENTER);
        return p;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // SIDEBAR
    // ═══════════════════════════════════════════════════════════════════════════
    private JPanel buildSidebar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(SIDEBAR);
        panel.setPreferredSize(new Dimension(210, 0));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER));

        // Logo
        JPanel logoArea = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 16));
        logoArea.setOpaque(false);
        JLabel logo = new JLabel("📈  InvestSys");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        logo.setForeground(TEXT);
        logoArea.add(logo);

        // Botões de navegação
        JPanel nav = new JPanel();
        nav.setOpaque(false);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));

        btnDash   = navBtn("🗂  Dashboard",      NAV_DASHBOARD);
        btnCart   = navBtn("💼  Minha Carteira",  NAV_CARTEIRA);
        btnHist   = navBtn("📜  Histórico",       NAV_HISTORICO); // Substituído Operações por Histórico
        btnUsers  = navBtn("👤  Usuários",        NAV_USUARIOS);
        btnMarket = navBtn("📊  Mercado",         NAV_MERCADO);

        nav.add(btnDash);
        nav.add(btnCart);
        nav.add(btnHist);
        if (isAdmin) nav.add(btnUsers); // Usuários só aparece para ADMIN
        nav.add(btnMarket);

        panel.add(logoArea, BorderLayout.NORTH);
        panel.add(nav,      BorderLayout.CENTER);
        return panel;
    }

    /** Cria um botão de navegação estilizado para a sidebar. */
    private JButton navBtn(String label, String key) {
        JButton b = new JButton(label);
        b.setFont(F_BODY);
        b.setForeground(MUTED);
        b.setBackground(SIDEBAR);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (ativo != b) { b.setBackground(CARD); b.setForeground(TEXT); }
            }
            @Override public void mouseExited(MouseEvent e) {
                if (ativo != b) { b.setBackground(SIDEBAR); b.setForeground(MUTED); }
            }
        });

        b.addActionListener(e -> navigate(key, b));
        return b;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // ÁREA DE CONTEÚDO (CardLayout)
    // ═══════════════════════════════════════════════════════════════════════════
    private JPanel buildContent() {
        cards       = new CardLayout();
        contentArea = new JPanel(cards);
        contentArea.setBackground(BG);

        // Painéis instanciados
        contentArea.add(new PainelDashboard(this.usuarioIdLogado), NAV_DASHBOARD);
        contentArea.add(new PainelCarteira(this.usuarioIdLogado), NAV_CARTEIRA);
        contentArea.add(new PainelHistorico(this.usuarioIdLogado), NAV_HISTORICO); // Instancia o novo PainelHistorico
        contentArea.add(new PainelUsuarios(), NAV_USUARIOS);
        contentArea.add(new PainelMercado(), NAV_MERCADO);

        return contentArea;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // RODAPÉ
    // ═══════════════════════════════════════════════════════════════════════════
    private JPanel buildFooter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
        p.setBackground(SIDEBAR);
        p.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));
        JLabel ver = new JLabel("Sistema Versão 1.0.0");
        ver.setFont(F_SMALL);
        ver.setForeground(MUTED);
        p.add(ver);
        return p;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // LÓGICA DE NAVEGAÇÃO
    // ═══════════════════════════════════════════════════════════════════════════
    void navigate(String key, JButton btn) {
        // Reseta o botão anterior
        if (ativo != null) {
            ativo.setBackground(SIDEBAR);
            ativo.setForeground(MUTED);
        }
        // Ativa o novo
        ativo = btn;
        btn.setBackground(ACCENT);
        btn.setForeground(Color.WHITE);
        // Mostra o painel correspondente
        cards.show(contentArea, key);
    }

    private void confirmarSaida() {
        int r = JOptionPane.showConfirmDialog(this,
                "Deseja mesmo sair do sistema?",
                "Confirmar Saída",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (r == JOptionPane.YES_OPTION) {
            dispose();
            // new TelaLogin().setVisible(true); // ← descomente quando tiver a tela de login
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // COMPONENTES REUTILIZÁVEIS (usados pelos painéis filhos)
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Painel com fundo arredondado na cor CARD.
     * Todos os painéis-card do sistema usam essa classe.
     */
    public static class CardPanel extends JPanel {
        private final int r;
        public CardPanel(int radius) {
            this.r = radius;
            setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(CARD);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), r, r);
            g2.dispose();
        }
    }

    /** Botão azul primário com cantos arredondados. */
    public static JButton btnPrimario(String label) {
        JButton b = new JButton(label) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c = getModel().isPressed()  ? ACCENT.darker() :
                          getModel().isRollover() ? new Color(59, 130, 246) : ACCENT;
                g2.setColor(c);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(F_BODY);
        b.setForeground(Color.WHITE);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(false);
        return b;
    }

    /** Botão vermelho (perigo / vender). */
    public static JButton btnPerigo(String label) {
        JButton b = new JButton(label) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? RED.darker() : RED);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(F_BODY);
        b.setForeground(Color.WHITE);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(false);
        return b;
    }

    /** Botão com borda (outline). */
    public static JButton btnOutline(String label) {
        JButton b = new JButton(label);
        b.setFont(F_BODY);
        b.setForeground(TEXT);
        b.setBackground(CARD);
        b.setBorder(BorderFactory.createLineBorder(BORDER, 1, true));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    /** Título de seção padronizado. */
    public static JLabel sectionTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(F_TITLE);
        l.setForeground(TEXT);
        return l;
    }

    /** Sub-título de seção (para dividir seções dentro de um painel). */
    public static JLabel sectionSubtitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(F_SUB);
        l.setForeground(TEXT);
        return l;
    }
    
    public int getUsuarioIdLogado() {
        return usuarioIdLogado;
    }
}
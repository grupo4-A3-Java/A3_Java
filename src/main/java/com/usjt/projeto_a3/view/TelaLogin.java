package com.usjt.projeto_a3.view;

import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import com.usjt.projeto_a3.dao.UsuarioDAO;
import com.usjt.projeto_a3.exception.ValidationException;
import com.usjt.projeto_a3.model.Usuario;
import com.usjt.projeto_a3.util.UIUtils;

public class TelaLogin extends JFrame {

    private JTextField     campoEmail;
    private JPasswordField campoSenha;
    private JButton        btnEntrar;
    private JButton        btnCriarConta;
    private JLabel         lblErro;

    public TelaLogin() {
        build();
    }

    private void build() {
        setTitle("InvestSys – Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(460, 580);
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(TelaPrincipal.BG);
        setContentPane(root);

        root.add(buildCard());
    }

    // ─── Card central ─────────────────────────────────────────────────────────
    private JPanel buildCard() {
        TelaPrincipal.CardPanel card = new TelaPrincipal.CardPanel(16);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(40, 40, 36, 40));
        card.setPreferredSize(new Dimension(380, 480));

        // ── Logo ──────────────────────────────────────────────────────────
        JPanel logoArea = new JPanel();
        logoArea.setOpaque(false);
        logoArea.setLayout(new BoxLayout(logoArea, BoxLayout.Y_AXIS));

        JLabel icone = new JLabel("📈") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(TelaPrincipal.ACCENT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        icone.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        icone.setHorizontalAlignment(SwingConstants.CENTER);
        icone.setPreferredSize(new Dimension(60, 60));
        icone.setMaximumSize(new Dimension(60, 60));
        icone.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titulo = new JLabel("Sistema de Gestão");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(TelaPrincipal.TEXT);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel("Plataforma de Investimentos");
        subtitulo.setFont(TelaPrincipal.F_SMALL);
        subtitulo.setForeground(TelaPrincipal.MUTED);
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        logoArea.add(icone);
        logoArea.add(Box.createVerticalStrut(14));
        logoArea.add(titulo);
        logoArea.add(Box.createVerticalStrut(4));
        logoArea.add(subtitulo);

        // ── Campos ────────────────────────────────────────────────────────
        campoEmail = UIUtils.buildTextFieldWithPlaceholder("Digite seu email");
        campoSenha = UIUtils.buildPasswordFieldWithPlaceholder("Digite sua senha");

        // ── Label de erro ─────────────────────────────────────────────────
        lblErro = new JLabel(" ");
        lblErro.setFont(TelaPrincipal.F_SMALL);
        lblErro.setForeground(TelaPrincipal.RED);
        lblErro.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Botões ────────────────────────────────────────────────────────
        btnEntrar = TelaPrincipal.btnPrimario("Entrar");
        btnEntrar.setPreferredSize(new Dimension(300, 42));
        btnEntrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnEntrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnEntrar.addActionListener(e -> tentarLogin());

        // Permite logar com Enter nos campos
        ActionListener enterLogin = e -> tentarLogin();
        campoEmail.addActionListener(enterLogin);
        campoSenha.addActionListener(enterLogin);

        // ── Link criar conta ──────────────────────────────────────────────
        JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        linkPanel.setOpaque(false);
        JLabel lblJa = new JLabel("Não tem uma conta?");
        lblJa.setFont(TelaPrincipal.F_SMALL);
        lblJa.setForeground(TelaPrincipal.MUTED);
        btnCriarConta = new JButton("Criar conta");
        btnCriarConta.setFont(TelaPrincipal.F_SMALL);
        btnCriarConta.setForeground(TelaPrincipal.ACCENT);
        btnCriarConta.setContentAreaFilled(false);
        btnCriarConta.setBorderPainted(false);
        btnCriarConta.setFocusPainted(false);
        btnCriarConta.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCriarConta.addActionListener(e -> abrirCadastro());
        linkPanel.add(lblJa);
        linkPanel.add(btnCriarConta);

        // ── Hint de acesso de teste ───────────────────────────────────────
        JPanel hint = buildHint();

        // ── Montagem do card ──────────────────────────────────────────────
        card.add(logoArea);
        card.add(Box.createVerticalStrut(30));
        card.add(UIUtils.buildFieldBlock("Email", campoEmail));
        card.add(Box.createVerticalStrut(14));
        card.add(UIUtils.buildFieldBlock("Senha", campoSenha));
        card.add(Box.createVerticalStrut(6));
        card.add(lblErro);
        card.add(Box.createVerticalStrut(6));
        card.add(btnEntrar);
        card.add(Box.createVerticalStrut(16));
        card.add(linkPanel);
        card.add(Box.createVerticalStrut(20));
        card.add(hint);

        return card;
    }

    // ─── Hint de credenciais de teste ─────────────────────────────────────────
    private JPanel buildHint() {
        JPanel p = new JPanel();
        p.setOpaque(true);
        p.setBackground(new Color(33, 38, 45));
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(TelaPrincipal.BORDER, 1, true),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        p.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        String[] linhas = {
            "Acessos de teste:",
            "ADMIN: admin@sistema.com / 123",
            "USER:  user@sistema.com / 123"
        };
        for (String linha : linhas) {
            JLabel l = new JLabel(linha);
            l.setFont(TelaPrincipal.F_SMALL);
            l.setForeground(TelaPrincipal.MUTED);
            l.setAlignmentX(Component.CENTER_ALIGNMENT);
            p.add(l);
        }
        return p;
    }

    // ─── LÓGICA DE LOGIN COM VERIFICAÇÃO DE STATUS ───
    private void tentarLogin() {
        
        String email = campoEmail.getText().trim();
        String senha = new String(campoSenha.getPassword());
        
        if (email.isEmpty() || senha.isEmpty() || email.equals("Digite seu email") || senha.equals("Digite sua senha")) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Por favor, preencha todos os campos.", 
                "Campos Vazios", 
                javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            UsuarioDAO usuarioDao = new UsuarioDAO();
            Usuario usuarioLogado = usuarioDao.buscarPorEmailESenha(email, senha);
            
            boolean isAdmin = "ADMIN".equalsIgnoreCase(usuarioLogado.getPerfil());
            
            TelaPrincipal principal = new TelaPrincipal(usuarioLogado.getId(), usuarioLogado.getNome(), isAdmin);
            principal.setVisible(true);
            
            this.dispose(); // Fecha a tela de login
            
        } catch (ValidationException ex) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                ex.getMessage(), 
                "Erro de Autenticação", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            String mensagem = "Erro interno do sistema";
            javax.swing.JOptionPane.showMessageDialog(this, 
                mensagem,
                "Erro no Sistema", 
                javax.swing.JOptionPane.ERROR_MESSAGE); 
        }
    }

    private void abrirCadastro() {
        // Se a sua TelaCadastro também tinha erro com o ano de nascimento,
        // vamos precisar ajustá-la a seguir para usar o Status.
        new TelaCadastro(this).setVisible(true);
    }
    // ─── Main para testar isoladamente ───────────────────────────────────────
    public static void main(String[] args) {
        FlatDarkLaf.setup();
        SwingUtilities.invokeLater(() -> new TelaLogin().setVisible(true));
    }
}
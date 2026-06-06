package com.usjt.projeto_a3.view;

import com.usjt.projeto_a3.model.Usuario;
import com.usjt.projeto_a3.factory.AppFactory;
import com.usjt.projeto_a3.service.UsuarioService;
import com.usjt.projeto_a3.util.UIUtils;
import javax.swing.*;
import java.awt.*;
import java.time.Year;

/**
 * Tela de Cadastro — abre como JDialog a partir da TelaLogin.
 *
 * Campos: Nome Completo, Email, Senha, Confirmar Senha, Ano de Nascimento.
 */
public class TelaCadastro extends JDialog {

    private JTextField     campoNome;
    private JTextField     campoEmail;
    private JPasswordField campoSenha;
    private JPasswordField campoConfirmar;
    private JTextField     campoAno;
    private JLabel         lblErro;
    private JButton        btnCadastrar;

    public TelaCadastro(JFrame parent) {
        super(parent, "Criar Conta", true); // true = modal
        build();
    }

    private void build() {
        setSize(460, 640);
        setResizable(false);
        setLocationRelativeTo(getParent());

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(TelaPrincipal.BG);
        setContentPane(root);

        root.add(buildCard());
    }

    // ─── Card central ─────────────────────────────────────────────────────────
    private JPanel buildCard() {
        TelaPrincipal.CardPanel card = new TelaPrincipal.CardPanel(16);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(36, 40, 32, 40));
        card.setPreferredSize(new Dimension(380, 560));

        // ── Cabeçalho ─────────────────────────────────────────────────────
        JLabel titulo = new JLabel("Criar Conta");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(TelaPrincipal.TEXT);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Preencha seus dados para começar");
        sub.setFont(TelaPrincipal.F_SMALL);
        sub.setForeground(TelaPrincipal.MUTED);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Campos ────────────────────────────────────────────────────────
        campoNome      = UIUtils.buildTextField();
        campoEmail     = UIUtils.buildTextField();
        campoSenha     = UIUtils.buildPasswordField();
        campoConfirmar = UIUtils.buildPasswordField();
        campoAno       = UIUtils.buildTextField();

        // ── Label de erro ─────────────────────────────────────────────────
        lblErro = new JLabel(" ");
        lblErro.setFont(TelaPrincipal.F_SMALL);
        lblErro.setForeground(TelaPrincipal.RED);
        lblErro.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ── Botão cadastrar ───────────────────────────────────────────────
        btnCadastrar = TelaPrincipal.btnPrimario("Criar Conta");
        btnCadastrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnCadastrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCadastrar.addActionListener(e -> tentarCadastro());

        // ── Link voltar ao login ───────────────────────────────────────────
        JPanel linkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        linkPanel.setOpaque(false);
        JLabel lblJa = new JLabel("Já tem uma conta?");
        lblJa.setFont(TelaPrincipal.F_SMALL);
        lblJa.setForeground(TelaPrincipal.MUTED);
        JButton btnVoltar = new JButton("Fazer login");
        btnVoltar.setFont(TelaPrincipal.F_SMALL);
        btnVoltar.setForeground(TelaPrincipal.ACCENT);
        btnVoltar.setContentAreaFilled(false);
        btnVoltar.setBorderPainted(false);
        btnVoltar.setFocusPainted(false);
        btnVoltar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnVoltar.addActionListener(e -> dispose());
        linkPanel.add(lblJa);
        linkPanel.add(btnVoltar);

        // ── Montagem ──────────────────────────────────────────────────────
        card.add(titulo);
        card.add(Box.createVerticalStrut(4));
        card.add(sub);
        card.add(Box.createVerticalStrut(28));
        card.add(UIUtils.buildFieldBlock("Nome Completo",      campoNome));
        card.add(Box.createVerticalStrut(14));
        card.add(UIUtils.buildFieldBlock("Email",              campoEmail));
        card.add(Box.createVerticalStrut(14));
        card.add(UIUtils.buildFieldBlock("Senha",              campoSenha));
        card.add(Box.createVerticalStrut(14));
        card.add(UIUtils.buildFieldBlock("Confirmar Senha",    campoConfirmar));
        card.add(Box.createVerticalStrut(14));
        card.add(UIUtils.buildFieldBlock("Ano de Nascimento",  campoAno));
        card.add(Box.createVerticalStrut(8));
        card.add(lblErro);
        card.add(Box.createVerticalStrut(8));
        card.add(btnCadastrar);
        card.add(Box.createVerticalStrut(16));
        card.add(linkPanel);

        return card;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // LÓGICA DE CADASTRO
    // ═══════════════════════════════════════════════════════════════════════════
    private void tentarCadastro() {
        String nome      = campoNome.getText().trim();
        String email     = campoEmail.getText().trim();
        String senha     = new String(campoSenha.getPassword());
        String confirmar = new String(campoConfirmar.getPassword());
        String anoStr    = campoAno.getText().trim();

        // ── Validações ────────────────────────────────────────────────────
        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()
                || confirmar.isEmpty() || anoStr.isEmpty()) {
            mostrarErro("Preencha todos os campos.");
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            mostrarErro("Email inválido.");
            return;
        }

        if (senha.length() < 3) {
            mostrarErro("A senha deve ter ao menos 3 caracteres.");
            return;
        }

        if (!senha.equals(confirmar)) {
            mostrarErro("As senhas não coincidem.");
            return;
        }

        int anoNasc;
        try {
            anoNasc = Integer.parseInt(anoStr);
        } catch (NumberFormatException e) {
            mostrarErro("Ano de nascimento inválido.");
            return;
        }

        int anoAtual = Year.now().getValue();
        if (anoNasc < 1900 || anoNasc > anoAtual) {
            mostrarErro("Ano de nascimento inválido.");
            return;
        }

        if ((anoAtual - anoNasc) < 18) {
            mostrarErro("É necessário ter ao menos 18 anos.");
            return;
        }

        
        Usuario novoUsuario = new Usuario();
        
        novoUsuario.setNome(nome);
        novoUsuario.setEmail(email);
        novoUsuario.setSenha(senha);
        novoUsuario.setPerfil("USER");
        novoUsuario.setDataNascimento(Integer.parseInt(anoStr));
        
        try {
            UsuarioService service = AppFactory.getInstance().getUsuarioService();
            service.salvar(novoUsuario);
            
            JOptionPane.showMessageDialog(this,
            "Conta criada com sucesso!\nFaça login para entrar.",
            "Cadastro realizado",
            JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
        }catch (RuntimeException ex){
            mostrarErro(ex.getMessage());
        }

        
    }

    private void mostrarErro(String msg) {
        lblErro.setText(msg);
    }
}

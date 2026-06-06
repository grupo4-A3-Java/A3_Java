package com.usjt.projeto_a3.view;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import com.usjt.projeto_a3.dao.UsuarioDAO;
import com.usjt.projeto_a3.factory.AppFactory;
import com.usjt.projeto_a3.service.UsuarioService;
import com.usjt.projeto_a3.model.Usuario;

public class PainelUsuarios extends JPanel {

    private static final String[] COLUNAS = {
        "ID", "Nome", "E-mail", "Perfil", "Status", "Ações"
    };
    
    // Variáveis da classe (Acessíveis em qualquer lado do código)
    private List<Usuario> usuariosCache; 
    private DefaultTableModel modeloTabela;
    private JTable tabela;

    public PainelUsuarios() {
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));
        build();
    }
    
    // ── Método que busca no banco e preenche a tabela
    private void carregarUsuariosNaTabela() {
        try {
            modeloTabela.setRowCount(0); // Limpa a tabela antes de preencher
            
            UsuarioService usuarioService = AppFactory.getInstance().getUsuarioService();
            usuariosCache = usuarioService.listarTodos(); // Guarda na memória
            
            for (Usuario u : usuariosCache) {
                modeloTabela.addRow(new Object[]{
                    u.getId(),
                    u.getNome(),
                    u.getEmail(),
                    u.getPerfil(),
                    u.getStatus(), // Busca o status real da base de dados
                    "✏  Editar"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erro ao carregar a lista de usuários: " + e.getMessage(), 
                "Erro de Banco de Dados", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void build() {
        // Cabeçalho
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.add(TelaPrincipal.sectionTitle("Gerenciamento de Usuários"));
        JLabel sub = new JLabel("Administre os acessos ao sistema");
        sub.setFont(TelaPrincipal.F_SMALL);
        sub.setForeground(TelaPrincipal.MUTED);
        titleBlock.add(sub);
        header.add(titleBlock, BorderLayout.WEST);

        JButton btnNovo = TelaPrincipal.btnPrimario("+ Novo Usuário");
        btnNovo.setPreferredSize(new Dimension(145, 34));
        
        // Ação do Botão Novo
        btnNovo.addActionListener(e -> abrirFormularioUsuario(null));
        
        header.add(btnNovo, BorderLayout.EAST);

        // Instanciar a Tabela
        modeloTabela = new DefaultTableModel(new Object[][]{}, COLUNAS) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        
        tabela = new JTable(modeloTabela);
        estilizarTabela(tabela);

        // Ação de Clique na Tabela (Para Editar)
        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int linha = tabela.rowAtPoint(e.getPoint());
                int coluna = tabela.columnAtPoint(e.getPoint());
                // Se clicou na coluna 5 ("Ações")
                if (linha >= 0 && coluna == 5) {
                    Usuario usuarioClicado = usuariosCache.get(linha);
                    abrirFormularioUsuario(usuarioClicado);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setOpaque(false);
        scroll.getViewport().setBackground(TelaPrincipal.CARD);
        scroll.setBorder(BorderFactory.createLineBorder(TelaPrincipal.BORDER, 1, true));
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(header, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        
        carregarUsuariosNaTabela();
    }

    // ── Formulário Modal de Inserção/Edição ──
    private void abrirFormularioUsuario(Usuario usuarioExistente) {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog((Frame) parentWindow, 
                usuarioExistente == null ? "Novo Usuário" : "Editar Usuário", true);
        
        dialog.setSize(400, 480);
        dialog.setLocationRelativeTo(parentWindow);
        dialog.setResizable(false);
        
        JPanel painelFundo = new JPanel();
        painelFundo.setLayout(new BoxLayout(painelFundo, BoxLayout.Y_AXIS));
        painelFundo.setBackground(TelaPrincipal.BG);
        painelFundo.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Campos do formulário
        JTextField txtNome = criarCampoTexto();
        JTextField txtEmail = criarCampoTexto();
        JPasswordField txtSenha = new JPasswordField();
        estilizarCampoTexto(txtSenha);
        
        JComboBox<String> comboPerfil = new JComboBox<>(new String[]{"USER", "ADMIN"});
        estilizarCombo(comboPerfil);
        
        JComboBox<String> comboStatus = new JComboBox<>(new String[]{"Ativo", "Inativo"});
        estilizarCombo(comboStatus);

        // Se for edição, preenche os campos
        if (usuarioExistente != null) {
            txtNome.setText(usuarioExistente.getNome());
            txtEmail.setText(usuarioExistente.getEmail());
            txtSenha.setText(usuarioExistente.getSenha());
            comboPerfil.setSelectedItem(usuarioExistente.getPerfil());
            comboStatus.setSelectedItem(usuarioExistente.getStatus());
        }

        // Adicionando ao layout
        painelFundo.add(criarBlocoCampo("Nome Completo:", txtNome));
        painelFundo.add(Box.createVerticalStrut(15));
        painelFundo.add(criarBlocoCampo("E-mail:", txtEmail));
        painelFundo.add(Box.createVerticalStrut(15));
        painelFundo.add(criarBlocoCampo("Senha:", txtSenha));
        painelFundo.add(Box.createVerticalStrut(15));
        painelFundo.add(criarBlocoCampo("Perfil de Acesso:", comboPerfil));
        
        // Só mostra a opção de mudar o Status se for edição
        if (usuarioExistente != null) {
            painelFundo.add(Box.createVerticalStrut(15));
            painelFundo.add(criarBlocoCampo("Status da Conta:", comboStatus));
        }

        painelFundo.add(Box.createVerticalStrut(25));

        // ── Botões ──
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        painelBotoes.setOpaque(false);
        
        JButton btnCancelar = TelaPrincipal.btnOutline("Cancelar");
        btnCancelar.addActionListener(e -> dialog.dispose());
        
        JButton btnSalvar = TelaPrincipal.btnPrimario("Salvar");
        btnSalvar.addActionListener(e -> {
            try {
                // Validação básica
                if (txtNome.getText().trim().isEmpty() || txtEmail.getText().trim().isEmpty() || new String(txtSenha.getPassword()).isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Preencha todos os campos obrigatórios.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Usuario u = new Usuario();
                u.setNome(txtNome.getText().trim());
                u.setEmail(txtEmail.getText().trim());
                u.setSenha(new String(txtSenha.getPassword()));
                u.setPerfil(comboPerfil.getSelectedItem().toString());
                
                UsuarioDAO dao = new UsuarioDAO();
                
                if (usuarioExistente == null) {
                    u.setStatus("Ativo");
                    dao.salvar(u);
                } else {
                    u.setId(usuarioExistente.getId());
                    u.setStatus(comboStatus.getSelectedItem().toString());
                    dao.atualizar(u);
                }
                
                dialog.dispose();
                
                // Recarrega a tabela de forma simples e limpa!
                carregarUsuariosNaTabela();
                
                JOptionPane.showMessageDialog(this, "Usuário salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Erro ao salvar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        if (usuarioExistente != null) {
            JButton btnExcluir = TelaPrincipal.btnPerigo("Excluir");
            btnExcluir.addActionListener(e -> {
                int r = JOptionPane.showConfirmDialog(dialog, "Tem a certeza que deseja excluir " + usuarioExistente.getNome() + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
                if (r == JOptionPane.YES_OPTION) {
                    try {
                        new UsuarioDAO().deletar(usuarioExistente.getId());
                        dialog.dispose();
                        carregarUsuariosNaTabela(); // Recarrega a tabela limpa
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Aviso de Segurança", JOptionPane.WARNING_MESSAGE);
                    }
                }
            });
            painelBotoes.add(btnExcluir);
        }

        painelBotoes.add(btnCancelar);
        painelBotoes.add(btnSalvar);
        
        painelFundo.add(painelBotoes);
        
        dialog.add(painelFundo);
        dialog.setVisible(true);
    }

    // ── Métodos Visuais de Ajuda para o Formulário ──
    private JPanel criarBlocoCampo(String labelText, JComponent campo) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(TelaPrincipal.F_SMALL);
        lbl.setForeground(TelaPrincipal.MUTED);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        
        p.add(lbl);
        p.add(Box.createVerticalStrut(4));
        p.add(campo);
        return p;
    }

    private JTextField criarCampoTexto() {
        JTextField t = new JTextField();
        estilizarCampoTexto(t);
        return t;
    }

    private void estilizarCampoTexto(JTextField t) {
        t.setBackground(TelaPrincipal.CARD);
        t.setForeground(TelaPrincipal.TEXT);
        t.setCaretColor(Color.WHITE);
        t.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(TelaPrincipal.BORDER, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        t.setFont(TelaPrincipal.F_BODY);
    }
    
    private void estilizarCombo(JComboBox c) {
        c.setBackground(TelaPrincipal.CARD);
        c.setForeground(TelaPrincipal.TEXT);
        c.setBorder(BorderFactory.createLineBorder(TelaPrincipal.BORDER, 1));
        c.setFont(TelaPrincipal.F_BODY);
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

        // Renderer base
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
            t.getColumnModel().getColumn(i).setCellRenderer(base);
        }

        // Coluna "Perfil"
        t.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(table, value, sel, foc, row, col);
                setBackground(sel ? TelaPrincipal.ACCENT : TelaPrincipal.CARD);
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                if (!sel) {
                    setForeground("ADMIN".equals(value) ? TelaPrincipal.ACCENT : TelaPrincipal.TEXT);
                }
                return this;
            }
        });

        // Coluna "Status"
        t.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(table, value, sel, foc, row, col);
                setBackground(sel ? TelaPrincipal.ACCENT : TelaPrincipal.CARD);
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                if (!sel) {
                    setForeground("Ativo".equals(value) ? TelaPrincipal.GREEN : TelaPrincipal.MUTED);
                }
                return this;
            }
        });

        // Coluna "Ações"
        t.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(table, value, sel, foc, row, col);
                setBackground(sel ? TelaPrincipal.ACCENT : TelaPrincipal.CARD);
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                if (!sel) setForeground(TelaPrincipal.ACCENT);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                return this;
            }
        });

        // Larguras das colunas
        int[] widths = {40, 180, 220, 80, 80, 100};
        for (int i = 0; i < widths.length; i++) {
            t.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }
}
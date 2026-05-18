package com.i08.controller;

import com.i08.db.UserDAO;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

// Classe responsável por abstrair a lógica de eventos da tela de Login.
// Gerencia listeners de botões e campos de entrada.
public class LoginController {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private UserDAO userDAO;

    public LoginController(JTextField usernameField, JPasswordField passwordField, JButton loginButton) {
        this.usernameField = usernameField;
        this.passwordField = passwordField;
        this.loginButton = loginButton;
        this.userDAO = new UserDAO();

        // Ativa os listeners dos componentes
        attachEventListeners();
    }

    // Anexa os listeners aos componentes.
    private void attachEventListeners() {
        // Listener para o botão "Entrar"
        loginButton.addActionListener(e -> handleLogin());

        // Listener detectar a tecla 'Enter' em qualquer campo
        usernameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin();
                }
            }
        });

        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin();
                }
            }
        });
    }

    // Lida com o evento de login.
    // Valida os dados e autentica no banco.
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        // Validação de campos vazios
        if (username.isEmpty() || password.isEmpty()) {
            return;
        }

        // Valida as credenciais no banco
        if (userDAO.validateLogin(username, password)) {
            // TODO: Navegar para a próxima tela
            System.out.println("[LoginController] Bem vindo!");
            clearFields();
        } else {
            clearFields();
        }
    }

    // Limpa os campos de entrada após clicar em 'login' ou apertar 'enter'
    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        usernameField.requestFocus();
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }
}

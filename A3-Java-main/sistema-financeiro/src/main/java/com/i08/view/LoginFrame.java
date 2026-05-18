package com.i08.view;

import com.i08.controller.LoginController;
import com.i08.styles.LoginStyles;
import com.i08.util.Util;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class LoginFrame {
    public LoginFrame(Boolean visible) {
        JFrame loginFrame = new JFrame();
        loginFrame.setTitle("LOGIN");
        loginFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        loginFrame.setContentPane(new LoginPanel());
        loginFrame.setSize(500, 800);
        loginFrame.setResizable(false);
        Util.centerFrame(loginFrame);
        loginFrame.setVisible(visible);
    }
}

class LoginPanel extends JPanel {
    private final JLabel title = new JLabel("LOGIN");
    private final JLabel userJLabel = new JLabel("Usuario:");
    private final JTextField userTextInput = new JTextField(20);
    private final JLabel passwordJLabel = new JLabel("Senha:");
    private final JPasswordField passwordTextInput = new JPasswordField(20);
    private final JButton enterJButton = new JButton("Entrar");
    
    public LoginPanel() {
        setLayout(new GridBagLayout());
        setBackground(LoginStyles.COLOR_BG);

        JPanel card = new JPanel(new GridBagLayout());
        LoginStyles.applyCard(card);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);

        // título
        LoginStyles.applyTitle(title);
        card.add(title, gbc);

        // usuário label
        gbc.gridy++;
        LoginStyles.applyLabel(userJLabel);
        card.add(userJLabel, gbc);

        // usuário input
        gbc.gridy++;
        LoginStyles.applyInput(userTextInput);
        card.add(userTextInput, gbc);

        // senha label
        gbc.gridy++;
        LoginStyles.applyLabel(passwordJLabel);
        card.add(passwordJLabel, gbc);

        // senha input
        gbc.gridy++;
        LoginStyles.applyInput(passwordTextInput);
        card.add(passwordTextInput, gbc);

        // botão
        gbc.gridy++;
        gbc.insets = new Insets(16, 0, 0, 0);
        LoginStyles.applyPrimaryButton(enterJButton);
        card.add(enterJButton, gbc);

        // centralização do card
        GridBagConstraints rootGbc = new GridBagConstraints();
        rootGbc.gridx = 0;
        rootGbc.gridy = 0;
        rootGbc.weightx = 1;
        rootGbc.weighty = 1;
        rootGbc.anchor = GridBagConstraints.CENTER;

        add(card, rootGbc);

        // inicializa os eventos de login
        new LoginController(userTextInput, passwordTextInput, enterJButton);
    }
}

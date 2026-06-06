package com.usjt.projeto_a3;

import com.usjt.projeto_a3.view.TelaLogin;
import javax.swing.UIManager;

/**
 * Ponto de entrada da aplicação InvestSys.
 * Inicializa a UI com tema FlatLaf e abre a tela de login.
 * 
 * @author thiagotago
 */
public class Projeto_A3 {

    public static void main(String[] args) {
        try {
            // Configura o tema FlatLaf
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarkLaf");
        } catch (Exception e) {
            // Erro silencioso ao carregar tema
        }

        // Inicializa a tela de login
        TelaLogin telaLogin = new TelaLogin();
        telaLogin.setVisible(true);
    }
}

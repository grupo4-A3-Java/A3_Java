package com.i08;

import com.i08.config.ApplicationContext;

/**
 * Classe Principal da Aplicação - Sistema Financeiro
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("SISTEMA FINANCEIRO - INICIANDO");
        System.out.println("========================================");
        
        try {
            ApplicationContext context = ApplicationContext.getInstance();
            
            // Executa a inicialização completa da aplicação
            // Verifica conexão com o banco de dados e inicializa os componentes
            if (!context.init()) {
                System.err.println("[Main] Falha na inicialização. Encerrando aplicação.");
                System.exit(1);
            }
            
            System.out.println("[Main] Aplicação iniciada e pronta para uso.");
            
        } catch (Exception e) {
            System.err.println("[Main] Exceção não tratada durante inicialização: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
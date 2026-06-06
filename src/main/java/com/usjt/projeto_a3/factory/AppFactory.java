package com.usjt.projeto_a3.factory;

import com.usjt.projeto_a3.dao.AtivoDAO;
import com.usjt.projeto_a3.dao.FinanceiroDAO;
import com.usjt.projeto_a3.dao.UsuarioDAO;
import com.usjt.projeto_a3.service.AtivoService;
import com.usjt.projeto_a3.service.AuthService;
import com.usjt.projeto_a3.service.FinanceiroService;
import com.usjt.projeto_a3.service.UsuarioService;

public class AppFactory {
    private static AppFactory instancia;

    // DAOs
    private AtivoDAO ativoDAO;
    private UsuarioDAO usuarioDAO;
    private FinanceiroDAO financeiroDAO;

    // Services
    private AuthService authService;
    private AtivoService ativoService;
    private UsuarioService usuarioService;
    private FinanceiroService financeiroService;

    private AppFactory() {
        inicializarDAOs();
        inicializarServicos();
    }

    public static AppFactory getInstance() {
        if (instancia == null) {
            instancia = new AppFactory();
        }
        return instancia;
    }

    private void inicializarDAOs() {
        ativoDAO = new AtivoDAO();
        usuarioDAO = new UsuarioDAO();
        financeiroDAO = new FinanceiroDAO();
    }

    private void inicializarServicos() {
        authService = new AuthService(usuarioDAO);
        ativoService = new AtivoService(ativoDAO);
        usuarioService = new UsuarioService(usuarioDAO);
        financeiroService = new FinanceiroService(financeiroDAO);
    }

    // Getters para DAOs
    public AtivoDAO getAtivoDAO() {
        return ativoDAO;
    }

    public UsuarioDAO getUsuarioDAO() {
        return usuarioDAO;
    }

    public FinanceiroDAO getFinanceiroDAO() {
        return financeiroDAO;
    }

    // Getters para Serviços
    public AuthService getAuthService() {
        return authService;
    }

    public AtivoService getAtivoService() {
        return ativoService;
    }

    public UsuarioService getUsuarioService() {
        return usuarioService;
    }

    public FinanceiroService getFinanceiroService() {
        return financeiroService;
    }
}

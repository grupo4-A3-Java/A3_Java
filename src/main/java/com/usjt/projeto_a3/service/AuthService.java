package com.usjt.projeto_a3.service;

import com.usjt.projeto_a3.dao.UsuarioDAO;
import com.usjt.projeto_a3.exception.ValidationException;
import com.usjt.projeto_a3.model.Usuario;
import com.usjt.projeto_a3.util.Validator;

public class AuthService {
    private final UsuarioDAO usuarioDAO;

    public AuthService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public Usuario autenticar(String email, String senha) {
        Validator.notEmpty(email, "Email");
        Validator.notEmpty(senha, "Senha");
        Validator.validEmail(email);

        Usuario usuario = usuarioDAO.buscarPorEmailESenha(email, senha);
        if (usuario == null) {
            throw new ValidationException("Email ou senha incorretos.");
        }

        if (!"Ativo".equals(usuario.getStatus())) {
            throw new ValidationException("Usuário inativo. Entre em contato com o suporte.");
        }

        return usuario;
    }
}

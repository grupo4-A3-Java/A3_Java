package com.usjt.projeto_a3.service;

import com.usjt.projeto_a3.dao.UsuarioDAO;
import com.usjt.projeto_a3.exception.ValidationException;
import com.usjt.projeto_a3.model.Usuario;
import com.usjt.projeto_a3.util.Validator;
import java.util.List;

public class UsuarioService {
    private final UsuarioDAO usuarioDAO;

    public UsuarioService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public List<Usuario> listarTodos() {
        return usuarioDAO.listarTodos();
    }

    public Usuario buscarPorId(int id) {
        if (id <= 0) {
            throw new ValidationException("ID deve ser positivo.");
        }
        return usuarioDAO.buscarPorId(id);
    }

    public void salvar(Usuario usuario) {
        validarUsuario(usuario);
        usuarioDAO.salvar(usuario);
    }

    public void atualizar(Usuario usuario) {
        Validator.notNull(usuario, "Usuário");
        if (usuario.getId() <= 0) {
            throw new ValidationException("Não é possível atualizar um usuário sem ID.");
        }
        validarUsuario(usuario);
        usuarioDAO.atualizar(usuario);
    }

    public void deletar(int id) {
        if (id <= 0) {
            throw new ValidationException("ID deve ser positivo.");
        }
        usuarioDAO.deletar(id);
    }

    private void validarUsuario(Usuario usuario) {
        Validator.notNull(usuario, "Usuário");
        Validator.notEmpty(usuario.getNome(), "Nome");
        Validator.notEmpty(usuario.getEmail(), "Email");
        Validator.notEmpty(usuario.getSenha(), "Senha");
        Validator.validEmail(usuario.getEmail());
        Validator.minLength(usuario.getSenha(), 5, "Senha");
    }
}

package com.usjt.projeto_a3.service;

import com.usjt.projeto_a3.dao.FinanceiroDAO;
import com.usjt.projeto_a3.exception.ValidationException;
import com.usjt.projeto_a3.util.Validator;
import java.util.List;

public class FinanceiroService {
    private final FinanceiroDAO financeiroDAO;

    public FinanceiroService(FinanceiroDAO financeiroDAO) {
        this.financeiroDAO = financeiroDAO;
    }

    public void comprarAtivo(int usuarioId, int ativoId, double quantidade, double precoUnitario) {
        validarParametrosOperacao(usuarioId, ativoId, quantidade, precoUnitario);
        financeiroDAO.comprarAtivo(usuarioId, ativoId, quantidade, precoUnitario);
    }

    public void venderAtivo(int usuarioId, int ativoId, double quantidade, double precoUnitario) {
        validarParametrosOperacao(usuarioId, ativoId, quantidade, precoUnitario);
        financeiroDAO.venderAtivo(usuarioId, ativoId, quantidade, precoUnitario);
    }

    public List<Object[]> buscarCarteiraDoUsuario(int usuarioId) {
        if (usuarioId <= 0) {
            throw new ValidationException("ID do usuário deve ser positivo.");
        }
        return financeiroDAO.buscarCarteiraDoUsuario(usuarioId);
    }

    public List<Object[]> buscarHistoricoOperacoes(int usuarioId) {
        if (usuarioId <= 0) {
            throw new ValidationException("ID do usuário deve ser positivo.");
        }
        return financeiroDAO.buscarHistoricoOperacoes(usuarioId);
    }

    private void validarParametrosOperacao(int usuarioId, int ativoId, double quantidade, double precoUnitario) {
        if (usuarioId <= 0) {
            throw new ValidationException("ID do usuário deve ser positivo.");
        }
        if (ativoId <= 0) {
            throw new ValidationException("ID do ativo deve ser positivo.");
        }
        Validator.notZero(quantidade, "Quantidade");
        Validator.notZero(precoUnitario, "Preço unitário");
    }
}

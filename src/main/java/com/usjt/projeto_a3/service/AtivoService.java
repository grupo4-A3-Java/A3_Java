package com.usjt.projeto_a3.service;

import com.usjt.projeto_a3.dao.AtivoDAO;
import com.usjt.projeto_a3.model.Ativo;
import com.usjt.projeto_a3.util.Validator;
import java.util.List;

public class AtivoService {
    private final AtivoDAO ativoDAO;

    public AtivoService(AtivoDAO ativoDAO) {
        this.ativoDAO = ativoDAO;
    }

    public List<Ativo> listarTodos() {
        return ativoDAO.listarTodos();
    }

    public Ativo buscarPorTicker(String ticker) {
        Validator.notEmpty(ticker, "Ticker");
        return ativoDAO.buscarPorTicker(ticker);
    }

    public Ativo buscarPorId(int id) {
        Validator.notNegative(id, "ID");
        if (id <= 0) {
            throw new IllegalArgumentException("ID deve ser positivo.");
        }
        return ativoDAO.buscarPorId(id);
    }
}

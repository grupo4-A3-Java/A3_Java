package com.usjt.projeto_a3.dao;

import com.usjt.projeto_a3.model.Ativo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import com.usjt.projeto_a3.util.ConexaoBanco;

public class AtivoDAO {
    
    public List<Ativo> listarTodos() {
        String sql = "SELECT * FROM ativos";
        List<Ativo> lista = new ArrayList<>();
        try {
                Connection conn = ConexaoBanco.getConexao();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(constroirAtivo(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar catálogo de ativos: " + e.getMessage(), e);
        }
        
        return lista;
    }
    
    public Ativo buscarPorTicker(String ticker) {
        String sql = "SELECT * FROM ativos WHERE ticker = ?";
        try {
            Connection conn = ConexaoBanco.getConexao();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, ticker);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return constroirAtivo(rs);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar ativo por ticker: " + e.getMessage(), e);
        }
        return null;
    }

    public Ativo buscarPorId(int id) {
        String sql = "SELECT * FROM ativos WHERE id = ?";
        try {
            
            Connection conn = ConexaoBanco.getConexao();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return constroirAtivo(rs);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar ativo por ID: " + e.getMessage(), e);
        }
        return null;
    }

    private Ativo constroirAtivo(ResultSet rs) throws java.sql.SQLException {
        Ativo ativo = new Ativo();
        ativo.setId(rs.getInt("id"));
        ativo.setTicker(rs.getString("ticker"));
        ativo.setNome(rs.getString("nome"));
        ativo.setTipo(rs.getString("tipo"));
        return ativo;
    }
}
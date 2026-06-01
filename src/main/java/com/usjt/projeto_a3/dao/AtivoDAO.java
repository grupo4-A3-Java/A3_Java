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

        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Ativo ativo = new Ativo();
                ativo.setId(rs.getInt("id"));
                ativo.setTicker(rs.getString("ticker"));
                ativo.setNome(rs.getString("nome"));
                ativo.setTipo(rs.getString("tipo"));
                
                lista.add(ativo);
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar catálogo de ativos: " + e.getMessage(), e);
        }
        
        return lista;
    }
    
    public Ativo buscarPorTicker(String ticker) {
        String sql = "SELECT * FROM ativos WHERE ticker = ?";
        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, ticker);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Ativo ativo = new Ativo();
                    ativo.setId(rs.getInt("id"));
                    ativo.setTicker(rs.getString("ticker"));
                    ativo.setNome(rs.getString("nome"));
                    ativo.setTipo(rs.getString("tipo"));
                    return ativo;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar ativo por ticker: " + e.getMessage(), e);
        }
        return null; // Retorna nulo se o ativo não existir na base de dados
    }
}
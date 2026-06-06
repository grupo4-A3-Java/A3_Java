package com.usjt.projeto_a3.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.usjt.projeto_a3.util.ConexaoBanco;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class FinanceiroDAO {

    public void comprarAtivo(int usuarioId, int ativoId, double quantidade, double precoUnitario) {
        
        // 1. SQL do Extrato (Operação)
        String sqlOperacao = "INSERT INTO operacoes (usuario_id, ativo_id, tipo, quantidade, preco_unitario) VALUES (?, ?, 'COMPRA', ?, ?)";
        
        // 2. SQL do Saldo (Carteira) com regra de UPSERT do MySQL
        // Se a linha não existe, ele faz o INSERT. Se já existe, ele faz o UPDATE somando a quantidade e recalculando o preço médio.
        String sqlCarteira = "INSERT INTO carteira (usuario_id, ativo_id, quantidade_total, preco_medio) " +
                             "VALUES (?, ?, ?, ?) " +
                             "ON DUPLICATE KEY UPDATE " +
                             "preco_medio = ((quantidade_total * preco_medio) + (VALUES(quantidade_total) * VALUES(preco_medio))) / (quantidade_total + VALUES(quantidade_total)), " +
                             "quantidade_total = quantidade_total + VALUES(quantidade_total)";

        Connection conn = null;

        try {
            conn = ConexaoBanco.getConexao();
            // Desliga o modo automático. Inicia a Transação.
            conn.setAutoCommit(false); 

            try (PreparedStatement psOp = conn.prepareStatement(sqlOperacao);
                 PreparedStatement psCart = conn.prepareStatement(sqlCarteira)) {

                // A. Registra a Operação
                psOp.setInt(1, usuarioId);
                psOp.setInt(2, ativoId);
                psOp.setDouble(3, quantidade);
                psOp.setDouble(4, precoUnitario);
                psOp.executeUpdate();

                // B. Atualiza a Carteira
                psCart.setInt(1, usuarioId);
                psCart.setInt(2, ativoId);
                psCart.setDouble(3, quantidade);
                psCart.setDouble(4, precoUnitario);
                psCart.executeUpdate();

                // C. Se as duas deram certo, "carimba" as duas no banco ao mesmo tempo!
                conn.commit();

            } catch (SQLException e) {
                // Se qualquer um dos dois der erro, desfaz tudo (Rollback) para não corromper os dados
                conn.rollback();
                throw new RuntimeException("Erro ao executar as tabelas de compra. Transação desfeita.", e);
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro de conexão no processamento financeiro: " + e.getMessage(), e);
            
        } finally {
            // Devolve a conexão ao estado normal e fecha
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    // Lembre-se de importar java.sql.ResultSet e java.util.ArrayList / List no topo, se não tiver.
    public List<Object[]> buscarCarteiraDoUsuario(int usuarioId) {
        String sql = "SELECT a.ticker, a.nome, a.tipo, c.quantidade_total, c.preco_medio " +
                     "FROM carteira c " +
                     "JOIN ativos a ON c.ativo_id = a.id " +
                     "WHERE c.usuario_id = ? AND c.quantidade_total > 0";
                     
        List<Object[]> lista = new ArrayList<>();

        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setInt(1, usuarioId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    Object[] item = new Object[5];
                    item[0] = rs.getString("ticker");
                    item[1] = rs.getString("nome");
                    item[2] = rs.getString("tipo");
                    item[3] = rs.getDouble("quantidade_total");
                    item[4] = rs.getDouble("preco_medio");
                    lista.add(item);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar a carteira: " + e.getMessage(), e);
        }
        
        return lista;
    }
    public void venderAtivo(int usuarioId, int ativoId, double quantidadeVenda, double precoAtual) {
        
        // 1. Regista no extrato como 'VENDA'
        String sqlOperacao = "INSERT INTO operacoes (usuario_id, ativo_id, tipo, quantidade, preco_unitario) VALUES (?, ?, 'VENDA', ?, ?)";
        
        // 2. Subtrai a quantidade da carteira
        String sqlCarteiraUpdate = "UPDATE carteira SET quantidade_total = quantidade_total - ? WHERE usuario_id = ? AND ativo_id = ?";
        
        // 3. Limpeza: Se a quantidade chegar a zero (ou menor, por segurança), remove a linha da carteira
        String sqlCarteiraLimpeza = "DELETE FROM carteira WHERE usuario_id = ? AND ativo_id = ? AND quantidade_total <= 0";

        Connection conn = null;

        try {
            conn = ConexaoBanco.getConexao();
            conn.setAutoCommit(false); // Inicia a Transação

            try (PreparedStatement psOp = conn.prepareStatement(sqlOperacao);
                 PreparedStatement psUpdate = conn.prepareStatement(sqlCarteiraUpdate);
                 PreparedStatement psClean = conn.prepareStatement(sqlCarteiraLimpeza)) {

                // A. Gravar a Operação
                psOp.setInt(1, usuarioId);
                psOp.setInt(2, ativoId);
                psOp.setDouble(3, quantidadeVenda);
                psOp.setDouble(4, precoAtual);
                psOp.executeUpdate();

                // B. Atualizar a Carteira (Subtrair)
                psUpdate.setDouble(1, quantidadeVenda);
                psUpdate.setInt(2, usuarioId);
                psUpdate.setInt(3, ativoId);
                psUpdate.executeUpdate();

                // C. Limpar lixo (se vendeu tudo, apaga a linha)
                psClean.setInt(1, usuarioId);
                psClean.setInt(2, ativoId);
                psClean.executeUpdate();

                // Confirma tudo na base de dados!
                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Erro ao executar a venda. Transação desfeita por segurança.", e);
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro de conexão no processamento da venda: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    // Lembre-se de importar java.sql.ResultSet, java.util.ArrayList, java.util.List se faltar
    public List<Object[]> buscarHistoricoUsuario(int usuarioId) {
        String sql = "SELECT o.data_operacao, a.ticker, a.nome, o.tipo, o.quantidade, o.preco_unitario " +
                     "FROM operacoes o " +
                     "JOIN ativos a ON o.ativo_id = a.id " +
                     "WHERE o.usuario_id = ? " +
                     "ORDER BY o.data_operacao DESC";

        List<Object[]> lista = new ArrayList<>();

        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, usuarioId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Object[] linha = new Object[6];
                    // Converte o DATETIME do MySQL para LocalDateTime do Java
                    linha[0] = rs.getTimestamp("data_operacao").toLocalDateTime(); 
                    linha[1] = rs.getString("ticker");
                    linha[2] = rs.getString("nome");
                    linha[3] = rs.getString("tipo");
                    linha[4] = rs.getDouble("quantidade");
                    linha[5] = rs.getDouble("preco_unitario");
                    lista.add(linha);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar o histórico de transações: " + e.getMessage(), e);
        }

        return lista;
    }

    public List<Object[]> buscarHistoricoOperacoes(int usuarioId) {
        return buscarHistoricoUsuario(usuarioId);
    }
}
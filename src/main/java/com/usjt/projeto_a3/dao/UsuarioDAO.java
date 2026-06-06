package com.usjt.projeto_a3.dao; 

import java.util.ArrayList;
import java.util.List;
import com.usjt.projeto_a3.util.ConexaoBanco;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.usjt.projeto_a3.model.Usuario;
import java.sql.SQLException;

public class UsuarioDAO {
    
    // ─── BUSCAR LOGIN ───
    public Usuario buscarPorEmailESenha(String email, String senha){
        String sql = "SELECT * FROM usuarios WHERE email = ? AND senha = ?";
        
        try(Connection conn = ConexaoBanco.getConexao();
            PreparedStatement ps = conn.prepareStatement(sql)){
            
            ps.setString(1, email);
            ps.setString(2, senha);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()){ 
                return constroirUsuario(rs);
            }
            
        } catch(Exception e){
            throw new RuntimeException("Erro ao buscar usuário por email e senha: " + e.getMessage(), e);
        }
        return null;
    }

    // ─── BUSCAR POR ID ───
    public Usuario buscarPorId(int id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        
        try(Connection conn = ConexaoBanco.getConexao();
            PreparedStatement ps = conn.prepareStatement(sql)){
            
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()){ 
                return constroirUsuario(rs);
            }
            
        } catch(Exception e){
            throw new RuntimeException("Erro ao buscar usuário por ID: " + e.getMessage(), e);
        }
        return null;
    }
    
    // ─── LISTAR TODOS ───
    public List<Usuario> listarTodos(){
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";
        
        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()){
            
            while (rs.next()){
                lista.add(constroirUsuario(rs));
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar usuários: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Erro inesperado na conexão com banco: " + e.getMessage(), e);
        }
        
        return lista;
    }
    
    // ─── SALVAR (CREATE) ───
    public void salvar(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nome, email, senha, perfil, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, usuario.getSenha());
            ps.setString(4, usuario.getPerfil()); 
            ps.setString(5, "Ativo"); // Todo novo usuário nasce como Ativo
            
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar utilizador: " + e.getMessage(), e);
        }
    }
    
    // ─── ATUALIZAR (UPDATE) ───
    public void atualizar(Usuario usuario){
        String sql = "UPDATE usuarios SET nome = ?, email = ?, senha = ?, perfil = ?, status = ? WHERE id = ?";
        
        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)){
            
            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getEmail());
            ps.setString(3, usuario.getSenha());
            ps.setString(4, usuario.getPerfil());
            ps.setString(5, usuario.getStatus()); 
            ps.setInt(6, usuario.getId());
            
            ps.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar usuário no banco de dados: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Erro inesperado na conexão com banco: " + e.getMessage(), e);
        }
    }
    
    // ─── DELETAR (DELETE) ───
    public void deletar(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (Connection conn = ConexaoBanco.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setInt(1, id);
            ps.executeUpdate();
            
        } catch (Exception e) {
            // Tratamento amigável para chave estrangeira (Foreign Key)
            throw new RuntimeException("Não é possível deletar este usuário porque ele possui operações financeiras no sistema. Edite e mude o status para 'Inativo'.", e);
        }
    }

    // ─── CONSTRUIR USUARIO (Helper) ───
    private Usuario constroirUsuario(ResultSet rs) throws SQLException {
        Usuario usu = new Usuario();
        usu.setId(rs.getInt("id"));
        usu.setNome(rs.getString("nome"));
        usu.setEmail(rs.getString("email"));
        usu.setSenha(rs.getString("senha"));
        usu.setPerfil(rs.getString("perfil"));
        usu.setStatus(rs.getString("status"));
        return usu;
    }
}
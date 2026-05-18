package com.i08.db;

import com.i08.util.Messages;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

 // Encapsula todas as consultas relacionadas a usuários.
public class UserDAO {
    private final Connection connection;

    public UserDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

     // Returna true se as credenciais forem válidas, false caso contrário
    public boolean validateLogin(String email, String senha) {
        String query = "SELECT * FROM usuarios WHERE email = ? AND senha = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            statement.setString(2, senha);
            
            ResultSet result = statement.executeQuery();
            return result.next(); // Retorna true se encontrou um usuário
            
        } catch (SQLException e) {
            System.err.println(Messages.ERROR_DB_LOGIN_VALIDATION + e.getMessage());
            return false;
        }
    }


    // Verificase o usuário existe no banco
    public boolean userExists(String email) {
        String query = "SELECT * FROM usuarios WHERE email = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            
            ResultSet result = statement.executeQuery();
            return result.next();
            
        } catch (SQLException e) {
            System.err.println(Messages.ERROR_DB_USER_EXISTS + e.getMessage());
            return false;
        }
    }
}

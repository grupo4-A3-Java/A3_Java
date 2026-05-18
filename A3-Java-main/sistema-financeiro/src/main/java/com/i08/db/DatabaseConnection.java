package com.i08.db;

import com.i08.util.Messages;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Classe responsável por gerenciar a conexão com o banco de dados.
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;

    // Configurações de conexão do banco (ALTERE CONFORME SEU BANCO)
    // jdbc:mysql://localhost:3306//sistema_financeiro_db?createDatabaseIfNotExist=true opcional, cria o banco se não existir
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/sistema_financeiro_db?createDatabaseIfNotExist=true";
    private static final String USER = "root";
    private static final String PASSWORD = "my-secret-pw";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    private DatabaseConnection() {
        connect();
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    // Estabelece a conexão com o banco de dados.
    private void connect() {
        try {
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println(Messages.SUCCESS_DB_CONNECTION);
        } catch (ClassNotFoundException e) {
            System.err.println(Messages.ERROR_DB_DRIVER_NOT_FOUND + DRIVER);
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println(Messages.ERROR_DB_CONNECTION + e.getMessage());
            e.printStackTrace();
        }
    }

    // Retorna a conexão ativa.
    public Connection getConnection() {
        if (connection == null) {
            connect();
        }
        return connection;
    }

    // Verifica se a conexão está ativa.
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    // Fecha a conexão com o banco.
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
        }
    }
}

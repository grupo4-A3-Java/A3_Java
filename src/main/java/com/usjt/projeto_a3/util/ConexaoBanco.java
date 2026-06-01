package com.usjt.projeto_a3.util;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class ConexaoBanco {
    private static String URL;
    private static String USUARIO;
    private static String SENHA;

    static {
        try (FileInputStream fis = new FileInputStream("db.properties")) {
            Properties props = new Properties();
            props.load(fis);
            URL = props.getProperty("db.url");
            USUARIO = props.getProperty("db.user");
            SENHA = props.getProperty("db.password");
        } catch (Exception e) {
            throw new RuntimeException(
                "Nao consegui ler o db.properties na raiz do projeto. " +
                "Copie o db.properties.example para db.properties e preencha seus dados.", e);
        }
    }

    public static Connection getConexao() throws Exception {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}

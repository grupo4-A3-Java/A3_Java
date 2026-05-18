package com.i08.util;

/**
 * Centraliza todas as mensagens de erro, sucesso e informação da aplicação.
 */
public class Messages {

    // ========================================
    // MENSAGENS DE INICIALIZAÇÃO
    // ========================================
    public static final String APP_INIT_START = "[ApplicationContext] Iniciando aplicação...";
    public static final String APP_CONNECTING_DB = "[ApplicationContext] Conectando ao banco de dados...";
    public static final String APP_DB_CONNECTION_SUCCESS = "[ApplicationContext] Conexão com banco estabelecida com sucesso!";
    public static final String APP_INIT_SUCCESS = "[ApplicationContext] Aplicação inicializada com sucesso!";

    // ========================================
    // MENSAGENS DE ERRO - CONEXÃO
    // ========================================
    public static final String ERROR_DB_CONNECTION_FAILED = "[ApplicationContext] ERRO: Falha na conexão com o banco!";
    public static final String ERROR_DB_CONNECTION_DIALOG_TITLE = "Erro de Conexão";
    public static final String ERROR_DB_CONNECTION_DIALOG_MESSAGE =
        "Erro: Não foi possível conectar ao banco de dados.\n" +
        "Verifique se o MySQL está rodando e as configurações estão corretas.";

    // ========================================
    // MENSAGENS DE ERRO - INICIALIZAÇÃO
    // ========================================
    public static final String ERROR_APP_INIT = "[ApplicationContext] ERRO ao inicializar a aplicação: ";
    public static final String ERROR_APP_INIT_DIALOG_TITLE = "Erro de Inicialização";
    public static final String ERROR_APP_INIT_DIALOG_MESSAGE = "Erro crítico na inicialização: ";

    // ========================================
    // MENSAGENS DE ERRO - DATABASE
    // ========================================
    public static final String ERROR_DB_DRIVER_NOT_FOUND = "Driver não encontrado: ";
    public static final String ERROR_DB_CONNECTION = "Erro ao conectar ao banco: ";
    public static final String ERROR_DB_INIT = "Erro ao inicializar banco de dados: ";
    public static final String ERROR_SCRIPT_SQL_NOT_FOUND = "Arquivo script.sql não encontrado no classpath";
    public static final String ERROR_DB_LOGIN_VALIDATION = "Erro ao validar login: ";
    public static final String ERROR_DB_USER_EXISTS = "Erro ao verificar usuário: ";

    // ========================================
    // MENSAGENS DE ERRO - LOGIN
    // ========================================
    public static final String ERROR_LOGIN_EMPTY_FIELDS = "Por favor, preencha todos os campos!";
    public static final String ERROR_LOGIN_EMPTY_FIELDS_TITLE = "Campos vazios";
    public static final String ERROR_LOGIN_INVALID_CREDENTIALS = "Usuário ou senha inválidos!";
    public static final String ERROR_LOGIN_INVALID_CREDENTIALS_TITLE = "Erro no Login";

    // ========================================
    // MENSAGENS DE SUCESSO - LOGIN
    // ========================================
    public static final String SUCCESS_LOGIN_TITLE = "Sucesso";
    public static final String SUCCESS_LOGIN_MESSAGE = "Login realizado com sucesso! Bem-vindo, ";

    // ========================================
    // MENSAGENS DE SUCESSO - DATABASE
    // ========================================
    public static final String SUCCESS_DB_CONNECTION = "Conexão com banco de dados estabelecida!";
    public static final String SUCCESS_DB_INIT = "Banco de dados inicializado com sucesso!";
}

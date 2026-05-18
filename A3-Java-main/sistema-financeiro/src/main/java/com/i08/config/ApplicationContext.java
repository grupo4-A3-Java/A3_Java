package com.i08.config;

import com.i08.db.DatabaseConnection;
import com.i08.db.UserDAO;
import com.i08.util.Messages;
import com.i08.view.LoginFrame;
import javax.swing.JOptionPane;

/**
 * Gerencia a inicialização e o ciclo de vida de todos os objetos/classes da aplicação.
 */
public class ApplicationContext {
    private static ApplicationContext instance;
    private DatabaseConnection dbConnection;
    private UserDAO userDAO;
    private LoginFrame loginView;

    public static ApplicationContext getInstance() {
        if (instance == null) {
            instance = new ApplicationContext();
        }
        return instance;
    }

    /**
     * Inicializa a aplicação com o seguinte fluxo:
     * 1. Conecta ao banco de dados
     * 2. Verifica se a conexão foi bem-sucedida
     * 3. Se OK: Carrega os DAOs(Classes que interagem com o banco de dados) e exibe a tela de login
     * 4. Se FALHA: Exibe erro e encerra a aplicação
     */
    public boolean init() {
        try {
            System.out.println(Messages.APP_INIT_START);
            
            // Obtém conexão com o banco de dados
            System.out.println(Messages.APP_CONNECTING_DB);
            dbConnection = DatabaseConnection.getInstance();

            // Verifica se a conexão foi estabelecida com sucesso
            if (!dbConnection.isConnected()) {
                System.err.println(Messages.ERROR_DB_CONNECTION_FAILED);
                JOptionPane.showMessageDialog(null,
                    Messages.ERROR_DB_CONNECTION_DIALOG_MESSAGE,
                    Messages.ERROR_DB_CONNECTION_DIALOG_TITLE,
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
                return false;
            }
            
            System.out.println(Messages.APP_DB_CONNECTION_SUCCESS);

            // Inicializa os DAOs
            userDAO = new UserDAO();
            
            // Inicializa interface de login
            loginView = new LoginFrame(true);
            
            System.out.println(Messages.APP_INIT_SUCCESS);
            return true;
            
        } catch (Exception e) {
            System.err.println(Messages.ERROR_APP_INIT + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                Messages.ERROR_APP_INIT_DIALOG_MESSAGE + e.getMessage(),
                Messages.ERROR_APP_INIT_DIALOG_TITLE,
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
            return false;
        }
    }

    public DatabaseConnection getDatabaseConnection() {
        return dbConnection;
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public LoginFrame getLoginView() {
        return loginView;
    }
}
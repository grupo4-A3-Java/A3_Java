# 📚 Sistema Financeiro

## 🎯 Visão Geral

O **Sistema Financeiro** é uma aplicação desktop desenvolvida em **Java Swing** que permite gerenciar operações financeiras (receitas e despesas) com autenticação de usuários e persistência de dados em banco MySQL.

**O que a aplicação faz:**
- 🔐 Login seguro de usuários
- 💰 Gerenciamento de ativos (receitas e despesas)
- 📊 Controle de operações financeiras
- 👥 Sistema de usuários com níveis de permissão (ADMIN e USER)

---

## 📋 Requisitos

Antes de executar o projeto, certifique-se de ter instalado:

| Requisito | Versão Mínima | Como Verificar |
|-----------|---------------|----------------|
| **Java**  | 11+  | `java -version`   |
| **Maven** | 3.6+ | `mvn -version`    |
| **MySQL** | 8.0+ | `mysql --version` |

### Instalando os Requisitos

**Linux (Ubuntu/Debian):**
```bash
# Java
sudo apt-get install openjdk-11-jdk

# Maven
sudo apt-get install maven

# MySQL
sudo apt-get install mysql-server
sudo systemctl start mysql
```

**macOS (com Homebrew):**
```bash
# Java
brew install openjdk@11
brew install maven
brew install mysql
brew services start mysql
```

**Windows:**
- Baixar Java: https://www.oracle.com/java/technologies/
- Baixar Maven: https://maven.apache.org/download.cgi
- Baixar MySQL: https://dev.mysql.com/downloads/mysql/

---

## Como Executar

### Preparar o Banco de Dados

Primeiro, inicie o MySQL e configure o banco:

```bash
# Conectar ao MySQL
mysql -u root -p

# Executar os scripts na pasta de recursos para criar o banco e as tabelas:
source ~/A3-Java/sistema-financeiro/src/main/resources/script.sql;

# Sair do MySQL
exit
```

**Alternativa:** Se preferir via bash diretamente:
```bash
mysql -u root -p < ~/A3-Java/sistema-financeiro/src/main/resources/script.sql
```

### 2️⃣ Configurar Credenciais do Banco

Edite o arquivo de conexão:
```
src/main/java/com/i08/db/DatabaseConnection.java
```

Ajuste as credenciais conforme seu MySQL:
```java
private static final String URL = "jdbc:mysql://localhost:3306/sistema_financeiro_db?createDatabaseIfNotExist=true";
private static final String USER = "root";           // ← Seu usuário MySQL
private static final String PASSWORD = "sua-senha"; // ← Sua senha MySQL
```

### 3️⃣ Compilar e Executar

```bash
# Navegar até a pasta do projeto
cd ~/A3-Java/sistema-financeiro

# Compilar
mvn clean compile

# Executar
mvn exec:java -Dexec.mainClass="com.i08.Main"
```

**Alternativamente, criar um JAR executável:**
```bash
# Gerar JAR
mvn clean package

# Executar
java -jar target/sistema-financeiro-1.0-SNAPSHOT-jar-with-dependencies.jar
```

---

## 📁 Estrutura do Projeto

```
sistema-financeiro/
├── src/
│   ├── main/
│   │   ├── java/com/i08/
│   │   │   ├── Main.java                    ← Ponto de entrada
│   │   │   ├── config/
│   │   │   │   └── ApplicationContext.java  ← Configuração centralizada
│   │   │   ├── controller/
│   │   │   │   └── LoginController.java     ← Lógica de eventos de tela
│   │   │   ├── db/
│   │   │   │   ├── DatabaseConnection.java  ← Conexão com banco
│   │   │   │   └── UserDAO.java             ← Operações de usuários
│   │   │   ├── view/
│   │   │   │   └── LoginFrame.java          ← Interface de login
│   │   │   ├── styles/
│   │   │   │   └── LoginStyles.java         ← Estilo dos componentes
│   │   │   └── util/
│   │   │       ├── Messages.java            ← Mensagens de erro
│   │   │       └── Util.java                ← Funções auxiliares
│   │   └── resources/
│   │       └── script.sql                   ← Script de criação do banco
│   │       └── seeds.sql                    ← Script com dados a serem inseridos no banco
│   └── test/                                ← Testes
├── pom.xml                                  ← Configuração Maven
└── README.md
```

### O que cada camada faz:

| Camada | Responsabilidade | Exemplo |
|--------|-----------------|---------|
| **View** | Interface visual (Swing) | `LoginFrame.java` - tela de login |
| **Controller** | Lógica de eventos e validações | `LoginController.java` - processar clique do botão/enter do teclado |
| **DB** | Acesso aos dados (DAO Pattern) | `UserDAO.java` - operações no banco relacionadas a usuários |
| **Config** | Inicialização e configurações | `ApplicationContext.java` - criar componentes |
| **Util** | Funções auxiliares reutilizáveis | `Messages.java` - mensagens padrão de erro/sucesso do sistema |

---

## 💡 Entendendo o Fluxo da Aplicação

```
1. Main.java inicia
   ↓
2. ApplicationContext.getInstance() carrega configurações
   ↓
3. LoginFrame aparece (interface visual)
   ↓
4. Usuário digita email e senha
   ↓
5. LoginController.handleLogin() processa o evento
   ↓
6. UserDAO.validateLogin() consulta o banco
   ↓
7. Se válido → próxima tela (TODO)
   Se inválido → limpa campos e exibe erro
```

---

## Exemplo: Como Implementar uma Nova Funcionalidade

Suponha que você queira adicionar um **CRUD de Ativos** (receitas e despesas). Aqui está o passo a passo:

### Passo 1: Criar a View (Interface)
```java
// src/main/java/com/i08/view/AtivosFrame.java
package com.i08.view;

import javax.swing.*;

public class AtivosFrame extends JFrame {
    private JTextField nomeField;
    private JComboBox<String> tipoCombo;
    private JButton salvarButton;

    public AtivosFrame() {
        setTitle("Gerenciar Ativos");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Criar componentes
        nomeField = new JTextField(20);
        tipoCombo = new JComboBox<>(new String[]{"RECEITA", "DESPESA"});
        salvarButton = new JButton("Salvar");
        
        // Adicionar ao painel...
    }
}
```

### Passo 2: Criar o Controller (Lógica)
```java
// src/main/java/com/i08/controller/AtivosController.java
package com.i08.controller;

import com.i08.db.AtivosDAO;

public class AtivosController {
    private AtivosDAO ativosDAO;

    public AtivosController() {
        this.ativosDAO = new AtivosDAO();
    }

    public void salvarAtivo(String nome, String tipo) {
        // Validar entrada
        if (nome.isEmpty()) {
            System.out.println("Nome não pode estar vazio!");
            return;
        }
        
        // Salvar no banco
        if (ativosDAO.criar(nome, tipo)) {
            System.out.println("Ativo salvo com sucesso!");
        }
    }
}
```

### Passo 3: Criar o DAO (Acesso ao Banco)
```java
// src/main/java/com/i08/db/AtivosDAO.java
package com.i08.db;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class AtivosDAO {
    private Connection connection;

    public AtivosDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public boolean criar(String nome, String tipo) {
        String query = "INSERT INTO ativos (nome, tipo) VALUES (?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, nome);
            stmt.setString(2, tipo);
            stmt.executeUpdate();
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao criar ativo: " + e.getMessage());
            return false;
        }
    }
}
```

### Passo 4: Integrar no ApplicationContext
```java
// Adicionar em ApplicationContext.java
private AtivosFrame ativosFrame;

public void inicializarTelas() {
    loginFrame = new LoginFrame(this);
    ativosFrame = new AtivosFrame(); // Nova tela
    loginFrame.setVisible(true);
}
```

### Resultado
Seguindo esse padrão (View → Controller → DAO), a nova funcionalidade fica:
- ✔️ Organizada e fácil de manter
- ✔️ Testável
- ✔️ Consistente com o resto do projeto

---

## 🐛 Dicas Úteis

### Adicionar Mensagens de Erro
Use a classe `Messages.java` para mensagens padrão:
```java
// src/main/java/com/i08/util/Messages.java
public class Messages {
    public static final String ERROR_DB_LOGIN_VALIDATION = "Erro ao validar login: ";
    // Adicionar novas mensagens conforme necessário
}
```

### Logs no Console
A aplicação exibe logs no console durante a execução:
```bash
========================================
SISTEMA FINANCEIRO - INICIANDO
========================================
[Main] Aplicação iniciada e pronta para uso.
```

### Problemas Comuns

| Problema | Solução |
|----------|---------|
| `ClassNotFoundException` | Verifique se MySQL está instalado e running |
| `Connection refused` | Edite `DatabaseConnection.java` com as credenciais corretas |
| `Access denied for user 'root'` | Ajuste `DATABASE CONNECTION.java` com a senha correta |
| Erro ao compilar | Execute `mvn clean install` para baixar dependências |

---

## 📚 Próximas Etapas Sugeridas

1. **Dashboard** - Tela principal após login com resumo financeiro
2. **Listar Operações** - Visualizar operações registradas
3. **Editar/Deletar** - Operações CRUD completas
4. **Relatórios** - Gráficos e relatórios de receita/despesa
5. **Testes Unitários** - Adicionar testes em `src/test/java/`

---

## Dúvidas?

Se tiver dúvidas durante a implementação:
- 📖 Consulte os comentários no código
- 🔍 Procure por padrões semelhantes já implementados
- 🧪 Teste pequenas mudanças uma de cada vez

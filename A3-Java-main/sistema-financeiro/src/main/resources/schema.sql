CREATE DATABASE IF NOT EXISTS sistema_financeiro_db;
USE sistema_financeiro_db;

-- =========================================
-- TABELA: USUARIOS
-- =========================================
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    tipo ENUM('ADMIN', 'USER') NOT NULL,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================================
-- TABELA: ATIVOS
-- =========================================
CREATE TABLE ativos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    tipo ENUM('RECEITA', 'DESPESA') NOT NULL
);

-- =========================================
-- TABELA: OPERACOES
-- =========================================
CREATE TABLE operacoes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    ativo_id INT NOT NULL,
    tipo ENUM('COMPRA', 'VENDA') NOT NULL,
    quantidade DECIMAL(10,2) NOT NULL,
    preco_unitario DECIMAL(10,2) NOT NULL,
    data_operacao DATE NOT NULL,

    CONSTRAINT fk_operacoes_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id),

    CONSTRAINT fk_operacoes_ativo
        FOREIGN KEY (ativo_id)
        REFERENCES ativos(id)
);

-- =========================================
-- TABELA: CARTEIRA
-- =========================================
CREATE TABLE carteira (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    ativo_id INT NOT NULL,
    quantidade_total DECIMAL(10,2) NOT NULL,
    preco_medio DECIMAL(10,2) NOT NULL,

    CONSTRAINT fk_carteira_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id),

    CONSTRAINT fk_carteira_ativo
        FOREIGN KEY (ativo_id)
        REFERENCES ativos(id)
);
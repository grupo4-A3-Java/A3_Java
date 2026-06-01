CREATE DATABASE IF NOT EXISTS investsys;
USE investsys;

CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha VARCHAR(100) NOT NULL,
    perfil VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'Ativo'
);

CREATE TABLE IF NOT EXISTS ativos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ticker VARCHAR(20) NOT NULL UNIQUE,
    nome VARCHAR(150) NOT NULL,
    tipo VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS operacoes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    ativo_id INT NOT NULL,
    tipo VARCHAR(10) NOT NULL,
    quantidade DOUBLE NOT NULL,
    preco_unitario DOUBLE NOT NULL,
    data_operacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (ativo_id)   REFERENCES ativos(id)
);

CREATE TABLE IF NOT EXISTS carteira (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    ativo_id INT NOT NULL,
    quantidade_total DOUBLE NOT NULL DEFAULT 0,
    preco_medio DOUBLE NOT NULL DEFAULT 0,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (ativo_id)   REFERENCES ativos(id),
    UNIQUE KEY uq_carteira (usuario_id, ativo_id)
);

-- Usuario admin pra conseguir entrar no sistema
INSERT INTO usuarios (nome, email, senha, perfil, status) VALUES
('Administrador', 'admin@investsys.com', 'admin123', 'Admin', 'Ativo');

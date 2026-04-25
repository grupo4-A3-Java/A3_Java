DROP DATABASE IF EXISTS investimentos;
CREATE DATABASE investimentos;
USE investimentos;

CREATE TABLE IF NOT EXISTS usuarios (
	id int auto_increment primary key,
    nome varchar(100) NOT NULL,
    email varchar(100) unique NOT NULL,
    senha varchar(255) NOT NULL,
    tipo ENUM('ADMIN', 'USER') NOT NULL, 
    data_criacao timestamp default current_timestamp
);

CREATE TABLE IF NOT EXISTS ativos (
	id int auto_increment primary key,
    nome varchar(50) NOT NULL,
    tipo ENUM('ACAO', 'FII', 'CRIPTO', 'OUTRO') NOT NULL
);

CREATE TABLE IF NOT EXISTS operacoes (
	id int auto_increment primary key,
    usuario_id int NOT NULL,
    ativo_id int NOT NULL,
    tipo ENUM('COMPRA', 'VENDA') NOT NULL,
    quantidade decimal(10,2) NOT NULL,
    preco_unitario decimal(10,2) NOT NULL,
    valor_total DECIMAL(12,2) GENERATED ALWAYS AS (quantidade * preco_unitario) STORED,
    data_operacao date NOT NULL,
    
    foreign key (usuario_id) references usuarios(id),
    foreign key (ativo_id) references ativos(id)
);

INSERT INTO usuarios (nome, email, senha, tipo) VALUES
('Erica', 'erica@admin.com', '123456', 'ADMIN'),
('Felipe', 'felipe@gmail.com', '123456', 'USER'),
('Padilha', 'padilha@gmail.com', '123456', 'USER'),
('Arthur', 'arthur@gmail.com', '123456', 'USER');

INSERT INTO ativos (nome, tipo) VALUES
('PETR4', 'ACAO'),
('VALE3', 'ACAO'),
('ITUB4', 'ACAO'),
('HGLG11', 'FII'),
('MXRF11', 'FII'),
('BTC', 'CRIPTO'),
('ETH', 'CRIPTO');

INSERT INTO operacoes (usuario_id, ativo_id, tipo, quantidade, preco_unitario, data_operacao) VALUES
(2, 1, 'COMPRA', 10, 30.00, '2026-01-10'),
(2, 2, 'COMPRA', 5, 70.00, '2026-01-12'),
(3, 4, 'COMPRA', 20, 10.00, '2026-02-01'),
(3, 6, 'COMPRA', 1, 200000.00, '2026-02-05'),
(4, 3, 'COMPRA', 15, 25.00, '2026-03-01'),
(4, 7, 'COMPRA', 2, 15000.00, '2026-03-03'),
(2, 1, 'VENDA', 5, 32.00, '2026-03-10');
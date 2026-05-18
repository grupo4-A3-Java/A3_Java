USE sistema_financeiro_db;

-- =========================================
-- SEEDS: USUARIOS
-- =========================================
INSERT INTO usuarios (nome, email, senha, tipo) VALUES
('Administrador', 'admin@sistema.com', '123456', 'ADMIN'),
('João Silva', 'joao@sistema.com', '123456', 'USER'),
('Maria Oliveira', 'maria@sistema.com', '123456', 'USER'),
('Carlos Souza', 'carlos@sistema.com', '123456', 'USER');

-- =========================================
-- SEEDS: ATIVOS
-- =========================================
INSERT INTO ativos (nome, tipo) VALUES
('Salário', 'RECEITA'),
('Freelance', 'RECEITA'),
('Dividendos', 'RECEITA'),
('Aluguel', 'DESPESA'),
('Internet', 'DESPESA'),
('Energia', 'DESPESA'),
('Supermercado', 'DESPESA'),
('Transporte', 'DESPESA');

-- =========================================
-- SEEDS: OPERACOES
-- =========================================
INSERT INTO operacoes (
    usuario_id,
    ativo_id,
    tipo,
    quantidade,
    preco_unitario,
    data_operacao
) VALUES
(2, 1, 'COMPRA', 1.00, 5000.00, '2026-05-01'),
(2, 4, 'COMPRA', 1.00, 1500.00, '2026-05-02'),
(2, 7, 'COMPRA', 1.00, 850.00, '2026-05-03'),

(3, 2, 'COMPRA', 1.00, 2200.00, '2026-05-01'),
(3, 5, 'COMPRA', 1.00, 120.00, '2026-05-02'),
(3, 6, 'COMPRA', 1.00, 300.00, '2026-05-03'),

(4, 1, 'COMPRA', 1.00, 7200.00, '2026-05-01'),
(4, 3, 'COMPRA', 1.00, 450.00, '2026-05-04'),
(4, 8, 'COMPRA', 1.00, 600.00, '2026-05-05');

-- =========================================
-- SEEDS: CARTEIRA
-- =========================================
INSERT INTO carteira (
    usuario_id,
    ativo_id,
    quantidade_total,
    preco_medio
) VALUES
(2, 1, 1.00, 5000.00),
(2, 4, 1.00, 1500.00),
(2, 7, 1.00, 850.00),

(3, 2, 1.00, 2200.00),
(3, 5, 1.00, 120.00),
(3, 6, 1.00, 300.00),

(4, 1, 1.00, 7200.00),
(4, 3, 1.00, 450.00),
(4, 8, 1.00, 600.00);
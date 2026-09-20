CREATE TABLE contas (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('CONTA_CORRENTE', 'POUPANCA', 'CARTEIRA', 'CARTAO_CREDITO', 'OUTRA')),
    saldo_inicial NUMERIC(19, 2) NOT NULL DEFAULT 0,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_criacao TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP NOT NULL
);

ALTER TABLE lancamentos
    ADD COLUMN conta_id BIGINT REFERENCES contas(id);

CREATE INDEX idx_lancamentos_conta ON lancamentos (conta_id);

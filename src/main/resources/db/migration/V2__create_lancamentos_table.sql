CREATE TABLE lancamentos (
    id BIGSERIAL PRIMARY KEY,
    descricao VARCHAR(120) NOT NULL,
    valor NUMERIC(19, 2) NOT NULL,
    tipo VARCHAR(10) NOT NULL CHECK (tipo IN ('RECEITA', 'DESPESA')),
    data DATE NOT NULL,
    categoria VARCHAR(60) NOT NULL,
    observacao VARCHAR(500),
    data_criacao TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP NOT NULL
);

CREATE INDEX idx_lancamentos_data ON lancamentos (data);
CREATE INDEX idx_lancamentos_tipo ON lancamentos (tipo);

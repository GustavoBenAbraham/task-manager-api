CREATE TABLE categorias (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(60) NOT NULL UNIQUE,
    ativa BOOLEAN NOT NULL DEFAULT TRUE,
    data_criacao TIMESTAMP NOT NULL
);

ALTER TABLE lancamentos
    ADD COLUMN categoria_id BIGINT REFERENCES categorias(id);

CREATE INDEX idx_lancamentos_categoria ON lancamentos (categoria_id);

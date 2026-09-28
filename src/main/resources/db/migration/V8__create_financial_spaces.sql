CREATE TABLE espacos_financeiros (
    id BIGSERIAL PRIMARY KEY,
    proprietario_id BIGINT NOT NULL REFERENCES usuarios(id),
    nome VARCHAR(100) NOT NULL,
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('PESSOAL', 'NEGOCIO')),
    cnpj VARCHAR(14),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_espaco_proprietario_tipo UNIQUE (proprietario_id, tipo)
);

CREATE TABLE acessos_espacos (
    id BIGSERIAL PRIMARY KEY,
    espaco_id BIGINT NOT NULL REFERENCES espacos_financeiros(id),
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id),
    papel VARCHAR(30) NOT NULL CHECK (papel IN ('PROPRIETARIO', 'GESTORA_FINANCEIRA')),
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_acesso_espaco_usuario UNIQUE (espaco_id, usuario_id)
);

CREATE INDEX idx_acessos_espacos_usuario ON acessos_espacos (usuario_id, ativo);
CREATE INDEX idx_acessos_espacos_espaco ON acessos_espacos (espaco_id, ativo);

INSERT INTO espacos_financeiros (proprietario_id, nome, tipo)
SELECT id, 'Pessoal', 'PESSOAL'
FROM usuarios;

INSERT INTO acessos_espacos (espaco_id, usuario_id, papel)
SELECT e.id, e.proprietario_id, 'PROPRIETARIO'
FROM espacos_financeiros e;

ALTER TABLE contas ADD COLUMN espaco_id BIGINT REFERENCES espacos_financeiros(id);
ALTER TABLE categorias ADD COLUMN espaco_id BIGINT REFERENCES espacos_financeiros(id);
ALTER TABLE lancamentos ADD COLUMN espaco_id BIGINT REFERENCES espacos_financeiros(id);
ALTER TABLE contas ADD COLUMN atualizado_por_usuario_id BIGINT REFERENCES usuarios(id);
ALTER TABLE categorias ADD COLUMN atualizado_por_usuario_id BIGINT REFERENCES usuarios(id);
ALTER TABLE lancamentos ADD COLUMN atualizado_por_usuario_id BIGINT REFERENCES usuarios(id);

UPDATE contas SET atualizado_por_usuario_id = usuario_id;
UPDATE categorias SET atualizado_por_usuario_id = usuario_id;
UPDATE lancamentos SET atualizado_por_usuario_id = usuario_id;

UPDATE contas c
SET espaco_id = e.id
FROM espacos_financeiros e
WHERE e.proprietario_id = c.usuario_id AND e.tipo = 'PESSOAL';

UPDATE categorias c
SET espaco_id = e.id
FROM espacos_financeiros e
WHERE e.proprietario_id = c.usuario_id AND e.tipo = 'PESSOAL';

UPDATE lancamentos l
SET espaco_id = e.id
FROM espacos_financeiros e
WHERE e.proprietario_id = l.usuario_id AND e.tipo = 'PESSOAL';

CREATE INDEX idx_contas_espaco ON contas (espaco_id);
CREATE INDEX idx_categorias_espaco ON categorias (espaco_id);
CREATE INDEX idx_lancamentos_espaco ON lancamentos (espaco_id, data DESC);

ALTER TABLE contas DROP CONSTRAINT IF EXISTS contas_nome_key;
ALTER TABLE contas ADD CONSTRAINT uk_contas_espaco_nome UNIQUE (espaco_id, nome);

ALTER TABLE categorias DROP CONSTRAINT IF EXISTS categorias_nome_key;
ALTER TABLE categorias ADD CONSTRAINT uk_categorias_espaco_nome UNIQUE (espaco_id, nome);

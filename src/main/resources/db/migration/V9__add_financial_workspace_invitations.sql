CREATE TABLE convites_espacos (
    id BIGSERIAL PRIMARY KEY,
    espaco_id BIGINT NOT NULL REFERENCES espacos_financeiros(id),
    email_convidado VARCHAR(180) NOT NULL,
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    papel VARCHAR(30) NOT NULL CHECK (papel = 'GESTORA_FINANCEIRA'),
    situacao VARCHAR(20) NOT NULL DEFAULT 'PENDENTE'
        CHECK (situacao IN ('PENDENTE', 'ACEITO', 'REVOGADO', 'EXPIRADO')),
    convidado_por_usuario_id BIGINT NOT NULL REFERENCES usuarios(id),
    aceito_por_usuario_id BIGINT REFERENCES usuarios(id),
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_expiracao TIMESTAMP NOT NULL,
    data_aceite TIMESTAMP
);

CREATE INDEX idx_convites_espaco_situacao ON convites_espacos (espaco_id, situacao);
CREATE INDEX idx_convites_email_situacao ON convites_espacos (email_convidado, situacao);

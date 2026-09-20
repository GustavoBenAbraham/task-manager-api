ALTER TABLE contas
    ADD COLUMN usuario_id BIGINT REFERENCES usuarios(id);

ALTER TABLE categorias
    ADD COLUMN usuario_id BIGINT REFERENCES usuarios(id);

ALTER TABLE lancamentos
    ADD COLUMN usuario_id BIGINT REFERENCES usuarios(id);

CREATE INDEX idx_contas_usuario ON contas (usuario_id);
CREATE INDEX idx_categorias_usuario ON categorias (usuario_id);
CREATE INDEX idx_lancamentos_usuario ON lancamentos (usuario_id);

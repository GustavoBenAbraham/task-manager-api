CREATE TABLE titulos_financeiros (
    id BIGSERIAL PRIMARY KEY,
    espaco_id BIGINT NOT NULL REFERENCES espacos_financeiros(id),
    usuario_id BIGINT REFERENCES usuarios(id),
    atualizado_por_usuario_id BIGINT REFERENCES usuarios(id),
    descricao VARCHAR(120) NOT NULL,
    valor NUMERIC(19, 2) NOT NULL CHECK (valor > 0),
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('A_PAGAR', 'A_RECEBER')),
    data_vencimento DATE NOT NULL,
    categoria VARCHAR(60) NOT NULL,
    categoria_id BIGINT REFERENCES categorias(id),
    observacao VARCHAR(500),
    situacao VARCHAR(20) NOT NULL DEFAULT 'PENDENTE'
        CHECK (situacao IN ('PENDENTE', 'PAGO', 'RECEBIDO', 'CANCELADO')),
    data_liquidacao DATE,
    conta_liquidacao_id BIGINT REFERENCES contas(id),
    lancamento_gerado_id BIGINT UNIQUE REFERENCES lancamentos(id),
    data_criacao TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP NOT NULL,
    CHECK (
        (situacao IN ('PAGO', 'RECEBIDO') AND data_liquidacao IS NOT NULL
            AND conta_liquidacao_id IS NOT NULL AND lancamento_gerado_id IS NOT NULL)
        OR (situacao IN ('PENDENTE', 'CANCELADO') AND data_liquidacao IS NULL
            AND conta_liquidacao_id IS NULL AND lancamento_gerado_id IS NULL)
    ),
    CHECK (
        (situacao = 'PAGO' AND tipo = 'A_PAGAR')
        OR (situacao = 'RECEBIDO' AND tipo = 'A_RECEBER')
        OR situacao IN ('PENDENTE', 'CANCELADO')
    )
);

CREATE INDEX idx_titulos_espaco_vencimento
    ON titulos_financeiros (espaco_id, data_vencimento);
CREATE INDEX idx_titulos_espaco_situacao
    ON titulos_financeiros (espaco_id, situacao);

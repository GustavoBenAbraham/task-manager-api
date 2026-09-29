package com.gustavo.taskmanager.dto;

import com.gustavo.taskmanager.model.SituacaoTituloFinanceiro;
import com.gustavo.taskmanager.model.TipoTituloFinanceiro;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class TituloFinanceiroResponseDTO {
    private Long id;
    private String descricao;
    private BigDecimal valor;
    private TipoTituloFinanceiro tipo;
    private LocalDate dataVencimento;
    private String categoria;
    private Long categoriaId;
    private String observacao;
    private SituacaoTituloFinanceiro situacao;
    private LocalDate dataLiquidacao;
    private Long contaLiquidacaoId;
    private String contaLiquidacaoNome;
    private Long lancamentoGeradoId;
    private Long criadoPorUsuarioId;
    private Long atualizadoPorUsuarioId;
}

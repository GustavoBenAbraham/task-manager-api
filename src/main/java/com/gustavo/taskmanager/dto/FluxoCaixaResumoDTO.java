package com.gustavo.taskmanager.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class FluxoCaixaResumoDTO {
    private LocalDate inicio;
    private LocalDate fim;
    private BigDecimal saldoInicial;
    private BigDecimal receitasRealizadas;
    private BigDecimal despesasRealizadas;
    private BigDecimal receitasPrevistas;
    private BigDecimal despesasPrevistas;
    private BigDecimal saldoProjetado;
}

package com.gustavo.taskmanager.dto;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class DashboardResumoDTO {
    private final LocalDate inicio;
    private final LocalDate fim;
    private final BigDecimal totalReceitas;
    private final BigDecimal totalDespesas;
    private final BigDecimal saldo;

    public DashboardResumoDTO(
            LocalDate inicio,
            LocalDate fim,
            BigDecimal totalReceitas,
            BigDecimal totalDespesas) {
        this.inicio = inicio;
        this.fim = fim;
        this.totalReceitas = totalReceitas;
        this.totalDespesas = totalDespesas;
        this.saldo = totalReceitas.subtract(totalDespesas);
    }

    public DashboardResumoDTO(
            BigDecimal totalReceitas,
            BigDecimal totalDespesas) {
        this(null, null, totalReceitas, totalDespesas);
    }
}

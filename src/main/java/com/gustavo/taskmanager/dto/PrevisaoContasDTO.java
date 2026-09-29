package com.gustavo.taskmanager.dto;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class PrevisaoContasDTO {
    private final BigDecimal receitasPrevistas;
    private final BigDecimal despesasPrevistas;

    public PrevisaoContasDTO(BigDecimal receitasPrevistas, BigDecimal despesasPrevistas) {
        this.receitasPrevistas = receitasPrevistas;
        this.despesasPrevistas = despesasPrevistas;
    }
}

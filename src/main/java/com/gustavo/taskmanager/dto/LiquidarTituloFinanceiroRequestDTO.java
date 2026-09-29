package com.gustavo.taskmanager.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class LiquidarTituloFinanceiroRequestDTO {
    @NotNull
    private Long contaId;

    @NotNull
    private LocalDate dataLiquidacao;
}

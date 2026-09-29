package com.gustavo.taskmanager.dto;

import com.gustavo.taskmanager.model.TipoTituloFinanceiro;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class TituloFinanceiroRequestDTO {
    @NotBlank
    @Size(max = 120)
    private String descricao;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal valor;

    @NotNull
    private TipoTituloFinanceiro tipo;

    @NotNull
    private LocalDate dataVencimento;

    @NotBlank
    @Size(max = 60)
    private String categoria;

    private Long categoriaId;

    @Size(max = 500)
    private String observacao;
}

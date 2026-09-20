package com.gustavo.taskmanager.dto;

import com.gustavo.taskmanager.model.TipoLancamento;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LancamentoRequestDTO {

    @NotBlank(message = "A descrição é obrigatória")
    @Size(max = 120, message = "A descrição deve ter no máximo 120 caracteres")
    private String descricao;

    @NotNull(message = "O valor é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
    private BigDecimal valor;

    @NotNull(message = "O tipo é obrigatório")
    private TipoLancamento tipo;

    @NotNull(message = "A data é obrigatória")
    private LocalDate data;

    @NotBlank(message = "A categoria é obrigatória")
    @Size(max = 60, message = "A categoria deve ter no máximo 60 caracteres")
    private String categoria;

    @Size(max = 500, message = "A observação deve ter no máximo 500 caracteres")
    private String observacao;
}

package com.gustavo.taskmanager.dto;

import com.gustavo.taskmanager.model.TipoConta;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContaRequestDTO {

    @NotBlank(message = "O nome da conta é obrigatório")
    @Size(max = 100, message = "O nome da conta deve ter no máximo 100 caracteres")
    private String nome;

    @NotNull(message = "O tipo da conta é obrigatório")
    private TipoConta tipo;

    @NotNull(message = "O saldo inicial é obrigatório")
    @DecimalMin(value = "0.00", message = "O saldo inicial não pode ser negativo")
    @Builder.Default
    private BigDecimal saldoInicial = BigDecimal.ZERO;
}

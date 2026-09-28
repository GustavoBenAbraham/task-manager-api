package com.gustavo.taskmanager.dto;

import com.gustavo.taskmanager.model.TipoEspacoFinanceiro;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EspacoFinanceiroRequestDTO {

    @NotBlank(message = "O nome do espaço é obrigatório")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
    private String nome;

    @NotNull(message = "O tipo do espaço é obrigatório")
    private TipoEspacoFinanceiro tipo;

    @Size(max = 18, message = "O CNPJ deve ter no máximo 18 caracteres")
    private String cnpj;
}

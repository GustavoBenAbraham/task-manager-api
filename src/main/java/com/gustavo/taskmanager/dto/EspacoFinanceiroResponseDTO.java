package com.gustavo.taskmanager.dto;

import com.gustavo.taskmanager.model.PapelAcessoEspaco;
import com.gustavo.taskmanager.model.TipoEspacoFinanceiro;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EspacoFinanceiroResponseDTO {
    private Long id;
    private String nome;
    private TipoEspacoFinanceiro tipo;
    private String cnpj;
    private PapelAcessoEspaco papel;
}

package com.gustavo.taskmanager.dto;

import com.gustavo.taskmanager.model.PapelAcessoEspaco;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AcessoEspacoResponseDTO {
    private Long usuarioId;
    private String nome;
    private String email;
    private PapelAcessoEspaco papel;
}

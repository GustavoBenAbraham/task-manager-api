package com.gustavo.taskmanager.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ConviteEspacoResponseDTO {
    private Long id;
    private Long espacoId;
    private String nomeEspaco;
    private String emailConvidado;
    private String token;
    private LocalDateTime dataExpiracao;
}

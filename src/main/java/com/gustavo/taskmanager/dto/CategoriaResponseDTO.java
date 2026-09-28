package com.gustavo.taskmanager.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaResponseDTO {
    private Long id;
    private String nome;
    private boolean ativa;
    private Long criadoPorUsuarioId;
    private Long atualizadoPorUsuarioId;
    private LocalDateTime dataCriacao;
}

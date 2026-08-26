package com.gustavo.taskmanager.dto;

import com.gustavo.taskmanager.model.TaskStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskResponseDTO {
    private Long id;
    private String titulo;
    private String descricao;
    private TaskStatus status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
}
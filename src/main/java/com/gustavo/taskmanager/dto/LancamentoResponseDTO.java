package com.gustavo.taskmanager.dto;

import com.gustavo.taskmanager.model.TipoLancamento;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LancamentoResponseDTO {
    private Long id;
    private String descricao;
    private BigDecimal valor;
    private TipoLancamento tipo;
    private LocalDate data;
    private String categoria;
    private String observacao;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
}

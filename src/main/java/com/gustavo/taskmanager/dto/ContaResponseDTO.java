package com.gustavo.taskmanager.dto;

import com.gustavo.taskmanager.model.TipoConta;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContaResponseDTO {
    private Long id;
    private String nome;
    private TipoConta tipo;
    private BigDecimal saldoInicial;
    /** Saldo atual = saldoInicial + receitas vinculadas − despesas vinculadas */
    private BigDecimal saldoAtual;
    private boolean ativo;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
}

package com.gustavo.taskmanager.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "convites_espacos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConviteEspaco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "espaco_id", nullable = false)
    private EspacoFinanceiro espaco;

    @Column(name = "email_convidado", nullable = false, length = 180)
    private String emailConvidado;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PapelAcessoEspaco papel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private SituacaoConviteEspaco situacao = SituacaoConviteEspaco.PENDENTE;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "convidado_por_usuario_id", nullable = false)
    private Usuario convidadoPor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aceito_por_usuario_id")
    private Usuario aceitoPor;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_expiracao", nullable = false)
    private LocalDateTime dataExpiracao;

    @Column(name = "data_aceite")
    private LocalDateTime dataAceite;

    @PrePersist
    protected void onCreate() {
        if (dataCriacao == null) dataCriacao = LocalDateTime.now();
    }
}

package com.gustavo.taskmanager.repository;

import com.gustavo.taskmanager.model.EspacoFinanceiro;
import com.gustavo.taskmanager.model.TituloFinanceiro;
import com.gustavo.taskmanager.model.SituacaoTituloFinanceiro;
import com.gustavo.taskmanager.dto.PrevisaoContasDTO;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

public interface TituloFinanceiroRepository extends JpaRepository<TituloFinanceiro, Long> {
    List<TituloFinanceiro> findAllByEspacoOrderByDataVencimentoAsc(EspacoFinanceiro espaco);
    boolean existsByLancamentoGerado_Id(Long lancamentoId);

    Optional<TituloFinanceiro> findByIdAndEspaco(Long id, EspacoFinanceiro espaco);

    @Query("""
        select new com.gustavo.taskmanager.dto.PrevisaoContasDTO(
            coalesce(sum(case when t.tipo = com.gustavo.taskmanager.model.TipoTituloFinanceiro.A_RECEBER then t.valor else 0 end), 0),
            coalesce(sum(case when t.tipo = com.gustavo.taskmanager.model.TipoTituloFinanceiro.A_PAGAR then t.valor else 0 end), 0)
        )
        from TituloFinanceiro t
        where t.espaco = :espaco and t.situacao = :situacao
          and t.dataVencimento between :inicio and :fim
        """)
    PrevisaoContasDTO buscarPrevisaoPorEspaco(
        @Param("espaco") EspacoFinanceiro espaco,
        @Param("situacao") SituacaoTituloFinanceiro situacao,
        @Param("inicio") LocalDate inicio,
        @Param("fim") LocalDate fim);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from TituloFinanceiro t where t.id = :id and t.espaco = :espaco")
    Optional<TituloFinanceiro> findByIdAndEspacoForUpdate(
        @Param("id") Long id, @Param("espaco") EspacoFinanceiro espaco);
}

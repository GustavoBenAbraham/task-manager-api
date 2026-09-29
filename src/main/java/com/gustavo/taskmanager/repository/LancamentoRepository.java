package com.gustavo.taskmanager.repository;

import com.gustavo.taskmanager.model.Lancamento;
import com.gustavo.taskmanager.model.EspacoFinanceiro;
import com.gustavo.taskmanager.model.TipoLancamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import com.gustavo.taskmanager.dto.DashboardResumoDTO;

@Repository
public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {
    List<Lancamento> findAllByEspacoOrderByDataDesc(EspacoFinanceiro espaco);
    List<Lancamento> findByEspacoAndTipoOrderByDataDesc(EspacoFinanceiro espaco, TipoLancamento tipo);

    @Query("""
        select new com.gustavo.taskmanager.dto.DashboardResumoDTO(
            coalesce(sum(case when l.tipo = com.gustavo.taskmanager.model.TipoLancamento.RECEITA then l.valor else 0 end), 0),
            coalesce(sum(case when l.tipo = com.gustavo.taskmanager.model.TipoLancamento.DESPESA then l.valor else 0 end), 0)
        )
        from Lancamento l
        where l.espaco = :espaco and l.data between :inicio and :fim
        """)
    DashboardResumoDTO buscarResumoPorEspaco(
        @Param("espaco") EspacoFinanceiro espaco,
        @Param("inicio") LocalDate inicio,
        @Param("fim") LocalDate fim);

    @Query("""
        select coalesce(sum(case when l.tipo = com.gustavo.taskmanager.model.TipoLancamento.RECEITA
            then l.valor else -l.valor end), 0)
        from Lancamento l
        where l.espaco = :espaco and l.data < :inicio
        """)
    java.math.BigDecimal calcularSaldoMovimentacoesAntesDoPeriodo(
        @Param("espaco") EspacoFinanceiro espaco,
        @Param("inicio") LocalDate inicio);

    /**
     * Calcula o saldo atual de uma conta:
     * saldoInicial + receitas vinculadas - despesas vinculadas.
     */
    @Query("""
        select
            coalesce(sum(case when l.tipo = com.gustavo.taskmanager.model.TipoLancamento.RECEITA then l.valor else 0 end), 0)
          - coalesce(sum(case when l.tipo = com.gustavo.taskmanager.model.TipoLancamento.DESPESA then l.valor else 0 end), 0)
        from Lancamento l
        where l.conta.id = :contaId
          and l.espaco = l.conta.espaco
        """)
    java.math.BigDecimal calcularMovimentacaoLiquida(@Param("contaId") Long contaId);
}

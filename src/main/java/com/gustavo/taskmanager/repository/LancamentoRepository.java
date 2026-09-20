package com.gustavo.taskmanager.repository;

import com.gustavo.taskmanager.model.Lancamento;
import com.gustavo.taskmanager.model.TipoLancamento;
import com.gustavo.taskmanager.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import com.gustavo.taskmanager.dto.DashboardResumoDTO;

@Repository
public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {
    List<Lancamento> findAllByOrderByDataDesc();
    List<Lancamento> findByTipoOrderByDataDesc(TipoLancamento tipo);
    List<Lancamento> findAllByUsuarioOrderByDataDesc(Usuario usuario);
    List<Lancamento> findByUsuarioAndTipoOrderByDataDesc(Usuario usuario, TipoLancamento tipo);

    @Query("""
        select new com.gustavo.taskmanager.dto.DashboardResumoDTO(
            coalesce(sum(case when l.tipo = com.gustavo.taskmanager.model.TipoLancamento.RECEITA then l.valor else 0 end), 0),
            coalesce(sum(case when l.tipo = com.gustavo.taskmanager.model.TipoLancamento.DESPESA then l.valor else 0 end), 0)
        )
        from Lancamento l
        where l.data between :inicio and :fim
        """)
    DashboardResumoDTO buscarResumo(
        @Param("inicio") LocalDate inicio,
        @Param("fim") LocalDate fim);

    @Query("""
        select new com.gustavo.taskmanager.dto.DashboardResumoDTO(
            coalesce(sum(case when l.tipo = com.gustavo.taskmanager.model.TipoLancamento.RECEITA then l.valor else 0 end), 0),
            coalesce(sum(case when l.tipo = com.gustavo.taskmanager.model.TipoLancamento.DESPESA then l.valor else 0 end), 0)
        )
        from Lancamento l
        where l.usuario = :usuario and l.data between :inicio and :fim
        """)
    DashboardResumoDTO buscarResumoPorUsuario(
        @Param("usuario") Usuario usuario,
        @Param("inicio") LocalDate inicio,
        @Param("fim") LocalDate fim);
}

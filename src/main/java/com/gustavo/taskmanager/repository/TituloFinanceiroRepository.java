package com.gustavo.taskmanager.repository;

import com.gustavo.taskmanager.model.EspacoFinanceiro;
import com.gustavo.taskmanager.model.TituloFinanceiro;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TituloFinanceiroRepository extends JpaRepository<TituloFinanceiro, Long> {
    List<TituloFinanceiro> findAllByEspacoOrderByDataVencimentoAsc(EspacoFinanceiro espaco);
    boolean existsByLancamentoGerado_Id(Long lancamentoId);

    Optional<TituloFinanceiro> findByIdAndEspaco(Long id, EspacoFinanceiro espaco);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from TituloFinanceiro t where t.id = :id and t.espaco = :espaco")
    Optional<TituloFinanceiro> findByIdAndEspacoForUpdate(
        @Param("id") Long id, @Param("espaco") EspacoFinanceiro espaco);
}

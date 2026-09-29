package com.gustavo.taskmanager.repository;

import com.gustavo.taskmanager.model.Conta;
import com.gustavo.taskmanager.model.EspacoFinanceiro;
import com.gustavo.taskmanager.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {
    boolean existsByEspacoAndNomeIgnoreCase(EspacoFinanceiro espaco, String nome);
    List<Conta> findAllByEspacoOrderByNomeAsc(EspacoFinanceiro espaco);

    @Query("select coalesce(sum(c.saldoInicial), 0) from Conta c where c.espaco = :espaco and c.ativo = true")
    BigDecimal somarSaldosIniciaisPorEspaco(@Param("espaco") EspacoFinanceiro espaco);
}

package com.gustavo.taskmanager.repository;

import com.gustavo.taskmanager.model.EspacoFinanceiro;
import com.gustavo.taskmanager.model.TipoEspacoFinanceiro;
import com.gustavo.taskmanager.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EspacoFinanceiroRepository extends JpaRepository<EspacoFinanceiro, Long> {
    Optional<EspacoFinanceiro> findByProprietarioAndTipo(Usuario proprietario, TipoEspacoFinanceiro tipo);
    boolean existsByProprietarioAndTipo(Usuario proprietario, TipoEspacoFinanceiro tipo);
}

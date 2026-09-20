package com.gustavo.taskmanager.repository;

import com.gustavo.taskmanager.model.Lancamento;
import com.gustavo.taskmanager.model.TipoLancamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {
    List<Lancamento> findAllByOrderByDataDesc();
    List<Lancamento> findByTipoOrderByDataDesc(TipoLancamento tipo);
}

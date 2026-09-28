package com.gustavo.taskmanager.repository;

import com.gustavo.taskmanager.model.Categoria;
import com.gustavo.taskmanager.model.EspacoFinanceiro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    boolean existsByEspacoAndNomeIgnoreCase(EspacoFinanceiro espaco, String nome);
    List<Categoria> findAllByEspacoOrderByNomeAsc(EspacoFinanceiro espaco);
}

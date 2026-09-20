package com.gustavo.taskmanager.repository;

import com.gustavo.taskmanager.model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {
    boolean existsByNomeIgnoreCase(String nome);
}

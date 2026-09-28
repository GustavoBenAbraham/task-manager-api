package com.gustavo.taskmanager.repository;

import com.gustavo.taskmanager.model.AcessoEspaco;
import com.gustavo.taskmanager.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AcessoEspacoRepository extends JpaRepository<AcessoEspaco, Long> {
    @Query("select a from AcessoEspaco a join fetch a.espaco where a.usuario = :usuario and a.ativo = true order by a.dataCriacao")
    List<AcessoEspaco> listarAtivosComEspaco(@Param("usuario") Usuario usuario);

    List<AcessoEspaco> findAllByUsuarioAndAtivoTrueOrderByDataCriacaoAsc(Usuario usuario);
    Optional<AcessoEspaco> findByEspacoIdAndUsuarioAndAtivoTrue(Long espacoId, Usuario usuario);
    boolean existsByEspacoAndUsuarioAndAtivoTrue(
        com.gustavo.taskmanager.model.EspacoFinanceiro espaco, Usuario usuario);

    Optional<AcessoEspaco> findByEspacoAndUsuario(
        com.gustavo.taskmanager.model.EspacoFinanceiro espaco, Usuario usuario);

    @Query("select a from AcessoEspaco a join fetch a.usuario where a.espaco.id = :espacoId and a.ativo = true order by a.dataCriacao")
    List<AcessoEspaco> listarAtivosComUsuarios(@Param("espacoId") Long espacoId);
}

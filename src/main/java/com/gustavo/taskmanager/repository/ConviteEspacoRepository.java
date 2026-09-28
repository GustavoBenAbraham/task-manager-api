package com.gustavo.taskmanager.repository;

import com.gustavo.taskmanager.model.ConviteEspaco;
import com.gustavo.taskmanager.model.SituacaoConviteEspaco;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConviteEspacoRepository extends JpaRepository<ConviteEspaco, Long> {
    Optional<ConviteEspaco> findByTokenHash(String tokenHash);
    List<ConviteEspaco> findAllByEspacoIdAndEmailConvidadoIgnoreCaseAndSituacao(
        Long espacoId, String emailConvidado, SituacaoConviteEspaco situacao);
}

package com.gustavo.taskmanager.service.impl;

import com.gustavo.taskmanager.dto.DashboardResumoDTO;
import com.gustavo.taskmanager.repository.LancamentoRepository;
import com.gustavo.taskmanager.service.DashboardService;
import com.gustavo.taskmanager.service.UsuarioAtualService;
import com.gustavo.taskmanager.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final LancamentoRepository repository;
    private final UsuarioAtualService usuarioAtualService;

    @Override
    public DashboardResumoDTO resumo(LocalDate inicio, LocalDate fim) {
        if (inicio.isAfter(fim)) {
            throw new IllegalArgumentException("A data inicial não pode ser posterior à data final");
        }

        Usuario usuario = usuarioAtualService == null ? null : usuarioAtualService.obter();
        DashboardResumoDTO resumo = usuario == null
            ? repository.buscarResumo(inicio, fim)
            : repository.buscarResumoPorUsuario(usuario, inicio, fim);
        return new DashboardResumoDTO(
            inicio,
            fim,
            resumo.getTotalReceitas(),
            resumo.getTotalDespesas()
        );
    }
}

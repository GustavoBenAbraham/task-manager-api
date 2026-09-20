package com.gustavo.taskmanager.service.impl;

import com.gustavo.taskmanager.dto.DashboardResumoDTO;
import com.gustavo.taskmanager.repository.LancamentoRepository;
import com.gustavo.taskmanager.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final LancamentoRepository repository;

    @Override
    public DashboardResumoDTO resumo(LocalDate inicio, LocalDate fim) {
        if (inicio.isAfter(fim)) {
            throw new IllegalArgumentException("A data inicial não pode ser posterior à data final");
        }

        DashboardResumoDTO resumo = repository.buscarResumo(inicio, fim);
        return new DashboardResumoDTO(
            inicio,
            fim,
            resumo.getTotalReceitas(),
            resumo.getTotalDespesas()
        );
    }
}

package com.gustavo.taskmanager.service;

import com.gustavo.taskmanager.dto.DashboardResumoDTO;
import com.gustavo.taskmanager.dto.FluxoCaixaResumoDTO;

import java.time.LocalDate;

public interface DashboardService {
    DashboardResumoDTO resumo(LocalDate inicio, LocalDate fim);
    FluxoCaixaResumoDTO fluxoCaixa(LocalDate inicio, LocalDate fim);
}

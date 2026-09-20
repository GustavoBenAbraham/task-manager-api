package com.gustavo.taskmanager.service;

import com.gustavo.taskmanager.dto.DashboardResumoDTO;

import java.time.LocalDate;

public interface DashboardService {
    DashboardResumoDTO resumo(LocalDate inicio, LocalDate fim);
}

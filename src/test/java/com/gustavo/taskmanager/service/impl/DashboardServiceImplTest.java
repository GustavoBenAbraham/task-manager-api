package com.gustavo.taskmanager.service.impl;

import com.gustavo.taskmanager.dto.DashboardResumoDTO;
import com.gustavo.taskmanager.repository.LancamentoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    private LancamentoRepository repository;

    @InjectMocks
    private DashboardServiceImpl service;

    @Test
    void deveCalcularSaldoDoPeriodo() {
        LocalDate inicio = LocalDate.of(2026, 9, 1);
        LocalDate fim = LocalDate.of(2026, 9, 30);
        when(repository.buscarResumo(inicio, fim))
            .thenReturn(new DashboardResumoDTO(new BigDecimal("3000.00"), new BigDecimal("1250.50")));

        DashboardResumoDTO response = service.resumo(inicio, fim);

        assertEquals(inicio, response.getInicio());
        assertEquals(fim, response.getFim());
        assertEquals(new BigDecimal("3000.00"), response.getTotalReceitas());
        assertEquals(new BigDecimal("1250.50"), response.getTotalDespesas());
        assertEquals(new BigDecimal("1749.50"), response.getSaldo());
    }

    @Test
    void deveRetornarSaldoZeradoQuandoNaoHouverMovimentacao() {
        LocalDate inicio = LocalDate.of(2026, 9, 1);
        LocalDate fim = LocalDate.of(2026, 9, 30);
        when(repository.buscarResumo(inicio, fim))
            .thenReturn(new DashboardResumoDTO(BigDecimal.ZERO, BigDecimal.ZERO));

        DashboardResumoDTO response = service.resumo(inicio, fim);

        assertEquals(BigDecimal.ZERO, response.getSaldo());
    }

    @Test
    void deveRejeitarPeriodoComDatasInvertidas() {
        LocalDate inicio = LocalDate.of(2026, 10, 1);
        LocalDate fim = LocalDate.of(2026, 9, 30);

        assertThrows(IllegalArgumentException.class, () -> service.resumo(inicio, fim));
    }
}

package com.gustavo.taskmanager.service.impl;

import com.gustavo.taskmanager.dto.DashboardResumoDTO;
import com.gustavo.taskmanager.dto.FluxoCaixaResumoDTO;
import com.gustavo.taskmanager.dto.PrevisaoContasDTO;
import com.gustavo.taskmanager.repository.LancamentoRepository;
import com.gustavo.taskmanager.repository.ContaRepository;
import com.gustavo.taskmanager.repository.TituloFinanceiroRepository;
import com.gustavo.taskmanager.model.EspacoFinanceiro;
import com.gustavo.taskmanager.model.SituacaoTituloFinanceiro;
import com.gustavo.taskmanager.service.DashboardService;
import com.gustavo.taskmanager.service.EspacoAtualService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final LancamentoRepository repository;
    private final EspacoAtualService espacoAtualService;
    private final ContaRepository contaRepository;
    private final TituloFinanceiroRepository tituloRepository;

    @Override
    public DashboardResumoDTO resumo(LocalDate inicio, LocalDate fim) {
        if (inicio.isAfter(fim)) {
            throw new IllegalArgumentException("A data inicial não pode ser posterior à data final");
        }

        DashboardResumoDTO resumo = repository.buscarResumoPorEspaco(espacoAtualService.obter(), inicio, fim);
        return new DashboardResumoDTO(
            inicio,
            fim,
            resumo.getTotalReceitas(),
            resumo.getTotalDespesas()
        );
    }

    @Override
    public FluxoCaixaResumoDTO fluxoCaixa(LocalDate inicio, LocalDate fim) {
        validarPeriodo(inicio, fim);
        EspacoFinanceiro espaco = espacoAtualService.obter();
        DashboardResumoDTO realizados = repository.buscarResumoPorEspaco(espaco, inicio, fim);
        PrevisaoContasDTO previstos = tituloRepository.buscarPrevisaoPorEspaco(
            espaco, SituacaoTituloFinanceiro.PENDENTE, inicio, fim);
        java.math.BigDecimal saldoInicial = contaRepository.somarSaldosIniciaisPorEspaco(espaco)
            .add(repository.calcularSaldoMovimentacoesAntesDoPeriodo(espaco, inicio));
        java.math.BigDecimal saldoProjetado = saldoInicial
            .add(realizados.getTotalReceitas())
            .subtract(realizados.getTotalDespesas())
            .add(previstos.getReceitasPrevistas())
            .subtract(previstos.getDespesasPrevistas());

        return FluxoCaixaResumoDTO.builder()
            .inicio(inicio)
            .fim(fim)
            .saldoInicial(saldoInicial)
            .receitasRealizadas(realizados.getTotalReceitas())
            .despesasRealizadas(realizados.getTotalDespesas())
            .receitasPrevistas(previstos.getReceitasPrevistas())
            .despesasPrevistas(previstos.getDespesasPrevistas())
            .saldoProjetado(saldoProjetado)
            .build();
    }

    private void validarPeriodo(LocalDate inicio, LocalDate fim) {
        if (inicio.isAfter(fim)) {
            throw new IllegalArgumentException("A data inicial não pode ser posterior à data final");
        }
    }
}

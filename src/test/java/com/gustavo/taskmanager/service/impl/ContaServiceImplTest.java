package com.gustavo.taskmanager.service.impl;

import com.gustavo.taskmanager.dto.ContaRequestDTO;
import com.gustavo.taskmanager.dto.ContaResponseDTO;
import com.gustavo.taskmanager.exception.ContaNotFoundException;
import com.gustavo.taskmanager.model.Conta;
import com.gustavo.taskmanager.model.TipoConta;
import com.gustavo.taskmanager.repository.ContaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContaServiceImplTest {

    @Mock
    private ContaRepository repository;

    @InjectMocks
    private ContaServiceImpl service;

    @Test
    void deveCriarContaComSaldoInicial() {
        ContaRequestDTO request = ContaRequestDTO.builder()
            .nome("Carteira")
            .tipo(TipoConta.CARTEIRA)
            .saldoInicial(new BigDecimal("500.00"))
            .build();

        when(repository.save(any(Conta.class))).thenAnswer(invocation -> {
            Conta conta = invocation.getArgument(0);
            conta.setId(1L);
            return conta;
        });

        ContaResponseDTO response = service.criar(request);

        assertEquals(1L, response.getId());
        assertEquals("Carteira", response.getNome());
        assertEquals(new BigDecimal("500.00"), response.getSaldoInicial());
        assertEquals(TipoConta.CARTEIRA, response.getTipo());
    }

    @Test
    void deveDesativarContaExistente() {
        Conta conta = Conta.builder()
            .id(1L)
            .nome("Conta principal")
            .tipo(TipoConta.CONTA_CORRENTE)
            .ativo(true)
            .build();
        when(repository.findById(1L)).thenReturn(Optional.of(conta));
        when(repository.save(conta)).thenReturn(conta);

        service.desativar(1L);

        assertFalse(conta.isAtivo());
    }

    @Test
    void deveLancarExcecaoAoDesativarContaInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ContaNotFoundException.class, () -> service.desativar(99L));
    }
}

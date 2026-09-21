package com.gustavo.taskmanager.service.impl;

import com.gustavo.taskmanager.dto.LancamentoRequestDTO;
import com.gustavo.taskmanager.dto.LancamentoResponseDTO;
import com.gustavo.taskmanager.exception.LancamentoNotFoundException;
import com.gustavo.taskmanager.model.Conta;
import com.gustavo.taskmanager.model.Usuario;
import com.gustavo.taskmanager.repository.CategoriaRepository;
import com.gustavo.taskmanager.model.Lancamento;
import com.gustavo.taskmanager.model.TipoLancamento;
import com.gustavo.taskmanager.repository.ContaRepository;
import com.gustavo.taskmanager.repository.LancamentoRepository;
import com.gustavo.taskmanager.service.UsuarioAtualService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LancamentoServiceImplTest {

    @Mock
    private LancamentoRepository repository;

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private UsuarioAtualService usuarioAtualService;

    @InjectMocks
    private LancamentoServiceImpl service;

    @Test
    void deveCriarLancamentoComValorMonetario() {
        LancamentoRequestDTO request = LancamentoRequestDTO.builder()
            .descricao("Salário")
            .valor(new BigDecimal("3500.00"))
            .tipo(TipoLancamento.RECEITA)
            .data(LocalDate.of(2026, 9, 20))
            .categoria("Salário")
            .contaId(1L)
            .build();

        when(usuarioAtualService.obter()).thenReturn(Usuario.builder().id(1L).build());
        when(contaRepository.findById(1L)).thenReturn(Optional.of(Conta.builder()
            .id(1L)
            .nome("Conta principal")
            .usuario(Usuario.builder().id(1L).build())
            .build()));

        when(repository.save(any(Lancamento.class))).thenAnswer(invocation -> {
            Lancamento lancamento = invocation.getArgument(0);
            lancamento.setId(1L);
            return lancamento;
        });

        LancamentoResponseDTO response = service.criar(request);

        assertEquals(1L, response.getId());
        assertEquals(new BigDecimal("3500.00"), response.getValor());
        assertEquals(TipoLancamento.RECEITA, response.getTipo());
    }

    @Test
    void deveLancarExcecaoAoBuscarLancamentoInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(LancamentoNotFoundException.class, () -> service.buscarPorId(99L));
    }

    @Test
    void deveAtualizarTodosOsDadosDoLancamento() {
        Usuario usuario = Usuario.builder().id(1L).build();
        Lancamento lancamento = Lancamento.builder()
            .id(1L)
            .descricao("Conta antiga")
            .valor(new BigDecimal("100.00"))
            .tipo(TipoLancamento.DESPESA)
            .data(LocalDate.of(2026, 9, 1))
            .categoria("Moradia")
            .usuario(usuario)
            .build();
        LancamentoRequestDTO request = LancamentoRequestDTO.builder()
            .descricao("Conta atualizada")
            .valor(new BigDecimal("125.50"))
            .tipo(TipoLancamento.DESPESA)
            .data(LocalDate.of(2026, 9, 20))
            .categoria("Moradia")
            .contaId(1L)
            .observacao("Pagamento atualizado")
            .build();

        when(usuarioAtualService.obter()).thenReturn(usuario);
        when(repository.findById(1L)).thenReturn(Optional.of(lancamento));
        when(contaRepository.findById(1L)).thenReturn(Optional.of(Conta.builder()
            .id(1L)
            .nome("Conta principal")
            .usuario(usuario)
            .build()));
        when(repository.save(lancamento)).thenReturn(lancamento);

        LancamentoResponseDTO response = service.atualizar(1L, request);

        assertEquals("Conta atualizada", response.getDescricao());
        assertEquals(new BigDecimal("125.50"), response.getValor());
        assertEquals(LocalDate.of(2026, 9, 20), response.getData());
        assertEquals("Pagamento atualizado", response.getObservacao());
    }
}

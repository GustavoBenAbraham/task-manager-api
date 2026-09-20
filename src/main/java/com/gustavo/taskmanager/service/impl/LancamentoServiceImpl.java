package com.gustavo.taskmanager.service.impl;

import com.gustavo.taskmanager.dto.LancamentoRequestDTO;
import com.gustavo.taskmanager.dto.LancamentoResponseDTO;
import com.gustavo.taskmanager.exception.ContaNotFoundException;
import com.gustavo.taskmanager.exception.LancamentoNotFoundException;
import com.gustavo.taskmanager.model.Conta;
import com.gustavo.taskmanager.model.Lancamento;
import com.gustavo.taskmanager.model.TipoLancamento;
import com.gustavo.taskmanager.repository.ContaRepository;
import com.gustavo.taskmanager.repository.LancamentoRepository;
import com.gustavo.taskmanager.service.LancamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LancamentoServiceImpl implements LancamentoService {

    private final LancamentoRepository repository;
    private final ContaRepository contaRepository;

    @Override
    public LancamentoResponseDTO criar(LancamentoRequestDTO dto) {
        Lancamento lancamento = Lancamento.builder()
            .descricao(dto.getDescricao())
            .valor(dto.getValor())
            .tipo(dto.getTipo())
            .data(dto.getData())
            .categoria(dto.getCategoria())
            .conta(buscarConta(dto.getContaId()))
            .observacao(dto.getObservacao())
            .build();

        return toDTO(repository.save(lancamento));
    }

    @Override
    public List<LancamentoResponseDTO> listarTodos() {
        return repository.findAllByOrderByDataDesc()
            .stream()
            .map(this::toDTO)
            .toList();
    }

    @Override
    public LancamentoResponseDTO buscarPorId(Long id) {
        return toDTO(repository.findById(id)
            .orElseThrow(() -> new LancamentoNotFoundException(id)));
    }

    @Override
    public List<LancamentoResponseDTO> buscarPorTipo(TipoLancamento tipo) {
        return repository.findByTipoOrderByDataDesc(tipo)
            .stream()
            .map(this::toDTO)
            .toList();
    }

    @Override
    public LancamentoResponseDTO atualizar(Long id, LancamentoRequestDTO dto) {
        Lancamento lancamento = repository.findById(id)
            .orElseThrow(() -> new LancamentoNotFoundException(id));

        lancamento.setDescricao(dto.getDescricao());
        lancamento.setValor(dto.getValor());
        lancamento.setTipo(dto.getTipo());
        lancamento.setData(dto.getData());
        lancamento.setCategoria(dto.getCategoria());
        lancamento.setConta(buscarConta(dto.getContaId()));
        lancamento.setObservacao(dto.getObservacao());

        return toDTO(repository.save(lancamento));
    }

    @Override
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new LancamentoNotFoundException(id);
        }
        repository.deleteById(id);
    }

    private LancamentoResponseDTO toDTO(Lancamento lancamento) {
        return LancamentoResponseDTO.builder()
            .id(lancamento.getId())
            .descricao(lancamento.getDescricao())
            .valor(lancamento.getValor())
            .tipo(lancamento.getTipo())
            .data(lancamento.getData())
            .categoria(lancamento.getCategoria())
            .contaId(lancamento.getConta() != null ? lancamento.getConta().getId() : null)
            .contaNome(lancamento.getConta() != null ? lancamento.getConta().getNome() : null)
            .observacao(lancamento.getObservacao())
            .dataCriacao(lancamento.getDataCriacao())
            .dataAtualizacao(lancamento.getDataAtualizacao())
            .build();
    }

    private Conta buscarConta(Long contaId) {
        return contaRepository.findById(contaId)
            .orElseThrow(() -> new ContaNotFoundException(contaId));
    }
}

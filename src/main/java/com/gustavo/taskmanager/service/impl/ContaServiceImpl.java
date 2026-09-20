package com.gustavo.taskmanager.service.impl;

import com.gustavo.taskmanager.dto.ContaRequestDTO;
import com.gustavo.taskmanager.dto.ContaResponseDTO;
import com.gustavo.taskmanager.exception.ContaNotFoundException;
import com.gustavo.taskmanager.model.Conta;
import com.gustavo.taskmanager.repository.ContaRepository;
import com.gustavo.taskmanager.service.ContaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContaServiceImpl implements ContaService {

    private final ContaRepository repository;

    @Override
    public ContaResponseDTO criar(ContaRequestDTO dto) {
        Conta conta = Conta.builder()
            .nome(dto.getNome())
            .tipo(dto.getTipo())
            .saldoInicial(dto.getSaldoInicial())
            .build();

        return toDTO(repository.save(conta));
    }

    @Override
    public List<ContaResponseDTO> listarTodas() {
        return repository.findAll()
            .stream()
            .map(this::toDTO)
            .toList();
    }

    @Override
    public ContaResponseDTO buscarPorId(Long id) {
        return toDTO(repository.findById(id)
            .orElseThrow(() -> new ContaNotFoundException(id)));
    }

    @Override
    public ContaResponseDTO atualizar(Long id, ContaRequestDTO dto) {
        Conta conta = repository.findById(id)
            .orElseThrow(() -> new ContaNotFoundException(id));

        conta.setNome(dto.getNome());
        conta.setTipo(dto.getTipo());
        conta.setSaldoInicial(dto.getSaldoInicial());

        return toDTO(repository.save(conta));
    }

    @Override
    public void desativar(Long id) {
        Conta conta = repository.findById(id)
            .orElseThrow(() -> new ContaNotFoundException(id));
        conta.setAtivo(false);
        repository.save(conta);
    }

    private ContaResponseDTO toDTO(Conta conta) {
        return ContaResponseDTO.builder()
            .id(conta.getId())
            .nome(conta.getNome())
            .tipo(conta.getTipo())
            .saldoInicial(conta.getSaldoInicial())
            .ativo(conta.isAtivo())
            .dataCriacao(conta.getDataCriacao())
            .dataAtualizacao(conta.getDataAtualizacao())
            .build();
    }
}

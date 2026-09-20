package com.gustavo.taskmanager.service.impl;

import com.gustavo.taskmanager.dto.ContaRequestDTO;
import com.gustavo.taskmanager.dto.ContaResponseDTO;
import com.gustavo.taskmanager.exception.ContaNotFoundException;
import com.gustavo.taskmanager.model.Conta;
import com.gustavo.taskmanager.repository.ContaRepository;
import com.gustavo.taskmanager.service.ContaService;
import com.gustavo.taskmanager.service.UsuarioAtualService;
import com.gustavo.taskmanager.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContaServiceImpl implements ContaService {

    private final ContaRepository repository;
    private final UsuarioAtualService usuarioAtualService;

    @Override
    public ContaResponseDTO criar(ContaRequestDTO dto) {
        Conta conta = Conta.builder()
            .nome(dto.getNome())
            .tipo(dto.getTipo())
            .saldoInicial(dto.getSaldoInicial())
            .usuario(usuarioAtual())
            .build();

        return toDTO(repository.save(conta));
    }

    @Override
    public List<ContaResponseDTO> listarTodas() {
        List<Conta> contas = usuarioAtual() == null
            ? repository.findAll()
            : repository.findAllByUsuarioOrderByNomeAsc(usuarioAtual());
        return contas
            .stream()
            .map(this::toDTO)
            .toList();
    }

    @Override
    public ContaResponseDTO buscarPorId(Long id) {
        return toDTO(obterConta(id));
    }

    @Override
    public ContaResponseDTO atualizar(Long id, ContaRequestDTO dto) {
        Conta conta = obterConta(id);

        conta.setNome(dto.getNome());
        conta.setTipo(dto.getTipo());
        conta.setSaldoInicial(dto.getSaldoInicial());

        return toDTO(repository.save(conta));
    }

    @Override
    public void desativar(Long id) {
        Conta conta = obterConta(id);
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

    private Conta obterConta(Long id) {
        Conta conta = repository.findById(id)
            .orElseThrow(() -> new ContaNotFoundException(id));
        Usuario usuario = usuarioAtual();
        if (usuario != null && (conta.getUsuario() == null
                || !usuario.getId().equals(conta.getUsuario().getId()))) {
            throw new ContaNotFoundException(id);
        }
        return conta;
    }

    private Usuario usuarioAtual() {
        return usuarioAtualService == null ? null : usuarioAtualService.obter();
    }
}

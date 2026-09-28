package com.gustavo.taskmanager.service.impl;

import com.gustavo.taskmanager.dto.CategoriaRequestDTO;
import com.gustavo.taskmanager.dto.CategoriaResponseDTO;
import com.gustavo.taskmanager.exception.CategoriaNotFoundException;
import com.gustavo.taskmanager.model.Categoria;
import com.gustavo.taskmanager.repository.CategoriaRepository;
import com.gustavo.taskmanager.service.CategoriaService;
import com.gustavo.taskmanager.service.UsuarioAtualService;
import com.gustavo.taskmanager.service.EspacoAtualService;
import com.gustavo.taskmanager.model.EspacoFinanceiro;
import com.gustavo.taskmanager.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository repository;
    private final UsuarioAtualService usuarioAtualService;
    private final EspacoAtualService espacoAtualService;

    @Override
    public CategoriaResponseDTO criar(CategoriaRequestDTO dto) {
        EspacoFinanceiro espaco = espacoAtualService.obter();
        Usuario usuario = usuarioAtual();
        Categoria categoria = Categoria.builder()
            .nome(dto.getNome())
            .usuario(usuario)
            .atualizadoPor(usuario)
            .espaco(espaco)
            .build();
        return toDTO(repository.save(categoria));
    }

    @Override
    public List<CategoriaResponseDTO> listarTodas() {
        List<Categoria> categorias = repository.findAllByEspacoOrderByNomeAsc(espacoAtualService.obter());
        return categorias
            .stream()
            .map(this::toDTO)
            .toList();
    }

    @Override
    public CategoriaResponseDTO buscarPorId(Long id) {
        return toDTO(obterCategoria(id));
    }

    @Override
    public CategoriaResponseDTO atualizar(Long id, CategoriaRequestDTO dto) {
        Categoria categoria = obterCategoria(id);
        categoria.setNome(dto.getNome());
        categoria.setAtualizadoPor(usuarioAtual());
        return toDTO(repository.save(categoria));
    }

    @Override
    public void desativar(Long id) {
        Categoria categoria = obterCategoria(id);
        categoria.setAtiva(false);
        categoria.setAtualizadoPor(usuarioAtual());
        repository.save(categoria);
    }

    private CategoriaResponseDTO toDTO(Categoria categoria) {
        return CategoriaResponseDTO.builder()
            .id(categoria.getId())
            .nome(categoria.getNome())
            .ativa(categoria.isAtiva())
            .criadoPorUsuarioId(categoria.getUsuario() == null ? null : categoria.getUsuario().getId())
            .atualizadoPorUsuarioId(categoria.getAtualizadoPor() == null ? null : categoria.getAtualizadoPor().getId())
            .dataCriacao(categoria.getDataCriacao())
            .build();
    }

    private Categoria obterCategoria(Long id) {
        Categoria categoria = repository.findById(id)
            .orElseThrow(() -> new CategoriaNotFoundException(id));
        EspacoFinanceiro espacoAtual = espacoAtualService.obter();
        if (categoria.getEspaco() == null || !espacoAtual.getId().equals(categoria.getEspaco().getId())) {
            throw new CategoriaNotFoundException(id);
        }
        return categoria;
    }

    private Usuario usuarioAtual() {
        return usuarioAtualService == null ? null : usuarioAtualService.obter();
    }
}

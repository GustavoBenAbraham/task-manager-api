package com.gustavo.taskmanager.service.impl;

import com.gustavo.taskmanager.dto.CategoriaRequestDTO;
import com.gustavo.taskmanager.dto.CategoriaResponseDTO;
import com.gustavo.taskmanager.exception.CategoriaNotFoundException;
import com.gustavo.taskmanager.model.Categoria;
import com.gustavo.taskmanager.repository.CategoriaRepository;
import com.gustavo.taskmanager.service.CategoriaService;
import com.gustavo.taskmanager.service.UsuarioAtualService;
import com.gustavo.taskmanager.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository repository;
    private final UsuarioAtualService usuarioAtualService;

    @Override
    public CategoriaResponseDTO criar(CategoriaRequestDTO dto) {
        Categoria categoria = Categoria.builder()
            .nome(dto.getNome())
            .usuario(usuarioAtual())
            .build();
        return toDTO(repository.save(categoria));
    }

    @Override
    public List<CategoriaResponseDTO> listarTodas() {
        List<Categoria> categorias = usuarioAtual() == null
            ? repository.findAll()
            : repository.findAllByUsuarioOrderByNomeAsc(usuarioAtual());
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
        return toDTO(repository.save(categoria));
    }

    @Override
    public void desativar(Long id) {
        Categoria categoria = obterCategoria(id);
        categoria.setAtiva(false);
        repository.save(categoria);
    }

    private CategoriaResponseDTO toDTO(Categoria categoria) {
        return CategoriaResponseDTO.builder()
            .id(categoria.getId())
            .nome(categoria.getNome())
            .ativa(categoria.isAtiva())
            .dataCriacao(categoria.getDataCriacao())
            .build();
    }

    private Categoria obterCategoria(Long id) {
        Categoria categoria = repository.findById(id)
            .orElseThrow(() -> new CategoriaNotFoundException(id));
        Usuario usuario = usuarioAtual();
        if (usuario != null && (categoria.getUsuario() == null
                || !usuario.getId().equals(categoria.getUsuario().getId()))) {
            throw new CategoriaNotFoundException(id);
        }
        return categoria;
    }

    private Usuario usuarioAtual() {
        return usuarioAtualService == null ? null : usuarioAtualService.obter();
    }
}

package com.gustavo.taskmanager.service.impl;

import com.gustavo.taskmanager.dto.CategoriaRequestDTO;
import com.gustavo.taskmanager.dto.CategoriaResponseDTO;
import com.gustavo.taskmanager.exception.CategoriaNotFoundException;
import com.gustavo.taskmanager.model.Categoria;
import com.gustavo.taskmanager.repository.CategoriaRepository;
import com.gustavo.taskmanager.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository repository;

    @Override
    public CategoriaResponseDTO criar(CategoriaRequestDTO dto) {
        Categoria categoria = Categoria.builder()
            .nome(dto.getNome())
            .build();
        return toDTO(repository.save(categoria));
    }

    @Override
    public List<CategoriaResponseDTO> listarTodas() {
        return repository.findAll()
            .stream()
            .map(this::toDTO)
            .toList();
    }

    @Override
    public CategoriaResponseDTO buscarPorId(Long id) {
        return toDTO(repository.findById(id)
            .orElseThrow(() -> new CategoriaNotFoundException(id)));
    }

    @Override
    public CategoriaResponseDTO atualizar(Long id, CategoriaRequestDTO dto) {
        Categoria categoria = repository.findById(id)
            .orElseThrow(() -> new CategoriaNotFoundException(id));
        categoria.setNome(dto.getNome());
        return toDTO(repository.save(categoria));
    }

    @Override
    public void desativar(Long id) {
        Categoria categoria = repository.findById(id)
            .orElseThrow(() -> new CategoriaNotFoundException(id));
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
}

package com.gustavo.taskmanager.service;

import com.gustavo.taskmanager.dto.CategoriaRequestDTO;
import com.gustavo.taskmanager.dto.CategoriaResponseDTO;

import java.util.List;

public interface CategoriaService {
    CategoriaResponseDTO criar(CategoriaRequestDTO dto);
    List<CategoriaResponseDTO> listarTodas();
    CategoriaResponseDTO buscarPorId(Long id);
    CategoriaResponseDTO atualizar(Long id, CategoriaRequestDTO dto);
    void desativar(Long id);
}

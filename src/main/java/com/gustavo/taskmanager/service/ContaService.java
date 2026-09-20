package com.gustavo.taskmanager.service;

import com.gustavo.taskmanager.dto.ContaRequestDTO;
import com.gustavo.taskmanager.dto.ContaResponseDTO;

import java.util.List;

public interface ContaService {
    ContaResponseDTO criar(ContaRequestDTO dto);
    List<ContaResponseDTO> listarTodas();
    ContaResponseDTO buscarPorId(Long id);
    ContaResponseDTO atualizar(Long id, ContaRequestDTO dto);
    void desativar(Long id);
}

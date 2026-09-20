package com.gustavo.taskmanager.service;

import com.gustavo.taskmanager.dto.LancamentoRequestDTO;
import com.gustavo.taskmanager.dto.LancamentoResponseDTO;
import com.gustavo.taskmanager.model.TipoLancamento;

import java.util.List;

public interface LancamentoService {
    LancamentoResponseDTO criar(LancamentoRequestDTO dto);
    List<LancamentoResponseDTO> listarTodos();
    LancamentoResponseDTO buscarPorId(Long id);
    List<LancamentoResponseDTO> buscarPorTipo(TipoLancamento tipo);
    LancamentoResponseDTO atualizar(Long id, LancamentoRequestDTO dto);
    void deletar(Long id);
}

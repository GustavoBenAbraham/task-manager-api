package com.gustavo.taskmanager.service;

import com.gustavo.taskmanager.dto.TaskRequestDTO;
import com.gustavo.taskmanager.dto.TaskResponseDTO;
import com.gustavo.taskmanager.model.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TaskService {
    TaskResponseDTO criar(TaskRequestDTO dto);
    List<TaskResponseDTO> listarTodas();
    Page<TaskResponseDTO> listarTodasPaginado(Pageable pageable);
    TaskResponseDTO buscarPorId(Long id);
    List<TaskResponseDTO> buscarPorStatus(TaskStatus status);
    Page<TaskResponseDTO> buscarPorStatusPaginado(TaskStatus status, Pageable pageable);
    TaskResponseDTO atualizar(Long id, TaskRequestDTO dto);
    void deletar(Long id);
}
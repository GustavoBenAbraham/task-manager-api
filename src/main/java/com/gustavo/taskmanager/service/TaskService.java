package com.gustavo.taskmanager.service;

import com.gustavo.taskmanager.dto.TaskRequestDTO;
import com.gustavo.taskmanager.dto.TaskResponseDTO;
import com.gustavo.taskmanager.model.TaskStatus;

import java.util.List;

public interface TaskService {
    TaskResponseDTO criar(TaskRequestDTO dto);
    List<TaskResponseDTO> listarTodas();
    TaskResponseDTO buscarPorId(Long id);
    List<TaskResponseDTO> buscarPorStatus(TaskStatus status);
    TaskResponseDTO atualizar(Long id, TaskRequestDTO dto);
    void deletar(Long id);
}
package com.gustavo.taskmanager.service.impl;

import com.gustavo.taskmanager.dto.TaskRequestDTO;
import com.gustavo.taskmanager.dto.TaskResponseDTO;
import com.gustavo.taskmanager.exception.TaskNotFoundException;
import com.gustavo.taskmanager.model.Task;
import com.gustavo.taskmanager.model.TaskStatus;
import com.gustavo.taskmanager.repository.TaskRepository;
import com.gustavo.taskmanager.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;

    @Override
    public TaskResponseDTO criar(TaskRequestDTO dto) {
        Task task = Task.builder()
            .titulo(dto.getTitulo())
            .descricao(dto.getDescricao())
            .status(dto.getStatus() != null ? dto.getStatus() : TaskStatus.PENDENTE)
            .build();
        
        Task salva = repository.save(task);
        return toDTO(salva);
    }

    @Override
    public List<TaskResponseDTO> listarTodas() {
        return repository.findAll()
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    @Override
    public TaskResponseDTO buscarPorId(Long id) {
        Task task = repository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(id));
        return toDTO(task);
    }

    @Override
    public List<TaskResponseDTO> buscarPorStatus(TaskStatus status) {
        return repository.findByStatus(status)
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    @Override
    public TaskResponseDTO atualizar(Long id, TaskRequestDTO dto) {
        Task task = repository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(id));
        
        task.setTitulo(dto.getTitulo());
        task.setDescricao(dto.getDescricao());
        if (dto.getStatus() != null) {
            task.setStatus(dto.getStatus());
        }
        
        Task atualizada = repository.save(task);
        return toDTO(atualizada);
    }

    @Override
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }
        repository.deleteById(id);
    }

    private TaskResponseDTO toDTO(Task task) {
        return TaskResponseDTO.builder()
            .id(task.getId())
            .titulo(task.getTitulo())
            .descricao(task.getDescricao())
            .status(task.getStatus())
            .dataCriacao(task.getDataCriacao())
            .dataAtualizacao(task.getDataAtualizacao())
            .build();
    }
}
package com.gustavo.taskmanager.service.impl;

import com.gustavo.taskmanager.dto.TaskRequestDTO;
import com.gustavo.taskmanager.dto.TaskResponseDTO;
import com.gustavo.taskmanager.exception.TaskNotFoundException;
import com.gustavo.taskmanager.model.Task;
import com.gustavo.taskmanager.model.TaskStatus;
import com.gustavo.taskmanager.model.Usuario;
import com.gustavo.taskmanager.repository.TaskRepository;
import com.gustavo.taskmanager.service.TaskService;
import com.gustavo.taskmanager.service.UsuarioAtualService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;
    private final UsuarioAtualService usuarioAtualService;

    @Override
    public TaskResponseDTO criar(TaskRequestDTO dto) {
        log.info("Criando tarefa: {}", dto.getTitulo());
        Task task = Task.builder()
            .titulo(dto.getTitulo())
            .descricao(dto.getDescricao())
            .status(dto.getStatus() != null ? dto.getStatus() : TaskStatus.PENDENTE)
            .usuario(usuarioAtual())
            .build();

        Task salva = repository.save(task);
        log.info("Tarefa criada com sucesso: id={}", salva.getId());
        return toDTO(salva);
    }

    @Override
    public List<TaskResponseDTO> listarTodas() {
        log.debug("Listando todas as tarefas");
        Usuario usuario = usuarioAtual();
        List<Task> tasks = usuario == null
            ? repository.findAll()
            : repository.findByUsuario(usuario);
        return tasks.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    @Override
    public Page<TaskResponseDTO> listarTodasPaginado(Pageable pageable) {
        log.debug("Listando tarefas paginadas: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        Usuario usuario = usuarioAtual();
        Page<Task> tasks = usuario == null
            ? repository.findAll(pageable)
            : repository.findByUsuario(usuario, pageable);
        return tasks.map(this::toDTO);
    }

    @Override
    public TaskResponseDTO buscarPorId(Long id) {
        log.debug("Buscando tarefa por id: {}", id);
        Task task = obterTask(id);
        return toDTO(task);
    }

    @Override
    public List<TaskResponseDTO> buscarPorStatus(TaskStatus status) {
        log.debug("Buscando tarefas por status: {}", status);
        Usuario usuario = usuarioAtual();
        List<Task> tasks = usuario == null
            ? repository.findByStatus(status)
            : repository.findByUsuarioAndStatus(usuario, status);
        return tasks.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    @Override
    public Page<TaskResponseDTO> buscarPorStatusPaginado(TaskStatus status, Pageable pageable) {
        log.debug("Buscando tarefas por status paginado: status={}, page={}", status, pageable.getPageNumber());
        Usuario usuario = usuarioAtual();
        Page<Task> tasks = usuario == null
            ? repository.findByStatus(status, pageable)
            : repository.findByUsuarioAndStatus(usuario, status, pageable);
        return tasks.map(this::toDTO);
    }

    @Override
    public TaskResponseDTO atualizar(Long id, TaskRequestDTO dto) {
        log.info("Atualizando tarefa: id={}", id);
        Task task = obterTask(id);

        task.setTitulo(dto.getTitulo());
        task.setDescricao(dto.getDescricao());
        if (dto.getStatus() != null) {
            task.setStatus(dto.getStatus());
        }

        Task atualizada = repository.save(task);
        log.info("Tarefa atualizada com sucesso: id={}", id);
        return toDTO(atualizada);
    }

    @Override
    public void deletar(Long id) {
        log.info("Deletando tarefa: id={}", id);
        obterTask(id);
        repository.deleteById(id);
        log.info("Tarefa deletada com sucesso: id={}", id);
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

    private Task obterTask(Long id) {
        Task task = repository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(id));
        Usuario usuario = usuarioAtual();
        if (usuario != null && (task.getUsuario() == null
                || !usuario.getId().equals(task.getUsuario().getId()))) {
            log.warn("Tentativa de acesso a tarefa de outro usuário: taskId={}, userId={}", id, usuario.getId());
            throw new TaskNotFoundException(id);
        }
        return task;
    }

    private Usuario usuarioAtual() {
        return usuarioAtualService.obter();
    }
}
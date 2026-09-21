package com.gustavo.taskmanager.controller;

import com.gustavo.taskmanager.dto.TaskRequestDTO;
import com.gustavo.taskmanager.dto.TaskResponseDTO;
import com.gustavo.taskmanager.model.TaskStatus;
import com.gustavo.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponseDTO> criar(@Valid @RequestBody TaskRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.criar(dto));
    }

    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> listarTodas() {
        return ResponseEntity.ok(taskService.listarTodas());
    }

    @GetMapping("/paginado")
    public ResponseEntity<Page<TaskResponseDTO>> listarTodasPaginado(
            @PageableDefault(size = 10, sort = "dataCriacao") Pageable pageable) {
        return ResponseEntity.ok(taskService.listarTodasPaginado(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.buscarPorId(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TaskResponseDTO>> buscarPorStatus(@PathVariable TaskStatus status) {
        return ResponseEntity.ok(taskService.buscarPorStatus(status));
    }

    @GetMapping("/status/{status}/paginado")
    public ResponseEntity<Page<TaskResponseDTO>> buscarPorStatusPaginado(
            @PathVariable TaskStatus status,
            @PageableDefault(size = 10, sort = "dataCriacao") Pageable pageable) {
        return ResponseEntity.ok(taskService.buscarPorStatusPaginado(status, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequestDTO dto) {
        return ResponseEntity.ok(taskService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        taskService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
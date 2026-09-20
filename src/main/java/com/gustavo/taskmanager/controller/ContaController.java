package com.gustavo.taskmanager.controller;

import com.gustavo.taskmanager.dto.ContaRequestDTO;
import com.gustavo.taskmanager.dto.ContaResponseDTO;
import com.gustavo.taskmanager.service.ContaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contas")
@RequiredArgsConstructor
public class ContaController {

    private final ContaService contaService;

    @PostMapping
    public ResponseEntity<ContaResponseDTO> criar(@Valid @RequestBody ContaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contaService.criar(dto));
    }

    @GetMapping
    public ResponseEntity<List<ContaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(contaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(contaService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContaResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ContaRequestDTO dto) {
        return ResponseEntity.ok(contaService.atualizar(id, dto));
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        contaService.desativar(id);
        return ResponseEntity.noContent().build();
    }
}

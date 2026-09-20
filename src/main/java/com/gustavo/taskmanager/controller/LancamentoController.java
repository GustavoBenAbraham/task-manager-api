package com.gustavo.taskmanager.controller;

import com.gustavo.taskmanager.dto.LancamentoRequestDTO;
import com.gustavo.taskmanager.dto.LancamentoResponseDTO;
import com.gustavo.taskmanager.model.TipoLancamento;
import com.gustavo.taskmanager.service.LancamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lancamentos")
@RequiredArgsConstructor
public class LancamentoController {

    private final LancamentoService lancamentoService;

    @PostMapping
    public ResponseEntity<LancamentoResponseDTO> criar(
            @Valid @RequestBody LancamentoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(lancamentoService.criar(dto));
    }

    @GetMapping
    public ResponseEntity<List<LancamentoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(lancamentoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LancamentoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(lancamentoService.buscarPorId(id));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<LancamentoResponseDTO>> buscarPorTipo(
            @PathVariable TipoLancamento tipo) {
        return ResponseEntity.ok(lancamentoService.buscarPorTipo(tipo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LancamentoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody LancamentoRequestDTO dto) {
        return ResponseEntity.ok(lancamentoService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        lancamentoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

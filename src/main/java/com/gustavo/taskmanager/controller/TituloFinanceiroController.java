package com.gustavo.taskmanager.controller;

import com.gustavo.taskmanager.dto.LiquidarTituloFinanceiroRequestDTO;
import com.gustavo.taskmanager.dto.TituloFinanceiroRequestDTO;
import com.gustavo.taskmanager.dto.TituloFinanceiroResponseDTO;
import com.gustavo.taskmanager.service.TituloFinanceiroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contas-previstas")
@RequiredArgsConstructor
public class TituloFinanceiroController {

    private final TituloFinanceiroService service;

    @GetMapping
    public ResponseEntity<List<TituloFinanceiroResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @PostMapping
    public ResponseEntity<TituloFinanceiroResponseDTO> criar(
            @Valid @RequestBody TituloFinanceiroRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TituloFinanceiroResponseDTO> atualizar(
            @PathVariable Long id, @Valid @RequestBody TituloFinanceiroRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @PostMapping("/{id}/liquidar")
    public ResponseEntity<TituloFinanceiroResponseDTO> liquidar(
            @PathVariable Long id, @Valid @RequestBody LiquidarTituloFinanceiroRequestDTO dto) {
        return ResponseEntity.ok(service.liquidar(id, dto));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<TituloFinanceiroResponseDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(service.cancelar(id));
    }
}

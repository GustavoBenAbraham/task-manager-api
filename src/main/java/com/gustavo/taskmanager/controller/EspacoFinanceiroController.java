package com.gustavo.taskmanager.controller;

import com.gustavo.taskmanager.dto.EspacoFinanceiroRequestDTO;
import com.gustavo.taskmanager.dto.EspacoFinanceiroResponseDTO;
import com.gustavo.taskmanager.dto.CriarConviteRequestDTO;
import com.gustavo.taskmanager.dto.ConviteEspacoResponseDTO;
import com.gustavo.taskmanager.dto.AcessoEspacoResponseDTO;
import com.gustavo.taskmanager.service.AcessoEspacoService;
import com.gustavo.taskmanager.service.EspacoFinanceiroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/espacos")
@RequiredArgsConstructor
public class EspacoFinanceiroController {

    private final EspacoFinanceiroService service;
    private final AcessoEspacoService acessoService;

    @GetMapping
    public ResponseEntity<List<EspacoFinanceiroResponseDTO>> listarMeusEspacos() {
        return ResponseEntity.ok(service.listarMeusEspacos());
    }

    @PostMapping
    public ResponseEntity<EspacoFinanceiroResponseDTO> criarNegocio(
            @Valid @RequestBody EspacoFinanceiroRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criarNegocio(dto));
    }

    @GetMapping("/{espacoId}/acessos")
    public ResponseEntity<List<AcessoEspacoResponseDTO>> listarAcessos(@PathVariable Long espacoId) {
        return ResponseEntity.ok(acessoService.listarAcessos(espacoId));
    }

    @PostMapping("/{espacoId}/convites")
    public ResponseEntity<ConviteEspacoResponseDTO> convidarGestora(
            @PathVariable Long espacoId,
            @Valid @RequestBody CriarConviteRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(acessoService.convidarGestora(espacoId, dto.getEmail()));
    }

    @DeleteMapping("/{espacoId}/acessos/{usuarioId}")
    public ResponseEntity<Void> removerGestora(
            @PathVariable Long espacoId, @PathVariable Long usuarioId) {
        acessoService.removerGestora(espacoId, usuarioId);
        return ResponseEntity.noContent().build();
    }
}

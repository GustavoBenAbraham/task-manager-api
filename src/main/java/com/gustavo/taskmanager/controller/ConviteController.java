package com.gustavo.taskmanager.controller;

import com.gustavo.taskmanager.dto.AceitarConviteRequestDTO;
import com.gustavo.taskmanager.dto.EspacoFinanceiroResponseDTO;
import com.gustavo.taskmanager.service.AcessoEspacoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/convites")
@RequiredArgsConstructor
public class ConviteController {

    private final AcessoEspacoService acessoEspacoService;

    @PostMapping("/aceitar")
    public ResponseEntity<EspacoFinanceiroResponseDTO> aceitar(
            @Valid @RequestBody AceitarConviteRequestDTO dto) {
        return ResponseEntity.ok(acessoEspacoService.aceitarConvite(dto.getToken()));
    }
}

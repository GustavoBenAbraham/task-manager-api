package com.gustavo.taskmanager.controller;

import com.gustavo.taskmanager.dto.AuthLoginRequestDTO;
import com.gustavo.taskmanager.dto.AuthRegisterRequestDTO;
import com.gustavo.taskmanager.dto.AuthResponseDTO;
import com.gustavo.taskmanager.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Void> registrar(@Valid @RequestBody AuthRegisterRequestDTO dto) {
        authService.registrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthLoginRequestDTO dto) {
        return ResponseEntity.ok(authService.autenticar(dto));
    }
}

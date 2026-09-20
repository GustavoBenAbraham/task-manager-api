package com.gustavo.taskmanager.service;

import com.gustavo.taskmanager.dto.AuthLoginRequestDTO;
import com.gustavo.taskmanager.dto.AuthRegisterRequestDTO;
import com.gustavo.taskmanager.dto.AuthResponseDTO;

public interface AuthService {
    void registrar(AuthRegisterRequestDTO dto);
    AuthResponseDTO autenticar(AuthLoginRequestDTO dto);
}

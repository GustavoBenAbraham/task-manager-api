package com.gustavo.taskmanager.service.impl;

import com.gustavo.taskmanager.dto.AuthLoginRequestDTO;
import com.gustavo.taskmanager.dto.AuthRegisterRequestDTO;
import com.gustavo.taskmanager.dto.AuthResponseDTO;
import com.gustavo.taskmanager.exception.EmailAlreadyUsedException;
import com.gustavo.taskmanager.model.Usuario;
import com.gustavo.taskmanager.repository.UsuarioRepository;
import com.gustavo.taskmanager.service.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl service;

    @Test
    void deveRegistrarUsuarioComSenhaHash() {
        AuthRegisterRequestDTO request = AuthRegisterRequestDTO.builder()
            .nome("Maria")
            .email("MARIA@EXEMPLO.COM")
            .senha("senha-segura")
            .build();
        when(repository.existsByEmailIgnoreCase("MARIA@EXEMPLO.COM")).thenReturn(false);
        when(passwordEncoder.encode("senha-segura")).thenReturn("hash");

        service.registrar(request);

        verify(repository).save(any(Usuario.class));
        verify(passwordEncoder).encode("senha-segura");
    }

    @Test
    void deveRecusarEmailDuplicado() {
        AuthRegisterRequestDTO request = AuthRegisterRequestDTO.builder()
            .nome("Maria")
            .email("maria@exemplo.com")
            .senha("senha-segura")
            .build();
        when(repository.existsByEmailIgnoreCase("maria@exemplo.com")).thenReturn(true);

        assertThrows(EmailAlreadyUsedException.class, () -> service.registrar(request));
    }

    @Test
    void deveAutenticarUsuarioEEmitirToken() {
        Usuario usuario = Usuario.builder()
            .email("maria@exemplo.com")
            .senhaHash("hash")
            .ativo(true)
            .build();
        when(repository.findByEmailIgnoreCase("maria@exemplo.com"))
            .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senha-segura", "hash")).thenReturn(true);
        when(jwtService.gerarToken(any())).thenReturn("token");

        AuthResponseDTO response = service.autenticar(AuthLoginRequestDTO.builder()
            .email("maria@exemplo.com")
            .senha("senha-segura")
            .build());

        assertEquals("token", response.getToken());
        assertEquals("Bearer", response.getTipo());
    }

    @Test
    void deveRecusarSenhaInvalida() {
        Usuario usuario = Usuario.builder()
            .email("maria@exemplo.com")
            .senhaHash("hash")
            .ativo(true)
            .build();
        when(repository.findByEmailIgnoreCase("maria@exemplo.com"))
            .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(eq("senha-errada"), eq("hash"))).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> service.autenticar(
            AuthLoginRequestDTO.builder()
                .email("maria@exemplo.com")
                .senha("senha-errada")
                .build()));
    }
}

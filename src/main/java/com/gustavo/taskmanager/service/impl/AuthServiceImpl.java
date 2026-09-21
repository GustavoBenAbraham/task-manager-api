package com.gustavo.taskmanager.service.impl;

import com.gustavo.taskmanager.dto.AuthLoginRequestDTO;
import com.gustavo.taskmanager.dto.AuthRegisterRequestDTO;
import com.gustavo.taskmanager.dto.AuthResponseDTO;
import com.gustavo.taskmanager.exception.EmailAlreadyUsedException;
import com.gustavo.taskmanager.model.Usuario;
import com.gustavo.taskmanager.repository.UsuarioRepository;
import com.gustavo.taskmanager.service.AuthService;
import com.gustavo.taskmanager.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public void registrar(AuthRegisterRequestDTO dto) {
        log.info("Tentando registrar usuário: {}", dto.getEmail());
        if (repository.existsByEmailIgnoreCase(dto.getEmail())) {
            log.warn("E-mail já em uso: {}", dto.getEmail());
            throw new EmailAlreadyUsedException();
        }

        Usuario usuario = Usuario.builder()
            .nome(dto.getNome())
            .email(dto.getEmail().trim().toLowerCase())
            .senhaHash(passwordEncoder.encode(dto.getSenha()))
            .build();
        repository.save(usuario);
        log.info("Usuário registrado com sucesso: {}", usuario.getEmail());
    }

    @Override
    public AuthResponseDTO autenticar(AuthLoginRequestDTO dto) {
        log.info("Tentando autenticar usuário: {}", dto.getEmail());
        Usuario usuario = repository.findByEmailIgnoreCase(dto.getEmail().trim().toLowerCase())
            .filter(Usuario::isAtivo)
            .orElseThrow(() -> {
                log.warn("Falha na autenticação: usuário não encontrado ou inativo: {}", dto.getEmail());
                return new BadCredentialsException("E-mail ou senha inválidos");
            });

        if (!passwordEncoder.matches(dto.getSenha(), usuario.getSenhaHash())) {
            log.warn("Falha na autenticação: senha inválida para: {}", dto.getEmail());
            throw new BadCredentialsException("E-mail ou senha inválidos");
        }

        UserDetails userDetails = User.withUsername(usuario.getEmail())
            .password(usuario.getSenhaHash())
            .roles("USER")
            .build();
        String token = jwtService.gerarToken(userDetails);
        log.info("Autenticação bem-sucedida: {}", usuario.getEmail());
        return new AuthResponseDTO(token, "Bearer");
    }
}

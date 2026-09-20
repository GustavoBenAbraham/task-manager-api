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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public void registrar(AuthRegisterRequestDTO dto) {
        if (repository.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new EmailAlreadyUsedException();
        }

        Usuario usuario = Usuario.builder()
            .nome(dto.getNome())
            .email(dto.getEmail().trim().toLowerCase())
            .senhaHash(passwordEncoder.encode(dto.getSenha()))
            .build();
        repository.save(usuario);
    }

    @Override
    public AuthResponseDTO autenticar(AuthLoginRequestDTO dto) {
        Usuario usuario = repository.findByEmailIgnoreCase(dto.getEmail().trim().toLowerCase())
            .filter(Usuario::isAtivo)
            .orElseThrow(() -> new BadCredentialsException("E-mail ou senha inválidos"));

        if (!passwordEncoder.matches(dto.getSenha(), usuario.getSenhaHash())) {
            throw new BadCredentialsException("E-mail ou senha inválidos");
        }

        UserDetails userDetails = User.withUsername(usuario.getEmail())
            .password(usuario.getSenhaHash())
            .roles("USER")
            .build();
        return new AuthResponseDTO(jwtService.gerarToken(userDetails), "Bearer");
    }
}

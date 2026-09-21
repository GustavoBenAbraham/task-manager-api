package com.gustavo.taskmanager.service;

import com.gustavo.taskmanager.model.Usuario;
import com.gustavo.taskmanager.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioAtualService {

    private final UsuarioRepository repository;

    public Usuario obter() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            log.debug("Usuário não autenticado ou anonymous");
            return null;
        }
        Usuario usuario = repository.findByEmailIgnoreCase(authentication.getName()).orElse(null);
        log.debug("Usuário obtido: {}", usuario != null ? usuario.getEmail() : "null");
        return usuario;
    }
}

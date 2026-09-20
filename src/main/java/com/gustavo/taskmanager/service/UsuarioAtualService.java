package com.gustavo.taskmanager.service;

import com.gustavo.taskmanager.model.Usuario;
import com.gustavo.taskmanager.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioAtualService {

    private final UsuarioRepository repository;

    public Usuario obter() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        return repository.findByEmailIgnoreCase(authentication.getName()).orElse(null);
    }
}

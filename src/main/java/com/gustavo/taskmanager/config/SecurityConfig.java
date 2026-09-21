package com.gustavo.taskmanager.config;

import com.gustavo.taskmanager.repository.UsuarioRepository;
import com.gustavo.taskmanager.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter)
            throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(
                org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                .anyRequest().authenticated())
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(UsuarioRepository repository) {
        return email -> repository.findByEmailIgnoreCase(email)
            .filter(usuario -> usuario.isAtivo())
            .map(usuario -> User.withUsername(usuario.getEmail())
                .password(usuario.getSenhaHash())
                .roles("USER")
                .build())
            .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException(
                "Usuário não encontrado"));
    }

    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        return new JwtAuthenticationFilter(jwtService, userDetailsService);
    }

    static class JwtAuthenticationFilter extends OncePerRequestFilter {
        private final JwtService jwtService;
        private final UserDetailsService userDetailsService;

        JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
            this.jwtService = jwtService;
            this.userDetailsService = userDetailsService;
        }

        @Override
        protected void doFilterInternal(
                HttpServletRequest request,
                HttpServletResponse response,
                FilterChain filterChain) throws ServletException, IOException {
            String authorization = request.getHeader("Authorization");
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            try {
                String token = authorization.substring(7);
                String email = jwtService.extrairEmail(token);
                if (org.springframework.security.core.context.SecurityContextHolder
                        .getContext().getAuthentication() == null) {
                    var userDetails = userDetailsService.loadUserByUsername(email);
                    if (jwtService.tokenValido(token, userDetails)) {
                        var authentication = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                        org.springframework.security.core.context.SecurityContextHolder
                            .getContext().setAuthentication(authentication);
                    }
                }
            } catch (RuntimeException ignored) {
                org.springframework.security.core.context.SecurityContextHolder.clearContext();
            }

            filterChain.doFilter(request, response);
        }
    }
}

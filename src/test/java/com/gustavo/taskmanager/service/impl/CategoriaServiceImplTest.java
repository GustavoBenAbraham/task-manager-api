package com.gustavo.taskmanager.service.impl;

import com.gustavo.taskmanager.dto.CategoriaRequestDTO;
import com.gustavo.taskmanager.dto.CategoriaResponseDTO;
import com.gustavo.taskmanager.exception.CategoriaNotFoundException;
import com.gustavo.taskmanager.model.Categoria;
import com.gustavo.taskmanager.repository.CategoriaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceImplTest {

    @Mock
    private CategoriaRepository repository;

    @InjectMocks
    private CategoriaServiceImpl service;

    @Test
    void deveCriarCategoria() {
        CategoriaRequestDTO request = CategoriaRequestDTO.builder()
            .nome("Alimentação")
            .build();
        when(repository.save(any(Categoria.class))).thenAnswer(invocation -> {
            Categoria categoria = invocation.getArgument(0);
            categoria.setId(1L);
            return categoria;
        });

        CategoriaResponseDTO response = service.criar(request);

        assertEquals(1L, response.getId());
        assertEquals("Alimentação", response.getNome());
        assertEquals(true, response.isAtiva());
    }

    @Test
    void deveDesativarCategoriaExistente() {
        Categoria categoria = Categoria.builder().id(1L).nome("Lazer").ativa(true).build();
        when(repository.findById(1L)).thenReturn(Optional.of(categoria));
        when(repository.save(categoria)).thenReturn(categoria);

        service.desativar(1L);

        assertFalse(categoria.isAtiva());
    }

    @Test
    void deveLancarExcecaoParaCategoriaInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CategoriaNotFoundException.class, () -> service.buscarPorId(99L));
    }
}

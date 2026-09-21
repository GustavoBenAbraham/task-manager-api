package com.gustavo.taskmanager.service.impl;

import com.gustavo.taskmanager.dto.TaskRequestDTO;
import com.gustavo.taskmanager.dto.TaskResponseDTO;
import com.gustavo.taskmanager.exception.TaskNotFoundException;
import com.gustavo.taskmanager.model.Task;
import com.gustavo.taskmanager.model.TaskStatus;
import com.gustavo.taskmanager.model.Usuario;
import com.gustavo.taskmanager.repository.TaskRepository;
import com.gustavo.taskmanager.service.UsuarioAtualService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository repository;

    @Mock
    private UsuarioAtualService usuarioAtualService;

    @InjectMocks
    private TaskServiceImpl service;

    @Test
    void deveCriarTarefaComStatusPendenteQuandoStatusNaoForInformado() {
        TaskRequestDTO request = TaskRequestDTO.builder()
            .titulo("Estudar testes")
            .descricao("Criar testes unitarios")
            .build();

        when(usuarioAtualService.obter()).thenReturn(Usuario.builder().id(1L).build());
        when(repository.save(any(Task.class))).thenAnswer(invocation -> {
            Task task = invocation.getArgument(0);
            task.setId(1L);
            return task;
        });

        TaskResponseDTO response = service.criar(request);

        assertEquals(1L, response.getId());
        assertEquals("Estudar testes", response.getTitulo());
        assertEquals(TaskStatus.PENDENTE, response.getStatus());
    }

    @Test
    void deveLancarExcecaoQuandoTarefaNaoForEncontrada() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> service.buscarPorId(99L));
    }

    @Test
    void devePreservarStatusAtualQuandoStatusNaoForInformadoNaAtualizacao() {
        Usuario usuario = Usuario.builder().id(1L).build();
        Task task = Task.builder()
            .id(1L)
            .titulo("Titulo antigo")
            .descricao("Descricao antiga")
            .status(TaskStatus.EM_ANDAMENTO)
            .usuario(usuario)
            .build();
        TaskRequestDTO request = TaskRequestDTO.builder()
            .titulo("Titulo novo")
            .descricao("Descricao nova")
            .build();

        when(usuarioAtualService.obter()).thenReturn(usuario);
        when(repository.findById(1L)).thenReturn(Optional.of(task));
        when(repository.save(task)).thenReturn(task);

        TaskResponseDTO response = service.atualizar(1L, request);

        assertEquals("Titulo novo", response.getTitulo());
        assertEquals("Descricao nova", response.getDescricao());
        assertEquals(TaskStatus.EM_ANDAMENTO, response.getStatus());
        verify(repository).save(task);
    }
}

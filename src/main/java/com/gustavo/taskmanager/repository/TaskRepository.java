package com.gustavo.taskmanager.repository;

import com.gustavo.taskmanager.model.Task;
import com.gustavo.taskmanager.model.TaskStatus;
import com.gustavo.taskmanager.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatus(TaskStatus status);
    List<Task> findByTituloContainingIgnoreCase(String titulo);

    List<Task> findByUsuario(Usuario usuario);
    List<Task> findByUsuarioAndStatus(Usuario usuario, TaskStatus status);

    Page<Task> findByUsuario(Usuario usuario, Pageable pageable);
    Page<Task> findByUsuarioAndStatus(Usuario usuario, TaskStatus status, Pageable pageable);
    Page<Task> findByStatus(TaskStatus status, Pageable pageable);
    Page<Task> findAll(Pageable pageable);
}
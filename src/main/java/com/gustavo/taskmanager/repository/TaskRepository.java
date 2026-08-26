package com.gustavo.taskmanager.repository;

import com.gustavo.taskmanager.model.Task;
import com.gustavo.taskmanager.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByStatus(TaskStatus status);
    List<Task> findByTituloContainingIgnoreCase(String titulo);
}
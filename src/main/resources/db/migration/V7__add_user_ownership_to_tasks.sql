ALTER TABLE tasks
    ADD COLUMN usuario_id BIGINT REFERENCES usuarios(id);

CREATE INDEX idx_tasks_usuario ON tasks (usuario_id);

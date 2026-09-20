package com.gustavo.taskmanager.exception;

public class LancamentoNotFoundException extends RuntimeException {
    public LancamentoNotFoundException(Long id) {
        super("Lançamento não encontrado com o ID: " + id);
    }
}

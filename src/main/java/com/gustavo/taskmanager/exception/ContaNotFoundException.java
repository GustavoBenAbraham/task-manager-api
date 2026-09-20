package com.gustavo.taskmanager.exception;

public class ContaNotFoundException extends RuntimeException {
    public ContaNotFoundException(Long id) {
        super("Conta não encontrada com o ID: " + id);
    }
}

package com.gustavo.taskmanager.exception;

public class EspacoAcessoNegadoException extends RuntimeException {
    public EspacoAcessoNegadoException() {
        super("Você não tem acesso a este espaço financeiro.");
    }
}

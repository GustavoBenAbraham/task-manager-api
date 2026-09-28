package com.gustavo.taskmanager.exception;

public class EspacoJaExisteException extends RuntimeException {
    public EspacoJaExisteException(String tipo) {
        super("Você já possui um espaço " + tipo.toLowerCase() + ".");
    }
}

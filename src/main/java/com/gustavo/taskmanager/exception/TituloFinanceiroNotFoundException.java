package com.gustavo.taskmanager.exception;

public class TituloFinanceiroNotFoundException extends RuntimeException {
    public TituloFinanceiroNotFoundException(Long id) {
        super("Conta prevista não encontrada: " + id);
    }
}

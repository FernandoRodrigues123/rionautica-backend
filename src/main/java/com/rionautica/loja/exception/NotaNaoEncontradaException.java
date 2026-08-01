package com.rionautica.loja.exception;


public class NotaNaoEncontradaException extends RuntimeException {
    public NotaNaoEncontradaException(Long id) {
        super("Nota não encontrada com id: " + id);
    }
}
package com.rionautica.loja.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProdutoNaoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleProdutoNaoEncontrado(ProdutoNaoEncontradoException ex) {
        Map<String, Object> corpo = Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.NOT_FOUND.value(),
                "mensagem", ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(corpo);
    }
    @ExceptionHandler(ClienteNaoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleClienteNaoEncontrado(ClienteNaoEncontradoException ex) {
        Map<String, Object> corpo = Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.NOT_FOUND.value(),
                "mensagem", ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(corpo);
    }
    @ExceptionHandler(NotaNaoEncontradaException.class)
    public ResponseEntity<Map<String, Object>> handleNotaNaoEncontrada(NotaNaoEncontradaException ex) {
        Map<String, Object> corpo = Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.NOT_FOUND.value(),
                "mensagem", ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(corpo);
    }
    @ExceptionHandler(ServicoNaoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleServicoNaoEncontrado(ServicoNaoEncontradoException ex) {
        Map<String, Object> corpo = Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.NOT_FOUND.value(),
                "mensagem", ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(corpo);
    }
}
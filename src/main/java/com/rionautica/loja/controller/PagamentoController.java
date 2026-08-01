package com.rionautica.loja.controller;

import com.rionautica.loja.model.MetodoPagamento;
import com.rionautica.loja.model.Pagamento;
import com.rionautica.loja.services.PagamentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notas/{notaId}/pagamentos")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @GetMapping
    public ResponseEntity<List<Pagamento>> listarPorNota(@PathVariable Long notaId) {
        return ResponseEntity.ok(pagamentoService.listarPorNota(notaId));
    }

    @PostMapping
    public ResponseEntity<Pagamento> registrar(
            @PathVariable Long notaId,
            @RequestBody PagamentoRequest request
    ) {
        Pagamento pagamento = pagamentoService.registrar(
                notaId,
                request.valor(),
                request.metodo(),
                request.conta()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(pagamento);
    }
    @PutMapping("/{pagamentoId}")
    public ResponseEntity<Pagamento> atualizar(
            @PathVariable Long notaId,
            @PathVariable Long pagamentoId,
            @RequestBody PagamentoRequest request
    ) {
        return ResponseEntity.ok(pagamentoService.atualizar(
                pagamentoId,
                request.valor(),
                request.metodo(),
                request.conta()
        ));
    }

    @DeleteMapping("/{pagamentoId}")
    public ResponseEntity<Void> remover(
            @PathVariable Long notaId,
            @PathVariable Long pagamentoId
    ) {
        pagamentoService.remover(pagamentoId);
        return ResponseEntity.noContent().build();
    }

    record PagamentoRequest(Double valor, MetodoPagamento metodo, String conta) {}
}
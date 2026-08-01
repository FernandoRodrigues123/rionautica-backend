package com.rionautica.loja.controller;

import com.rionautica.loja.model.Nota;
import com.rionautica.loja.model.ProdutoNota;
import com.rionautica.loja.model.StatusPagamento;
import com.rionautica.loja.services.NotaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/notas")
public class NotaController {

    private final NotaService notaService;

    @PutMapping("/{id}")
    public ResponseEntity<Nota> atualizar(
            @PathVariable Long id,
            @RequestBody CriarNotaRequest request
    ) {
        return ResponseEntity.ok(notaService.atualizar(
                id,
                request.clienteId(),
                request.itens(),
                request.desconto()
        ));
    }

    public NotaController(NotaService notaService) {
        this.notaService = notaService;
    }

    @GetMapping
    public ResponseEntity<List<Nota>> listarTodas() {
        return ResponseEntity.ok(notaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Nota> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(notaService.buscarPorId(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Nota>> listarPorStatus(@PathVariable StatusPagamento status) {
        return ResponseEntity.ok(notaService.listarPorStatus(status));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Nota>> listarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(notaService.listarPorCliente(clienteId));
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<Nota>> listarPorPeriodo(
            @RequestParam LocalDateTime inicio,
            @RequestParam LocalDateTime fim
    ) {
        return ResponseEntity.ok(notaService.listarPorPeriodo(inicio, fim));
    }

    @PostMapping
    public ResponseEntity<Nota> criar(@RequestBody CriarNotaRequest request) {
        Nota nota = notaService.criar(
                request.clienteId(),
                request.itens(),
                request.desconto()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(nota);
    }

    @PatchMapping("/{id}/pagamento")
    public ResponseEntity<Nota> registrarPagamento(
            @PathVariable Long id,
            @RequestBody PagamentoRequest request
    ) {
        return ResponseEntity.ok(notaService.registrarPagamento(id, request.status()));
    }

    @PatchMapping("/{id}/desconto")
    public ResponseEntity<Nota> aplicarDesconto(
            @PathVariable Long id,
            @RequestBody DescontoRequest request
    ) {
        return ResponseEntity.ok(notaService.aplicarDesconto(id, request.desconto()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        notaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // -------------------------
    // Records de request
    // -------------------------

    record CriarNotaRequest(Long clienteId, List<ProdutoNota> itens, Double desconto) {}
    record PagamentoRequest(StatusPagamento status) {}
    record DescontoRequest(Double desconto) {}
}
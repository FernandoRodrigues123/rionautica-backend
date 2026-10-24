package com.rionautica.loja.controller;

import com.rionautica.loja.model.Nota;
import com.rionautica.loja.model.ProdutoNota;
import com.rionautica.loja.model.ServicoNota;
import com.rionautica.loja.model.StatusPagamento;
import com.rionautica.loja.services.NotaService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/notas")
public class NotaController {

    private final NotaService notaService;



    public NotaController(NotaService notaService) {
        this.notaService = notaService;
    }

    @GetMapping
    public ResponseEntity<Page<Nota>> listarTodas(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho) {
        return ResponseEntity.ok(notaService.listarTodas(pagina, tamanho));
    }
    @GetMapping("/{id}")
    public ResponseEntity<Nota> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(notaService.buscarPorId(id));
    }
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<Nota>> listarPorStatus(
            @PathVariable StatusPagamento status,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho) {
        return ResponseEntity.ok(notaService.listarPorStatus(status, pagina, tamanho));
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
    public ResponseEntity<Nota> criar(
            @RequestBody CriarNotaRequest request
    ) {
        Nota nota = notaService.criar(
                request.clienteId(),
                request.itens(),
                request.servicos(),
                request.desconto(),
                request.observacao(),
                request.isOrcamento(),
                request.agendamentoId()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(nota);
    }
    @PostMapping("/{id}/converter")
    public ResponseEntity<Nota> converterParaNota(@PathVariable Long id) {
        return ResponseEntity.ok(notaService.converterParaNota(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Nota> atualizar(
            @PathVariable Long id,
            @RequestBody CriarNotaRequest request
    ) {
        return ResponseEntity.ok(notaService.atualizar(
                id,
                request.clienteId(),
                request.itens(),
                request.servicos(),
                request.desconto(),
                request.observacao()
        ));
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

    record CriarNotaRequest(
            Long clienteId,
            List<ProdutoNota> itens,
            List<ServicoNota> servicos,
            Double desconto,
            String observacao,
            Boolean isOrcamento,
            Long agendamentoId
    ) {}
    record PagamentoRequest(StatusPagamento status) {}
    record DescontoRequest(Double desconto) {}
}
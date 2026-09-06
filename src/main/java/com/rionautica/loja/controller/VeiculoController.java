package com.rionautica.loja.controller;

import com.rionautica.loja.model.Veiculo;
import com.rionautica.loja.services.VeiculoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/veiculos")
public class VeiculoController {

    private final VeiculoService veiculoService;

    public VeiculoController(VeiculoService veiculoService) {
        this.veiculoService = veiculoService;
    }

    @GetMapping
    public ResponseEntity<List<Veiculo>> listarTodos() {
        return ResponseEntity.ok(veiculoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Veiculo> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(veiculoService.buscarPorId(id));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Veiculo>> listarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(veiculoService.listarPorCliente(clienteId));
    }

    @GetMapping("/pendentes")
    public ResponseEntity<List<Veiculo>> listarComRevisaoPendente() {
        return ResponseEntity.ok(veiculoService.listarComRevisaoPendente());
    }

    @PostMapping("/cliente/{clienteId}")
    public ResponseEntity<Veiculo> salvar(
            @PathVariable Long clienteId,
            @RequestBody Veiculo veiculo) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(veiculoService.salvar(clienteId, veiculo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Veiculo> atualizar(
            @PathVariable Long id,
            @RequestBody Veiculo veiculo) {
        return ResponseEntity.ok(veiculoService.atualizar(id, veiculo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        veiculoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/recalcular")
    public ResponseEntity<Void> recalcular() {
        veiculoService.recalcularRevisaoEmDia();
        return ResponseEntity.ok().build();
    }
}
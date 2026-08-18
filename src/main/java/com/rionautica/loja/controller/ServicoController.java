package com.rionautica.loja.controller;

import com.rionautica.loja.model.Servico;
import com.rionautica.loja.service.ServicoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicos")
public class ServicoController {

    private final ServicoService servicoService;

    public ServicoController(ServicoService servicoService) {
        this.servicoService = servicoService;
    }

    @GetMapping
    public ResponseEntity<List<Servico>> listarTodos() {
        return ResponseEntity.ok(servicoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Servico> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(servicoService.buscarPorId(id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Servico>> buscarPorNome(@RequestParam String nome) {
        return ResponseEntity.ok(servicoService.buscarPorNome(nome));
    }

    @PostMapping
    public ResponseEntity<Servico> salvar(@RequestBody Servico servico) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicoService.salvar(servico));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Servico> atualizar(@PathVariable Long id, @RequestBody Servico servico) {
        return ResponseEntity.ok(servicoService.atualizar(id, servico));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        servicoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
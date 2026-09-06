package com.rionautica.loja.controller;

import com.rionautica.loja.model.Mecanico;
import com.rionautica.loja.service.MecanicoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mecanicos")
public class MecanicoController {

    private final MecanicoService mecanicoService;

    public MecanicoController(MecanicoService mecanicoService) {
        this.mecanicoService = mecanicoService;
    }

    @GetMapping
    public ResponseEntity<List<Mecanico>> listarTodos() {
        return ResponseEntity.ok(mecanicoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mecanico> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mecanicoService.buscarPorId(id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Mecanico>> buscarPorNome(@RequestParam String nome) {
        return ResponseEntity.ok(mecanicoService.buscarPorNome(nome));
    }

    @PostMapping
    public ResponseEntity<Mecanico> salvar(@RequestBody Mecanico mecanico) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mecanicoService.salvar(mecanico));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Mecanico> atualizar(@PathVariable Long id, @RequestBody Mecanico mecanico) {
        return ResponseEntity.ok(mecanicoService.atualizar(id, mecanico));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id){
        mecanicoService.deletar(id);
        return  ResponseEntity.noContent().build();
    }
}
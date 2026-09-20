                                                                                                                                                                        package com.rionautica.loja.controller;

import com.rionautica.loja.model.Cliente;
import com.rionautica.loja.model.ClientePessoaFisica;
import com.rionautica.loja.model.ClientePessoaJuridica;
import com.rionautica.loja.services.ClienteService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public ResponseEntity<Page<Cliente>> listarTodos(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho) {
        return ResponseEntity.ok(clienteService.listarTodos(pagina, tamanho));
    }
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.buscarPorId(id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Cliente>> buscarPorCelular(@RequestParam String celular) {
        return ResponseEntity.ok(clienteService.buscarPorCelular(celular));
    }

    @PostMapping("/pf")
    public ResponseEntity<ClientePessoaFisica> cadastrarPessoaFisica(@RequestBody ClientePessoaFisica cliente) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.cadastrarPessoaFisica(cliente));
    }

    @PostMapping("/pj")
    public ResponseEntity<ClientePessoaJuridica> cadastrarPessoaJuridica(@RequestBody ClientePessoaJuridica cliente) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.cadastrarPessoaJuridica(cliente));
    }

    @PutMapping("/pf/{id}")
    public ResponseEntity<ClientePessoaFisica> atualizarPessoaFisica(
            @PathVariable Long id,
            @RequestBody ClientePessoaFisica cliente
    ) {
        return ResponseEntity.ok(clienteService.atualizarPessoaFisica(id, cliente));
    }

    @PutMapping("/pj/{id}")
    public ResponseEntity<ClientePessoaJuridica> atualizarPessoaJuridica(
            @PathVariable Long id,
            @RequestBody ClientePessoaJuridica cliente
    ) {
        return ResponseEntity.ok(clienteService.atualizarPessoaJuridica(id, cliente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        clienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
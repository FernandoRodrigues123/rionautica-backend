package com.rionautica.loja.controller;

import com.rionautica.loja.model.Agendamentos;
import com.rionautica.loja.model.Cliente;
import com.rionautica.loja.model.Veiculo;
import com.rionautica.loja.repository.ClienteRepository;
import com.rionautica.loja.services.AgendamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/agendamentos")
public class AgendamentoController {

    @Autowired
    private AgendamentoService agendamentoService;
    @Autowired
    private ClienteRepository clienteRepository;

    @PostMapping("/agendar")
    public ResponseEntity salvar(@RequestBody AgendamentoRecord agendamento){
        Cliente cliente = clienteRepository.getReferenceById(agendamento.clienteId);
        Optional<Veiculo> veiculoOptional = cliente.getVeiculos().stream().filter(v -> v.getId().equals( agendamento.veiculoId)).findFirst();
        if(veiculoOptional.isPresent()){
            Agendamentos agendamentos = new Agendamentos();
            agendamentos.setCliente(cliente);
            agendamentos.setVeiculo(veiculoOptional.get());
            agendamentos.setDescricao(agendamento.descricao);
            agendamentoService.salvar(agendamentos);
            return ResponseEntity.ok().build();
        }else{
            System.out.println("VEICULO NÃO ENCONTRADO");
            return  ResponseEntity.badRequest().body("VEICULO NÃO ENCONTRADO");
        }
    }

    @PutMapping("/cancelar/{id}")
    public ResponseEntity<AgendamentoRecord> cancelar(@PathVariable Long id){
        var ag = agendamentoService.cancelar(id);

        return ResponseEntity.ok(new AgendamentoRecord(ag.getCliente().getId(),ag.getVeiculo().getId(),ag.getDescricao()));
    }


    @GetMapping
    public List<Agendamentos> listar(){
        return agendamentoService.listar();
    }

    record AgendamentoRecord(
            Long clienteId ,
            Long veiculoId,
            String descricao
    ){
        public AgendamentoRecord(Long clienteId, Long veiculoId, String descricao) {
            this.clienteId = clienteId;
            this.veiculoId = veiculoId;
            this.descricao = descricao;
        }
    }


}

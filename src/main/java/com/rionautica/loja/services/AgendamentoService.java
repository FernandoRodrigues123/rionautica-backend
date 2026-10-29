package com.rionautica.loja.services;


import com.rionautica.loja.model.Agendamentos;
import com.rionautica.loja.model.Status;
import com.rionautica.loja.record.agendamento.AgendamentoRecord;
import com.rionautica.loja.repository.AgendamentoRepository;
import com.rionautica.loja.repository.ClienteRepository;
import com.rionautica.loja.repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class AgendamentoService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private VeiculoRepository veiculoRepository;

    public Agendamentos buscaPorId(Long id){
        return agendamentoRepository.getReferenceById(id);
    }
    public void salvar(Agendamentos agendamentos){
        agendamentos.setStatus(Status.PENDENTE);
        agendamentoRepository.save(agendamentos);
    }

    public List<Agendamentos> listar(){
        return agendamentoRepository.findAll();

    }
    public Agendamentos cancelar(Long id){
        var ag =  agendamentoRepository.findById(id).get();
        ag.setStatus(Status.CANCELADO);
        agendamentoRepository.save(ag);
        return ag;
    }

    public Agendamentos descancelar(Long id){
        var ag =  agendamentoRepository.findById(id).get();
        ag.setStatus(Status.PENDENTE);
        agendamentoRepository.save(ag);
        return ag;
    }



    public void atualizar(Long id, AgendamentoRecord agendamento) {

        var ag = agendamentoRepository.findById(id);

        if (ag.isEmpty()) {
            throw new IllegalArgumentException(
                    "Agendamento não encontrado: " + id
            );
        }

        var agendamentoBD = ag.get();

        // Verifica se o cliente foi alterado
        if (!Objects.equals(
                agendamento.clienteId(),
                agendamentoBD.getCliente().getId()
        )) {
            var clienteOptional = clienteRepository.findById(
                    agendamento.clienteId()
            );

            if (clienteOptional.isEmpty()) {
                throw new IllegalArgumentException(
                        "Cliente não encontrado: " + agendamento.clienteId()
                );
            }

            var clienteNovo = clienteOptional.get();
            agendamentoBD.setCliente(clienteNovo);

            // A troca de cliente exige um veículo pertencente ao novo cliente
            if (agendamento.veiculoId() == null) {
                throw new IllegalArgumentException(
                        "Informe um veículo para o novo cliente."
                );
            }

            var novoVeiculo = clienteNovo.getVeiculos()
                    .stream()
                    .filter(v -> Objects.equals(
                            v.getId(),
                            agendamento.veiculoId()
                    ))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Veículo não encontrado para o cliente informado."
                    ));

            agendamentoBD.setVeiculo(novoVeiculo);

        } else if (agendamento.veiculoId() != null
                && !Objects.equals(
                agendamento.veiculoId(),
                agendamentoBD.getVeiculo().getId()
        )) {

            // Troca de veículo mantendo o mesmo cliente
            var veiculoOptional = veiculoRepository.findById(
                    agendamento.veiculoId()
            );

            if (veiculoOptional.isEmpty()) {
                throw new IllegalArgumentException(
                        "Veículo não encontrado: " + agendamento.veiculoId()
                );
            }

            var novoVeiculo = veiculoOptional.get();

            // Impede associar um veículo de outro cliente
            if (!Objects.equals(
                    novoVeiculo.getCliente().getId(),
                    agendamentoBD.getCliente().getId()
            )) {
                throw new IllegalArgumentException(
                        "O veículo informado não pertence ao cliente."
                );
            }

            agendamentoBD.setVeiculo(novoVeiculo);
        }

        // Atualiza a descrição
        agendamentoBD.setDescricao(agendamento.descricao());

        agendamentoRepository.save(agendamentoBD);
    }


}

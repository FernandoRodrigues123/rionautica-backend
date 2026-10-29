package com.rionautica.loja.record.agendamento;

public record AgendamentoRecord(
        Long id,
        Long clienteId ,
        Long veiculoId,
        String descricao
){
    public AgendamentoRecord(Long id, Long clienteId, Long veiculoId, String descricao) {
        this.id = id;
        this.clienteId = clienteId;
        this.veiculoId = veiculoId;
        this.descricao = descricao;
    }
}
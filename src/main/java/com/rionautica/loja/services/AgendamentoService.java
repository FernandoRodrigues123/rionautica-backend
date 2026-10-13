package com.rionautica.loja.services;

import com.rionautica.loja.model.Agendamentos;
import com.rionautica.loja.repository.AgendamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgendamentoService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    public void salvar(Agendamentos agendamentos){
        agendamentoRepository.save(agendamentos);
    }

    public List<Agendamentos> listar(){
        return agendamentoRepository.findAll();

    }
}

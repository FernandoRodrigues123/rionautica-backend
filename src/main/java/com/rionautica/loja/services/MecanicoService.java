package com.rionautica.loja.services;

import com.rionautica.loja.model.Mecanico;
import com.rionautica.loja.repository.MecanicoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MecanicoService {

    private final MecanicoRepository mecanicoRepository;

    public MecanicoService(MecanicoRepository mecanicoRepository) {
        this.mecanicoRepository = mecanicoRepository;
    }

    public List<Mecanico> listarTodos() {
        return mecanicoRepository.findAll();
    }

    public Mecanico buscarPorId(Long id) {
        return mecanicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mecânico não encontrado: " + id));
    }

    public List<Mecanico> buscarPorNome(String nome) {
        return mecanicoRepository.findByNomeContainingIgnoreCase(nome);
    }

    public Mecanico salvar(Mecanico mecanico) {
        return mecanicoRepository.save(mecanico);
    }

    public Mecanico atualizar(Long id, Mecanico dados) {
        Mecanico mecanico = buscarPorId(id);
        mecanico.setCodigo(dados.getCodigo());
        mecanico.setNome(dados.getNome());
        return mecanicoRepository.save(mecanico);
    }

    public void deletar(Long id) {
        mecanicoRepository.delete(buscarPorId(id));
    }
}
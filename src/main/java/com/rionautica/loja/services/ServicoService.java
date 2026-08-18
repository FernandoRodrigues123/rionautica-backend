package com.rionautica.loja.service;

import com.rionautica.loja.exception.ServicoNaoEncontradoException;
import com.rionautica.loja.model.Servico;
import com.rionautica.loja.repository.ServicoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicoService {

    private final ServicoRepository servicoRepository;

    public ServicoService(ServicoRepository servicoRepository) {
        this.servicoRepository = servicoRepository;
    }

    public List<Servico> listarTodos() {
        return servicoRepository.findAll();
    }

    public Servico buscarPorId(Long id) {
        return servicoRepository.findById(id)
                .orElseThrow(() -> new ServicoNaoEncontradoException(id));
    }

    public List<Servico> buscarPorNome(String nome) {
        return servicoRepository.findByNomeContainingIgnoreCase(nome);
    }

    public Servico salvar(Servico servico) {
        return servicoRepository.save(servico);
    }

    public Servico atualizar(Long id, Servico dados) {
        Servico servico = buscarPorId(id);
        servico.setCodigo(dados.getCodigo());
        servico.setNome(dados.getNome());
        servico.setValor(dados.getValor());
        return servicoRepository.save(servico);
    }

    public void deletar(Long id) {
        servicoRepository.delete(buscarPorId(id));
    }
}
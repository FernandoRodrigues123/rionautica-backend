package com.rionautica.loja.services;

import com.rionautica.loja.exception.ClienteNaoEncontradoException;
import com.rionautica.loja.model.Cliente;
import com.rionautica.loja.model.ClientePessoaFisica;
import com.rionautica.loja.model.ClientePessoaJuridica;
import com.rionautica.loja.repository.ClienteRepository;
import com.rionautica.loja.repository.ClientePessoaFisicaRepository;
import com.rionautica.loja.repository.ClientePessoaJuridicaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClientePessoaFisicaRepository pfRepository;
    private final ClientePessoaJuridicaRepository pjRepository;

    public ClienteService(
            ClienteRepository clienteRepository,
            ClientePessoaFisicaRepository pfRepository,
            ClientePessoaJuridicaRepository pjRepository
    ) {
        this.clienteRepository = clienteRepository;
        this.pfRepository = pfRepository;
        this.pjRepository = pjRepository;
    }

    // -------------------------
    // Listagem e busca
    // -------------------------

    public Page<Cliente> listarTodos(int pagina, int tamanho) {
        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by("nome").ascending());
        return clienteRepository.findAll(pageable);
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNaoEncontradoException(id));
    }

    // -------------------------
    // Cadastro
    // -------------------------

    public ClientePessoaFisica cadastrarPessoaFisica(ClientePessoaFisica cliente) {
        if (pfRepository.existsByCpf(cliente.getCpf())) {
            throw new IllegalArgumentException("CPF já cadastrado: " + cliente.getCpf());
        }
        if (clienteRepository.existsByEmail(cliente.getEmail())) {
            throw new IllegalArgumentException("E-mail já cadastrado: " + cliente.getEmail());
        }
        return pfRepository.save(cliente);
    }

    public ClientePessoaJuridica cadastrarPessoaJuridica(ClientePessoaJuridica cliente) {
        if (pjRepository.existsByCnpj(cliente.getCnpj())) {
            throw new IllegalArgumentException("CNPJ já cadastrado: " + cliente.getCnpj());
        }
        if (clienteRepository.existsByEmail(cliente.getEmail())) {
            throw new IllegalArgumentException("E-mail já cadastrado: " + cliente.getEmail());
        }
        return pjRepository.save(cliente);
    }

    // -------------------------
    // Atualização
    // -------------------------

    public ClientePessoaFisica atualizarPessoaFisica(Long id, ClientePessoaFisica dadosAtualizados) {
        ClientePessoaFisica cliente = pfRepository.findById(id)
                .orElseThrow(() -> new ClienteNaoEncontradoException(id));

        cliente.setNome(dadosAtualizados.getNome());
        cliente.setEmail(dadosAtualizados.getEmail());
        cliente.setCelular(dadosAtualizados.getCelular());
        cliente.setEndereco(dadosAtualizados.getEndereco());
        cliente.setNumero(dadosAtualizados.getNumero());
        cliente.setCep(dadosAtualizados.getCep());
        cliente.setCidade(dadosAtualizados.getCidade());
        cliente.setEstado(dadosAtualizados.getEstado());
        cliente.setCpf(dadosAtualizados.getCpf());

        return pfRepository.save(cliente);
    }

    public ClientePessoaJuridica atualizarPessoaJuridica(Long id, ClientePessoaJuridica dadosAtualizados) {
        ClientePessoaJuridica cliente = pjRepository.findById(id)
                .orElseThrow(() -> new ClienteNaoEncontradoException(id));

        cliente.setNome(dadosAtualizados.getNome());
        cliente.setEmail(dadosAtualizados.getEmail());
        cliente.setCelular(dadosAtualizados.getCelular());
        cliente.setEndereco(dadosAtualizados.getEndereco());
        cliente.setNumero(dadosAtualizados.getNumero());
        cliente.setCep(dadosAtualizados.getCep());
        cliente.setCidade(dadosAtualizados.getCidade());
        cliente.setEstado(dadosAtualizados.getEstado());
        cliente.setCnpj(dadosAtualizados.getCnpj());
        cliente.setInscricaoEstadual(dadosAtualizados.getInscricaoEstadual());

        return pjRepository.save(cliente);
    }

    // -------------------------
    // Exclusão
    // -------------------------

    public void deletar(Long id) {
        Cliente cliente = buscarPorId(id);
        clienteRepository.delete(cliente);
    }

    public List<Cliente> buscarPorCelular(String celular) {
        return clienteRepository.findByCelularContaining(celular);
    }
}

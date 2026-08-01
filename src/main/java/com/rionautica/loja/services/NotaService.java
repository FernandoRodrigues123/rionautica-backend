package com.rionautica.loja.services;

import com.rionautica.loja.exception.NotaNaoEncontradaException;
import com.rionautica.loja.model.*;
import com.rionautica.loja.repository.ClienteRepository;
import com.rionautica.loja.repository.NotaRepository;
import com.rionautica.loja.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotaService {

    private final NotaRepository notaRepository;
    private final ProdutoRepository produtoRepository;
    private final ClienteRepository clienteRepository;

    public NotaService(
            NotaRepository notaRepository,
            ProdutoRepository produtoRepository,
            ClienteRepository clienteRepository
    ) {
        this.notaRepository = notaRepository;
        this.produtoRepository = produtoRepository;
        this.clienteRepository = clienteRepository;
    }

    // -------------------------
    // Listagem e busca
    // -------------------------

    public List<Nota> listarTodas() {
        return notaRepository.findAll();
    }

    public Nota buscarPorId(Long id) {
        return notaRepository.findById(id)
                .orElseThrow(() -> new NotaNaoEncontradaException(id));
    }

    public List<Nota> listarPorStatus(StatusPagamento status) {
        return notaRepository.findByStatusPagamento(status);
    }

    public List<Nota> listarPorCliente(Long clienteId) {
        return notaRepository.findByClienteId(clienteId);
    }

    public List<Nota> listarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return notaRepository.findByDataBetween(inicio, fim);
    }

    // -------------------------
    // Criação
    // -------------------------

    @Transactional
    public Nota criar(Long clienteId, List<ProdutoNota> itens, Double desconto) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + clienteId));

        Nota nota = new Nota();
        nota.setData(LocalDateTime.now()); // data automática do dia
        nota.setCliente(cliente);
        nota.setStatusPagamento(StatusPagamento.ABERTO); // toda nota começa aberta
        nota.setDesconto(desconto != null ? desconto : 0.0);

        for (ProdutoNota item : itens) {
            Produto produto = produtoRepository.findById(item.getProduto().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + item.getProduto().getId()));

            if (produto.getQuantidade() < item.getQuantidade()) {
                throw new IllegalStateException(
                        "Estoque insuficiente para o produto: " + produto.getNome() +
                                ". Disponível: " + produto.getQuantidade()
                );
            }

            // deduz do estoque
            produto.setQuantidade(produto.getQuantidade() - item.getQuantidade());
            produtoRepository.save(produto);

            item.setNota(nota);
            item.setValorUnitario(produto.getValor());
            nota.getProdutos().add(item);
        }

        double valorBruto = nota.getProdutos().stream()
                .mapToDouble(i -> i.getValorUnitario() * i.getQuantidade())
                .sum();

        nota.setValorBruto(valorBruto);
        nota.setValorTotal(valorBruto - nota.getDesconto());

        return notaRepository.save(nota);
    }

    // -------------------------
    // Pagamento
    // -------------------------

    @Transactional
    public Nota registrarPagamento(Long id, StatusPagamento novoStatus) {
        Nota nota = buscarPorId(id);

        if (nota.getStatusPagamento() == StatusPagamento.PAGO) {
            throw new IllegalStateException("Nota já está paga.");
        }

        nota.setStatusPagamento(novoStatus);
        return notaRepository.save(nota);
    }

    // -------------------------
    // Desconto
    // -------------------------

    @Transactional
    public Nota aplicarDesconto(Long id, Double desconto) {
        Nota nota = buscarPorId(id);

        if (nota.getStatusPagamento() == StatusPagamento.PAGO) {
            throw new IllegalStateException("Não é possível aplicar desconto em nota já paga.");
        }

        if (desconto < 0 || desconto > nota.getValorBruto()) {
            throw new IllegalArgumentException("Desconto inválido: " + desconto);
        }

        nota.setDesconto(desconto);
        nota.setValorTotal(nota.getValorBruto() - desconto);
        return notaRepository.save(nota);
    }

    // -------------------------
    // Exclusão
    // -------------------------

    @Transactional
    public void deletar(Long id) {
        Nota nota = buscarPorId(id);

        // devolve estoque ao cancelar
        for (ProdutoNota item : nota.getProdutos()) {
            Produto produto = item.getProduto();
            produto.setQuantidade(produto.getQuantidade() + item.getQuantidade());
            produtoRepository.save(produto);
        }

        notaRepository.delete(nota);
    }
    @Transactional
    public Nota atualizar(Long id, Long clienteId, List<ProdutoNota> itens, Double desconto) {
        Nota nota = buscarPorId(id);

        if (nota.getStatusPagamento() == StatusPagamento.PAGO) {
            throw new IllegalStateException("Nota já está paga e não pode ser alterada.");
        }

        // devolve estoque dos produtos antigos
        for (ProdutoNota item : nota.getProdutos()) {
            Produto produto = item.getProduto();
            produto.setQuantidade(produto.getQuantidade() + item.getQuantidade());
            produtoRepository.save(produto);
        }

        // limpa itens antigos
        nota.getProdutos().clear();

        // novo cliente
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + clienteId));
        nota.setCliente(cliente);

        // novos produtos
        for (ProdutoNota item : itens) {
            Produto produto = produtoRepository.findById(item.getProduto().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + item.getProduto().getId()));

            if (produto.getQuantidade() < item.getQuantidade()) {
                throw new IllegalStateException(
                        "Estoque insuficiente para: " + produto.getNome() +
                                ". Disponível: " + produto.getQuantidade()
                );
            }

            produto.setQuantidade(produto.getQuantidade() - item.getQuantidade());
            produtoRepository.save(produto);

            item.setNota(nota);
            item.setValorUnitario(produto.getValor());
            nota.getProdutos().add(item);
        }

        double valorBruto = nota.getProdutos().stream()
                .mapToDouble(i -> i.getValorUnitario() * i.getQuantidade())
                .sum();

        nota.setValorBruto(valorBruto);
        nota.setDesconto(desconto != null ? desconto : 0.0);
        nota.setValorTotal(valorBruto - nota.getDesconto());

        return notaRepository.save(nota);
    }

}
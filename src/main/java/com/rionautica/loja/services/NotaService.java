package com.rionautica.loja.services;

import com.rionautica.loja.exception.NotaNaoEncontradaException;
import com.rionautica.loja.model.*;
import com.rionautica.loja.repository.AgendamentoRepository;
import com.rionautica.loja.repository.ClienteRepository;
import com.rionautica.loja.repository.NotaRepository;
import com.rionautica.loja.repository.ProdutoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotaService {

    private final NotaRepository notaRepository;
    private final ProdutoRepository produtoRepository;
    private final ClienteRepository clienteRepository;
    private  final VeiculoService   veiculoService;
    private final AgendamentoRepository agendamentosRepository;
    public NotaService(
            NotaRepository notaRepository,
            ProdutoRepository produtoRepository,
            ClienteRepository clienteRepository,
            VeiculoService   veiculoService,
            AgendamentoRepository agendamentosRepository
    ) {
        this.notaRepository = notaRepository;
        this.produtoRepository = produtoRepository;
        this.clienteRepository = clienteRepository;
        this.veiculoService =   veiculoService;
        this.agendamentosRepository =   agendamentosRepository;
    }

    // -------------------------
    // Listagem e busca
    // -------------------------

    public Page<Nota> listarTodas(int pagina, int tamanho) {
        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by("data").descending());
        return notaRepository.findAll(pageable);
    }
    public Nota buscarPorId(Long id) {
        return notaRepository.findById(id)
                .orElseThrow(() -> new NotaNaoEncontradaException(id));
    }


    public Page<Nota> listarPorStatus(StatusPagamento status, int pagina, int tamanho) {
        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by("data").descending());
        return notaRepository.findByStatusPagamento(status, pageable);
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
    public Nota criar(
            Long clienteId,
            List<ProdutoNota> itens,
            List<ServicoNota> servicos,
            Double desconto,
            String observacao,
            Boolean isOrcamento,
            Long agendamentoId
    ) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Cliente não encontrado: " + clienteId
                        ));

        boolean orcamento = Boolean.TRUE.equals(isOrcamento);

        Nota nota = new Nota();
        nota.setData(LocalDateTime.now());
        nota.setCliente(cliente);
        nota.setStatusPagamento(StatusPagamento.ABERTO);
        nota.setDesconto(desconto != null ? desconto : 0.0);
        nota.setObservacao(observacao);
        nota.setIsOrcamento(orcamento);

        // Vincula o agendamento, se informado
        if (agendamentoId != null) {
            Agendamentos agendamento = agendamentosRepository.findById(agendamentoId)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Agendamento não encontrado: " + agendamentoId
                            ));

            nota.setAgendamentos(agendamento);

            // Orçamento iniciado: agendamento em andamento
            if (orcamento) {
                agendamento.setStatus(Status.EM_ANDAMENTO);
                agendamentosRepository.save(agendamento);
            }
        }

        if (itens != null) {
            for (ProdutoNota item : itens) {
                Long produtoId = item.getProduto().getId();

                Produto produto = produtoRepository.findById(produtoId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Produto não encontrado: " + produtoId
                                ));

                // Não desconta estoque ao criar orçamento
                if (!orcamento) {
                    if (produto.getQuantidade() < item.getQuantidade()) {
                        throw new IllegalStateException(
                                "Estoque insuficiente para: " + produto.getNome()
                                        + ". Disponível: " + produto.getQuantidade()
                        );
                    }

                    produto.setQuantidade(
                            produto.getQuantidade() - item.getQuantidade()
                    );
                    produtoRepository.save(produto);
                }

                item.setNota(nota);
                item.setValorUnitario(produto.getValor());
                nota.getProdutos().add(item);
            }
        }

        if (servicos != null) {
            for (ServicoNota item : servicos) {
                item.setNota(nota);
                nota.getServicos().add(item);
            }
        }

        double valorBruto = nota.getProdutos().stream()
                .mapToDouble(i ->
                        i.getValorUnitario() * i.getQuantidade())
                .sum()
                + nota.getServicos().stream()
                .mapToDouble(i ->
                        i.getValorUnitario() * i.getQuantidade())
                .sum();

        nota.setValorBruto(valorBruto);
        nota.setValorTotal(valorBruto - nota.getDesconto());

        Nota salva = notaRepository.save(nota);

        if (!orcamento) {
            veiculoService.processarServicosDaNota(salva);

            // Se foi criada diretamente como nota, conclui o agendamento
            if (salva.getAgendamentos() != null) {
                Agendamentos agendamento = salva.getAgendamentos();
                agendamento.setStatus(Status.REALIZADO);
                agendamentosRepository.save(agendamento);
            }
        }

        return salva;
    }
    @Transactional
    public Nota converterParaNota(Long id) {
        Nota nota = notaRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Nota não encontrada: " + id
                        ));

        if (!Boolean.TRUE.equals(nota.getIsOrcamento())) {
            throw new IllegalStateException(
                    "Esta nota já não é um orçamento."
            );
        }

        // Confere e desconta o estoque durante a conversão
        for (ProdutoNota item : nota.getProdutos()) {
            Long produtoId = item.getProduto().getId();

            Produto produto = produtoRepository.findById(produtoId)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Produto não encontrado: " + produtoId
                            ));

            if (produto.getQuantidade() < item.getQuantidade()) {
                throw new IllegalStateException(
                        "Estoque insuficiente para: " + produto.getNome()
                                + ". Disponível: " + produto.getQuantidade()
                );
            }

            produto.setQuantidade(
                    produto.getQuantidade() - item.getQuantidade()
            );
            produtoRepository.save(produto);
        }

        nota.setIsOrcamento(false);
        nota.setData(LocalDateTime.now());

        Nota salva = notaRepository.save(nota);

        // Processa os serviços da nota convertida
        veiculoService.processarServicosDaNota(salva);

        // Finaliza o agendamento associado
        if (salva.getAgendamentos() != null) {
            Agendamentos agendamento = salva.getAgendamentos();
            agendamento.setStatus(Status.REALIZADO);
            agendamentosRepository.save(agendamento);
        }

        return salva;
    }
    @Transactional
    public Nota atualizar(Long id, Long clienteId, List<ProdutoNota> itens,
                          List<ServicoNota> servicos, Double desconto,
                          String observacao) {

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

        nota.getProdutos().clear();
        nota.getServicos().clear();

        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + clienteId));
        nota.setCliente(cliente);
        nota.setObservacao(observacao);
        nota.setDesconto(desconto != null ? desconto : 0.0);

        // novos produtos
        if (itens != null) {
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
        }

        // novos serviços
        if (servicos != null) {
            for (ServicoNota item : servicos) {
                item.setNota(nota);
                nota.getServicos().add(item);
            }
        }

        double valorBruto = nota.getProdutos().stream()
                .mapToDouble(i -> i.getValorUnitario() * i.getQuantidade())
                .sum()
                + nota.getServicos().stream()
                .mapToDouble(i -> i.getValorUnitario() * i.getQuantidade())
                .sum();

        nota.setValorBruto(valorBruto);
        nota.setValorTotal(valorBruto - nota.getDesconto());

        Nota salva = notaRepository.save(nota);
        veiculoService.processarServicosDaNota(salva);

        return salva;
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
        var ag = nota.getAgendamentos();
        ag.setStatus(Status.CANCELADO);
        agendamentosRepository.save(ag);
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
    public record CriarNotaRequest(
            Long clienteId,
            List<ProdutoNota> itens,
            List<ServicoNota> servicos,
            Double desconto,
            String observacao,
            Boolean isOrcamento,
            Long agendamentoId
    ) {
    }
}
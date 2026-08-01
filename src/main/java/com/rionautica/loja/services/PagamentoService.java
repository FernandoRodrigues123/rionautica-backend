package com.rionautica.loja.services;

import com.rionautica.loja.exception.NotaNaoEncontradaException;
import com.rionautica.loja.model.*;
import com.rionautica.loja.repository.NotaRepository;
import com.rionautica.loja.repository.PagamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final NotaRepository notaRepository;

    public PagamentoService(
            PagamentoRepository pagamentoRepository,
            NotaRepository notaRepository
    ) {
        this.pagamentoRepository = pagamentoRepository;
        this.notaRepository = notaRepository;
    }

    public List<Pagamento> listarPorNota(Long notaId) {
        return pagamentoRepository.findByNotaId(notaId);
    }

    @Transactional
    public Pagamento registrar(Long notaId, Double valor, MetodoPagamento metodo, String conta) {
        Nota nota = notaRepository.findById(notaId)
                .orElseThrow(() -> new NotaNaoEncontradaException(notaId));

        if (nota.getStatusPagamento() == StatusPagamento.PAGO) {
            throw new IllegalStateException("Nota já está totalmente paga.");
        }

        double saldoRestante = nota.getValorTotal() - nota.getValorPago();

        if (valor <= 0) {
            throw new IllegalArgumentException("Valor do pagamento deve ser maior que zero.");
        }

        if (valor > saldoRestante) {
            throw new IllegalArgumentException(
                    "Valor informado (" + valor + ") é maior que o saldo restante (" + saldoRestante + ")."
            );
        }

        // registra pagamento
        Pagamento pagamento = new Pagamento();
        pagamento.setNota(nota);
        pagamento.setValor(valor);
        pagamento.setMetodo(metodo);
        pagamento.setConta(conta);
        pagamento.setData(LocalDateTime.now());
        pagamentoRepository.save(pagamento);

        // atualiza valor pago e status da nota
        double novoValorPago = nota.getValorPago() + valor;
        nota.setValorPago(novoValorPago);

        if (novoValorPago >= nota.getValorTotal()) {
            nota.setStatusPagamento(StatusPagamento.PAGO);
        } else {
            nota.setStatusPagamento(StatusPagamento.PARCIAL);
        }

        notaRepository.save(nota);

        return pagamento;
    }

    @Transactional
    public Pagamento atualizar(Long pagamentoId, Double novoValor, MetodoPagamento novoMetodo, String novaConta) {
        Pagamento pagamento = pagamentoRepository.findById(pagamentoId)
                .orElseThrow(() -> new IllegalArgumentException("Pagamento não encontrado: " + pagamentoId));

        Nota nota = pagamento.getNota();

        if (nota.getStatusPagamento() == StatusPagamento.PAGO) {
            // mesmo paga, permite corrigir — vai recalcular depois
        }

        // remove o valor antigo do total pago
        double valorPagoAtualizado = nota.getValorPago() - pagamento.getValor() + novoValor;

        if (novoValor <= 0) {
            throw new IllegalArgumentException("Valor deve ser maior que zero.");
        }

        if (valorPagoAtualizado > nota.getValorTotal()) {
            throw new IllegalArgumentException(
                    "Valor informado ultrapassa o total da nota. Saldo restante: " +
                            (nota.getValorTotal() - (nota.getValorPago() - pagamento.getValor()))
            );
        }

        pagamento.setValor(novoValor);
        pagamento.setMetodo(novoMetodo);
        pagamento.setConta(novaConta);
        pagamentoRepository.save(pagamento);

        recalcularNota(nota, valorPagoAtualizado);

        return pagamento;
    }

    @Transactional
    public void remover(Long pagamentoId) {
        Pagamento pagamento = pagamentoRepository.findById(pagamentoId)
                .orElseThrow(() -> new IllegalArgumentException("Pagamento não encontrado: " + pagamentoId));

        Nota nota = pagamento.getNota();
        double valorPagoAtualizado = nota.getValorPago() - pagamento.getValor();

        pagamentoRepository.delete(pagamento);

        recalcularNota(nota, valorPagoAtualizado);
    }

// -------------------------
// Auxiliar
// -------------------------

    private void recalcularNota(Nota nota, double novoValorPago) {
        nota.setValorPago(Math.max(novoValorPago, 0));

        if (nota.getValorPago() <= 0) {
            nota.setStatusPagamento(StatusPagamento.ABERTO);
        } else if (nota.getValorPago() >= nota.getValorTotal()) {
            nota.setStatusPagamento(StatusPagamento.PAGO);
        } else {
            nota.setStatusPagamento(StatusPagamento.PARCIAL);
        }

        notaRepository.save(nota);
    }
}
package com.rionautica.loja.services;

import com.rionautica.loja.exception.ProdutoNaoEncontradoException;
import com.rionautica.loja.model.Produto;
import com.rionautica.loja.repository.ProdutoRepository;
import com.rionautica.loja.util.Normalizador;
import com.rionautica.loja.util.Trigrama;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public Produto salvar(Produto produto) {
        return produtoRepository.save(produto);
    }

    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ProdutoNaoEncontradoException(id));
    }

    public Produto atualizar(Long id, Produto produtoAtualizado) {
        Produto produto = buscarPorId(id);
        produto.setCodigo(produtoAtualizado.getCodigo());
        produto.setNome(produtoAtualizado.getNome());
        produto.setDescricao(produtoAtualizado.getDescricao());
        produto.setMarca(produtoAtualizado.getMarca());
        produto.setQuantidade(produtoAtualizado.getQuantidade());
        produto.setQuantidadeMinima(produtoAtualizado.getQuantidadeMinima());
        produto.setValor(produtoAtualizado.getValor());
        return produtoRepository.save(produto);
    }

    public void deletar(Long id) {
        Produto produto = buscarPorId(id);
        produtoRepository.delete(produto);
    }

    public List<Produto> buscarInteligente(String termoBusca) {
        List<String> tokensBusca = Normalizador.tokenizar(termoBusca);

        if (tokensBusca.isEmpty()) return List.of();

        List<Produto> candidatos = produtoRepository.findAll();

        return candidatos.stream()
                .map(p -> new ProdutoComScore(p, calcularScore(p, tokensBusca)))
                .filter(pc -> pc.score() > 0)
                .sorted(Comparator.comparingDouble(ProdutoComScore::score).reversed())
                .map(ProdutoComScore::produto)
                .toList();
    }


    private double calcularScore(Produto p, List<String> tokensBusca) {
        String textoProduto = Normalizador.normalizar(
                (p.getNome() != null ? p.getNome() : "") + " " +
                        (p.getDescricao() != null ? p.getDescricao() : "") + " " +
                        (p.getCodigo() != null ? p.getCodigo() : "")
        );
        List<String> tokensProduto = Arrays.asList(textoProduto.split(" "));

        double score = 0;
        for (String tokenBusca : tokensBusca) {
            double melhorPontuacaoToken = 0;

            for (String tokenProduto : tokensProduto) {
                double pontuacao;

                if (tokenProduto.equals(tokenBusca)) {
                    pontuacao = 3.0; // match exato
                } else if (tokenProduto.contains(tokenBusca) || tokenBusca.contains(tokenProduto)) {
                    pontuacao = 2.0; // um token contém o outro (ex: "agu" em "agua")
                } else {
                    pontuacao = Trigrama.similaridade(tokenProduto, tokenBusca) * 2.0; // parecença estrutural
                }

                melhorPontuacaoToken = Math.max(melhorPontuacaoToken, pontuacao);
            }

            score += melhorPontuacaoToken > 0.4 ? melhorPontuacaoToken : -0.5; // penaliza token sem nenhum match razoável
        }
        return score;
    }

    private record ProdutoComScore(Produto produto, double score) {}
}
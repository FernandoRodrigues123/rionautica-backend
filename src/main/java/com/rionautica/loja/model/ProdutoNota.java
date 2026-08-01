package com.rionautica.loja.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "produto_nota")
@Getter
@Setter
@NoArgsConstructor
public class ProdutoNota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "nota_id")
    @JsonIgnoreProperties("produtos")
    private Nota nota;

    @ManyToOne(optional = false)
    @JoinColumn(name = "produto_id")
    @JsonIgnoreProperties({"descricao", "quantidade", "quantidadeMinima"})
    private Produto produto;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false)
    private Double valorUnitario;
}
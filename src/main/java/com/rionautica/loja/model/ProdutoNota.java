package com.rionautica.loja.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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
    private Produto produto;


    private Integer quantidade;
    private Double valorUnitario;
    private String descricao;
}
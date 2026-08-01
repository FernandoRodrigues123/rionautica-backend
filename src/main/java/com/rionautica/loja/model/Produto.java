package com.rionautica.loja.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table
@AllArgsConstructor

@Getter
@Setter
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String codigo;
    private String nome;
    private String descricao;
    @Enumerated(EnumType.STRING)
    private Marca marca;
    private Integer quantidade;
    private Integer quantidadeMinima;
    private Double valor;

    public Produto(){}
}

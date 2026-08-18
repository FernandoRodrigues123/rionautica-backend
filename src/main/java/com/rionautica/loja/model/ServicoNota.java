package com.rionautica.loja.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "servico_nota")
@Getter
@Setter
@NoArgsConstructor
public class ServicoNota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "nota_id")
    @JsonIgnoreProperties("servicos")
    private Nota nota;

    @ManyToOne(optional = false)
    @JoinColumn(name = "servico_id")
    private Servico servico;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false)
    private Double valorUnitario;

    @Column
    private String descricao;
}
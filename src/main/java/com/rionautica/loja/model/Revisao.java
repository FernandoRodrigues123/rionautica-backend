package com.rionautica.loja.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "revisao")
@Getter
@Setter
@NoArgsConstructor
public class Revisao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "veiculo_id")
    @JsonIgnoreProperties("revisoes")
    private Veiculo veiculo;

    @ManyToOne
    @JoinColumn(name = "nota_id")
    private Nota nota;

    @ManyToOne
    @JoinColumn(name = "mecanico_id")
    private Mecanico mecanico;

    @Column(nullable = false)
    private LocalDateTime dataRealizacao;

    @Column
    private Double horasNaRevisao;

    @Column
    private LocalDateTime proximaRevisaoData;

    @Column
    private Double proximaRevisaoHoras;

    @Column
    private String observacao;
}
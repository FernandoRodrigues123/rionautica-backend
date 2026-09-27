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
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nota_id")
    @JsonIgnoreProperties("servicos")
    private Nota nota;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "servico_id")
    private Servico servico;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "mecanico_id")
    @JsonIgnoreProperties("hibernateLazyInitializer")
    private Mecanico mecanico;


    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false)
    private Double valorUnitario;

    @Column(length = 500)
    private String descricao;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "veiculo_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Veiculo veiculo;

    @Column
    private Double horasVeiculo; // horas do veículo no momento do serviço
}
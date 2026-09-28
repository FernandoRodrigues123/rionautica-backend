package com.rionautica.loja.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rionautica.loja.model.Cliente;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "veiculo")
@Getter
@Setter
@NoArgsConstructor
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id")
    @JsonIgnoreProperties("veiculos")
    private Cliente cliente;

    // campos flexíveis — todos opcionais exceto nome
    @Column(nullable = false)
    private String nome; // ex: "Apache 19", "JET GTI 130", "Motor Yamaha 25HP"

    @Column
    private String descricao;

    @Column
    private String marca;

    @Column
    private String modelo;

    @Column
    private String ano;

    @Column
    private String chassi;

    @Column
    private Double horasAtuais;

    // intervalo de revisão configurável por veículo
    @Column
    private Double intervaloHoras;

    @Column
    private Integer intervaloMeses;

    @Column(nullable = false)
    private Boolean revisaoEmDia = true;

    @OneToMany(mappedBy = "veiculo", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"veiculo", "nota"})
    private List<ServicoVeiculo> servicos = new ArrayList<>();

    @OneToMany(mappedBy = "veiculo", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"veiculo", "nota"})
    private List<Revisao> revisoes = new ArrayList<>();
}
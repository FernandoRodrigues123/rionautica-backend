package com.rionautica.loja.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "nota")
@Getter
@Setter
@NoArgsConstructor
public class Nota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime data;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @OneToMany(mappedBy = "nota", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("nota")
    private List<ProdutoNota> produtos = new ArrayList<>();

    @OneToMany(mappedBy = "nota", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("nota")
    private List<ServicoNota> servicos = new ArrayList<>();
    @Column(nullable = false)
    private Double valorBruto;

    @Column(nullable = false)
    private Double desconto;

    @Column(nullable = false)
    private Double valorTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPagamento statusPagamento;

    @OneToMany(mappedBy = "nota", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("nota")
    private List<Pagamento> pagamentos = new ArrayList<>();

    // campo auxiliar para controle
    @Column(nullable = false)
    private Double valorPago = 0.0;
    @Column(columnDefinition = "TEXT")
    private String observacao;
}
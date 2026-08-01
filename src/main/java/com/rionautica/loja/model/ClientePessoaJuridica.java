package com.rionautica.loja.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cliente_pessoa_juridica")
@DiscriminatorValue("PJ")
@Getter
@Setter
@NoArgsConstructor
public class ClientePessoaJuridica extends Cliente {

    @Column(nullable = false, unique = true, length = 14)
    private String cnpj;

    @Column(name = "inscricao_estadual")
    private String inscricaoEstadual;
}
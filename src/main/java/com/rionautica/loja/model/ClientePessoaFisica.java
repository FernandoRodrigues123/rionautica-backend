package com.rionautica.loja.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cliente_pessoa_fisica")
@DiscriminatorValue("PF")
@Getter
@Setter
@NoArgsConstructor
public class ClientePessoaFisica extends Cliente {

    @Column(nullable = false, unique = true, length = 11)
    private String cpf;
}
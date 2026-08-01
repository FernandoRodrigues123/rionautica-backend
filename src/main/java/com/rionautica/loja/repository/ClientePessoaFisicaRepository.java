package com.rionautica.loja.repository;

import com.rionautica.loja.model.ClientePessoaFisica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientePessoaFisicaRepository extends JpaRepository<ClientePessoaFisica, Long> {

    boolean existsByCpf(String cpf);

    java.util.Optional<ClientePessoaFisica> findByCpf(String cpf);
}
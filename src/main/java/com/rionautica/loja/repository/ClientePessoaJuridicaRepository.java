package com.rionautica.loja.repository;

import com.rionautica.loja.model.ClientePessoaJuridica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientePessoaJuridicaRepository extends JpaRepository<ClientePessoaJuridica, Long> {

    boolean existsByCnpj(String cnpj);

    java.util.Optional<ClientePessoaJuridica> findByCnpj(String cnpj);
}
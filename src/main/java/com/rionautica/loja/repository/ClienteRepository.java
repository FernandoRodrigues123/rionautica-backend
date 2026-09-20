package com.rionautica.loja.repository;

import com.rionautica.loja.model.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByEmail(String email);
    List<Cliente> findByCelularContaining(String celular);
    Page<Cliente> findAll(Pageable pageable);
}
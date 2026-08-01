package com.rionautica.loja.repository;


import com.rionautica.loja.model.Nota;
import com.rionautica.loja.model.StatusPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotaRepository extends JpaRepository<Nota, Long> {
    List<Nota> findByStatusPagamento(StatusPagamento status);
    List<Nota> findByClienteId(Long clienteId);
    List<Nota> findByDataBetween(LocalDateTime inicio, LocalDateTime fim);
}
package com.rionautica.loja.repository;

import com.rionautica.loja.model.Mecanico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MecanicoRepository extends JpaRepository<Mecanico, Long> {
    List<Mecanico> findByNomeContainingIgnoreCase(String nome);
}
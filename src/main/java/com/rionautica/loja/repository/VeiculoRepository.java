package com.rionautica.loja.repository;

import com.rionautica.loja.model.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {
    List<Veiculo> findByClienteId(Long clienteId);
    List<Veiculo> findByRevisaoEmDiaFalse();
}
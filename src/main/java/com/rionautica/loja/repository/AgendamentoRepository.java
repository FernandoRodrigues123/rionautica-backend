package com.rionautica.loja.repository;

import com.rionautica.loja.model.Agendamentos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamentos, Long> {
}

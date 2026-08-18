package com.rionautica.loja.repository;

import com.rionautica.loja.model.Servico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicoRepository extends JpaRepository<Servico,Long> {

    List<Servico> findByNomeContainingIgnoreCase(String nome);
    List<Servico> findByCodigoContainingIgnoreCase(String codigo);
}

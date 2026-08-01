package com.rionautica.loja.repository;
import com.rionautica.loja.model.ProdutoNota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdutoNotaRepository extends JpaRepository<ProdutoNota, Long> {
}
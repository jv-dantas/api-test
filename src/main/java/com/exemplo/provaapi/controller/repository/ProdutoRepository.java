package com.exemplo.provaapi.controller.repository;

import com.exemplo.provaapi.controller.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}

package com.exemplo.provaapi.controller;

import com.exemplo.provaapi.controller.model.Produto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class testarcriacaoproduto {


    @Test
    void testarCriacaoProduto() {
        Produto produto = new Produto();
        produto.setNome("Mesa");
        produto.setPreco(500.0);

        assertNotNull(produto);
        assertEquals("Mesa", produto.getNome());
    }


}




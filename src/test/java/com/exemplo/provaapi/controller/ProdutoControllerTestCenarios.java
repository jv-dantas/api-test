package com.exemplo.provaapi.controller;

import com.exemplo.provaapi.controller.controller.ProdutoController;
import com.exemplo.provaapi.controller.model.Produto;
import com.exemplo.provaapi.controller.service.ProdutoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProdutoController.class)
public class ProdutoControllerTestCenarios {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProdutoService produtoService;

    // Cenário de Sucesso
    @Test
    void listarTodos_deveRetornarListaDeProdutos() throws Exception {
        Produto produto = new Produto();
        produto.setId(1L);
        produto.setNome("Produto Teste");
        produto.setPreco(100.0);

        when(produtoService.listarTodos()).thenReturn(Collections.singletonList(produto));

        mockMvc.perform(get("/api/produtos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].nome").value("Produto Teste"));
    }

    // Cenário de Borda: Lista de produtos vazia
    @Test
    void listarTodos_listaVaziaDeveRetornarStatusOk() throws Exception {
        when(produtoService.listarTodos()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/produtos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty());
    }

}

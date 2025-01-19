package com.exemplo.provaapi.controller;

import com.exemplo.provaapi.model.Produto;
import com.exemplo.provaapi.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
@Tag(name = "Produtos", description = "Endpoints para gerenciamento de produtos")
public class ProdutoController {
    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    @Operation(summary = "Listar todos os produtos", description = "Retorna uma lista de todos os produtos cadastrados")
    public List<Produto> listarTodos() {
        return produtoService.listarTodos();
    }

    @PostMapping
    @Operation(summary = "Criar um novo produto", description = "Adiciona um produto ao sistema")
    public Produto criarProduto(@RequestBody Produto produto) {
        return produtoService.criarProduto(produto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um produto", description = "Atualiza os detalhes de um produto existente")
    public ResponseEntity<Produto> atualizarProduto(@PathVariable Long id, @RequestBody Produto produto) {
        return produtoService.atualizarProduto(id, produto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir um produto", description = "Remove um produto do sistema pelo ID")
    public ResponseEntity<Void> deletarProduto(@PathVariable Long id) {
        if (produtoService.deletarProduto(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

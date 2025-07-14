package Cadastro.mercadoria.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Cadastro.mercadoria.DTOs.ProdutoDTO;
import Cadastro.mercadoria.models.Produto;
import Cadastro.mercadoria.services.ProdutoService;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @GetMapping(path = { "/teste" })
    public String teste() {
        return "Funcionou";
    }

    @PostMapping("/inserir")
    public ResponseEntity<?> criarProduto(@RequestBody ProdutoDTO produtoDTO) {
        try {
            Produto novoProduto = produtoService.salvarProduto(produtoDTO); // Chama o método do service
            return ResponseEntity.status(HttpStatus.CREATED).body(novoProduto); // Sucesso: 201 Created
        } catch (IllegalArgumentException e) {
            // Captura exceções de validação de negócio lançadas pelo service
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); // Ex: quantidade negativa
        } catch (RuntimeException e) {
            // Captura RuntimeExceptions genéricas do service (incluindo
            // DataIntegrityViolationException relançadas)
            // Logar a stack trace da exceção original é crucial para depuração
            e.printStackTrace(); // Imprime a stack trace no console para você
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao criar o produto. Verifique os dados informados.");
        } catch (Exception e) {
            // Captura qualquer outra exceção inesperada
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocorreu um erro inesperado ao criar o produto.");
        }
    }

    @PutMapping("/alterar/{id}")
    public ResponseEntity<?> alterarProduto(@PathVariable String id, @RequestBody ProdutoDTO produtoDTO) {

        try {
            Produto produtoAtualizado = produtoService.alterarProduto(id, produtoDTO);
            return ResponseEntity.ok(produtoAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/buscar/{id}")
    public Produto buscarProduto(@PathVariable String id) {
        return produtoService.buscarProduto(id);
    }

    @GetMapping("/listar")
    public List<Produto> listarProdutos() {
        return produtoService.listarProdutos();
    }

    @DeleteMapping("/deletar/{id}")
    public void deletarProduto(@PathVariable String id) {
        produtoService.deletarProduto(id);
    }

}

package Cadastro.mercadoria.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import Cadastro.mercadoria.DTOs.ProdutoDTO;
import Cadastro.mercadoria.models.Produto;
import Cadastro.mercadoria.repositories.ProdutoRepository;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    /**
     * Para receber imagem no produto, precisa implemtar o FileStorageService
     * 
     * @Autowired
     *            private FileStorageService fileStorageService; // Injeta o serviço
     *            de armazenamento
     * 
     *            public ProdutoService(ProdutoRepository produtoRepository,
     *            FileStorageService fileStorageService) {
     *            this.produtoRepository = produtoRepository;
     *            this.fileStorageService = fileStorageService;
     *            }
     */

    public Produto salvarProduto(ProdutoDTO produtoDTO) {
        if (produtoRepository.existsByNome(produtoDTO.getNome())) {
            // Lança uma exceção se o nome já existir
            throw new IllegalArgumentException("Já existe um produto com o nome '" + produtoDTO.getNome() + "'.");
        }

        // Mapear DTO para Entidade
        Produto produto = new Produto();
        produto.setNome(produtoDTO.getNome());
        produto.setPreco(produtoDTO.getPreco());
        produto.setDescricao(produtoDTO.getDescricao());
        produto.setQuantidade(produtoDTO.getQuantidade());

        try {
            return produtoRepository.save(produto);
        } catch (DataIntegrityViolationException e) {
            // Este catch ainda é importante se você tiver uma UNIQUE constraint no banco de
            // dados
            // para o campo 'nome'. Isso atua como uma segunda linha de defesa.
            throw new RuntimeException(
                    "Erro ao salvar produto: violação de dados ou nome duplicado. Detalhes: " + e.getMessage(), e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Ocorreu um erro inesperado ao salvar o produto.", e);
        }
    }

    public Produto alterarProduto(String id, ProdutoDTO produtoDTO) {
        if (!produtoRepository.existsById(id)) {
            throw new RuntimeException("Produto com ID " + id + " não encontrado.");
        }
        Produto produto = buscarProduto(id);
        produto.setNome(produtoDTO.getNome());
        produto.setPreco(produtoDTO.getPreco());
        produto.setDescricao(produtoDTO.getDescricao());
        produto.setQuantidade(produtoDTO.getQuantidade());

        return produtoRepository.save(produto);
    }

    public Produto buscarProduto(String id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto com ID " + id + " não encontrado."));
    }

    public List<Produto> listarProdutos() {
        return produtoRepository.findAll();
    }

    public void deletarProduto(String id) {
        if (produtoRepository.existsById(id)) {
            produtoRepository.deleteById(id);
        } else {
            throw new RuntimeException("Produto com ID " + id + " não encontrado.");
        }
    }
}
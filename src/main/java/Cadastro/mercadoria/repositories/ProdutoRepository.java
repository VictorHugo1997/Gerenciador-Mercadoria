package Cadastro.mercadoria.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import Cadastro.mercadoria.models.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, String> {

    boolean existsByNome(String nome);

}

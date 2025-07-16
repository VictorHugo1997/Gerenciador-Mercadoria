package Cadastro.mercadoria;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import Cadastro.mercadoria.DTOs.ProdutoDTO;
import Cadastro.mercadoria.models.Produto;
import Cadastro.mercadoria.repositories.ProdutoRepository;
import Cadastro.mercadoria.services.ProdutoService;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ProdutoService produtoService;

    // Entidade Produto para simular retornos do repositório ou produtos existentes
    private Produto produtoExistente1;
    private Produto produtoExistente2;

    // DTOs para simular as entradas dos métodos do serviço
    private ProdutoDTO produtoDTOValidoParaCriar;
    private ProdutoDTO produtoDTONomeDuplicadoParaCriar;
    private ProdutoDTO produtoDTOParaAtualizar;

    @BeforeEach
    void setUp() {
        // Produtos existentes no "banco de dados" (mocks do repositório)
        produtoExistente1 = new Produto("Samsung Galaxy S23", "Smartphone X", null, 1999.99, 50, null);
        produtoExistente2 = new Produto("prod-id-2", "Smartwatch Z", "Um relógio inteligente", 800.00, 30, null);

        // DTOs que seriam recebidos pelo serviço
        produtoDTOValidoParaCriar = new ProdutoDTO("Laptop Y", 3500.00, "Um notebook para produtividade", 20);
        produtoDTONomeDuplicadoParaCriar = new ProdutoDTO("Smartphone X", 2000.00, "Outro smartphone", 10); // Nome
                                                                                                            // duplicado
                                                                                                            // do
                                                                                                            // produtoExistente1
        produtoDTOParaAtualizar = new ProdutoDTO("Smartphone Xtreme", 2500.00, "Modelo aprimorado", 60);
    }

    // --- Testes para salvarProduto (recebe ProdutoDTO) ---
    @Test
    @DisplayName("Deve salvar um produto com sucesso quando o nome não existe")
    void deveSalvarProdutoComSucesso() {
        // Cenário: Nome do produto não existe no repositório
        when(produtoRepository.existsByNome(produtoDTOValidoParaCriar.getNome())).thenReturn(false);

        // Cenário: o método save retorna o produto salvo com um ID (simulado)
        // Note que o `save` do repositório recebe e retorna a ENTIDADE `Produto`
        when(produtoRepository.save(any(Produto.class))).thenAnswer(invocation -> {
            Produto p = invocation.getArgument(0); // Captura o argumento Produto passado para save
            p.setId("new-uuid-123"); // Simula a atribuição de um ID pelo banco de dados
            return p;
        });

        // Chama o método do serviço, passando o DTO
        Produto resultado = produtoService.salvarProduto(produtoDTOValidoParaCriar);

        // Verificações
        assertNotNull(resultado);
        assertNotNull(resultado.getId());
        assertEquals("new-uuid-123", resultado.getId());
        assertEquals("Laptop Y", resultado.getNome());
        assertEquals(3500.00, resultado.getPreco());
        assertEquals(20, resultado.getQuantidade());

        // Verifica se existsByNome foi chamado com o nome do DTO
        verify(produtoRepository, times(1)).existsByNome(produtoDTOValidoParaCriar.getNome());
        // Verifica se save foi chamado com uma instância de Produto (mapeada do DTO)
        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException ao tentar salvar produto com nome duplicado")
    void deveLancarExcecaoAoSalvarProdutoComNomeDuplicado() {
        // Cenário: Nome do produto já existe no repositório
        when(produtoRepository.existsByNome(produtoDTONomeDuplicadoParaCriar.getNome())).thenReturn(true);

        // Verifica se a exceção esperada é lançada
        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> produtoService.salvarProduto(produtoDTONomeDuplicadoParaCriar), // Passando o DTO
                "Esperava-se IllegalArgumentException para nome duplicado");

        // Verifica a mensagem da exceção
        assertTrue(thrown.getMessage()
                .contains("Já existe um produto com o nome '" + produtoDTONomeDuplicadoParaCriar.getNome() + "'."));
        // Verifica se o método save NÃO foi chamado
        verify(produtoRepository, never()).save(any(Produto.class));
        verify(produtoRepository, times(1)).existsByNome(produtoDTONomeDuplicadoParaCriar.getNome());
    }

    @Test
    @DisplayName("Deve lançar RuntimeException para erro de violação de dados ao salvar")
    void deveLancarRuntimeExceptionPorViolacaoDeDadosAoSalvar() {
        when(produtoRepository.existsByNome(anyString())).thenReturn(false);
        // Simula uma DataIntegrityViolationException ao tentar salvar uma Entidade
        // Produto
        when(produtoRepository.save(any(Produto.class)))
                .thenThrow(new DataIntegrityViolationException("Erro de constraint no DB"));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> produtoService.salvarProduto(produtoDTOValidoParaCriar), // Passando o DTO
                "Esperava-se RuntimeException para violação de dados");

        assertTrue(thrown.getMessage().contains("violação de dados ou nome duplicado"));
        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    @Test
    @DisplayName("Deve lançar RuntimeException para erro inesperado ao salvar")
    void deveLancarRuntimeExceptionParaErroInesperadoAoSalvar() {
        when(produtoRepository.existsByNome(anyString())).thenReturn(false);
        // Simula uma exceção genérica ao tentar salvar uma Entidade Produto
        when(produtoRepository.save(any(Produto.class))).thenThrow(new RuntimeException("Erro genérico de banco"));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> produtoService.salvarProduto(produtoDTOValidoParaCriar), // Passando o DTO
                "Esperava-se RuntimeException para erro inesperado");

        assertTrue(thrown.getMessage().contains("Ocorreu um erro inesperado ao salvar o produto"));
        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    // --- Testes para alterarProduto (recebe String id, ProdutoDTO produtoDTO) ---
    @Test
    @DisplayName("Deve alterar um produto existente com sucesso")
    void deveAlterarProdutoComSucesso() {
        String idParaAtualizar = produtoExistente1.getId();

        // Mocks:
        // 1. Verificar se o produto existe pelo ID (existsById)
        when(produtoRepository.existsById(idParaAtualizar)).thenReturn(true);
        // 2. Buscar o produto pelo ID (findById, usado internamente por buscarProduto)
        when(produtoRepository.findById(idParaAtualizar)).thenReturn(Optional.of(produtoExistente1));
        // 3. Salvar o produto com as alterações
        // O método save recebe a ENTIDADE Produto, não o DTO
        when(produtoRepository.save(any(Produto.class))).thenAnswer(invocation -> {
            // Simula o produto salvo com as novas informações do DTO
            Produto p = invocation.getArgument(0);
            p.setNome(produtoDTOParaAtualizar.getNome());
            p.setPreco(produtoDTOParaAtualizar.getPreco());
            p.setDescricao(produtoDTOParaAtualizar.getDescricao());
            p.setQuantidade(produtoDTOParaAtualizar.getQuantidade());
            return p;
        });

        Produto resultado = produtoService.alterarProduto(idParaAtualizar, produtoDTOParaAtualizar); // Passando o DTO

        assertNotNull(resultado);
        assertEquals(idParaAtualizar, resultado.getId());
        assertEquals("Smartphone Xtreme", resultado.getNome()); // Verifica se o nome foi atualizado
        assertEquals(2500.00, resultado.getPreco());
        assertEquals(60, resultado.getQuantidade());

        verify(produtoRepository, times(1)).existsById(idParaAtualizar);
        verify(produtoRepository, times(1)).findById(idParaAtualizar);
        verify(produtoRepository, times(1)).save(any(Produto.class)); // O save recebe o Produto mapeado
    }

    @Test
    @DisplayName("Deve lançar RuntimeException ao tentar alterar produto inexistente")
    void deveLancarExcecaoAoAlterarProdutoInexistente() {
        String idInexistente = "id-inexistente";
        when(produtoRepository.existsById(idInexistente)).thenReturn(false);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> produtoService.alterarProduto(idInexistente, produtoDTOParaAtualizar), // Passando o DTO
                "Esperava-se RuntimeException para produto inexistente");

        assertTrue(thrown.getMessage().contains("Produto com ID " + idInexistente + " não encontrado."));
        verify(produtoRepository, times(1)).existsById(idInexistente);
        verify(produtoRepository, never()).findById(anyString());
        verify(produtoRepository, never()).save(any(Produto.class));
    }

    // --- Testes para buscarProduto (recebe String id, retorna Produto) ---
    @Test
    @DisplayName("Deve buscar um produto pelo ID com sucesso")
    void deveBuscarProdutoComSucesso() {
        String idExistente = produtoExistente1.getId();
        when(produtoRepository.findById(idExistente)).thenReturn(Optional.of(produtoExistente1));

        Produto resultado = produtoService.buscarProduto(idExistente);

        assertNotNull(resultado);
        assertEquals(idExistente, resultado.getId());
        assertEquals("Smartphone X", resultado.getNome());
        verify(produtoRepository, times(1)).findById(idExistente);
    }

    @Test
    @DisplayName("Deve lançar RuntimeException ao tentar buscar produto inexistente")
    void deveLancarExcecaoAoBuscarProdutoInexistente() {
        String idInexistente = "id-nao-existe";
        when(produtoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> produtoService.buscarProduto(idInexistente),
                "Esperava-se RuntimeException para produto inexistente");

        assertTrue(thrown.getMessage().contains("Produto com ID " + idInexistente + " não encontrado."));
        verify(produtoRepository, times(1)).findById(idInexistente);
    }

    // --- Testes para listarProdutos (retorna List<Produto>) ---
    @Test
    @DisplayName("Deve listar todos os produtos existentes")
    void deveListarTodosOsProdutos() {
        List<Produto> produtos = Arrays.asList(produtoExistente1, produtoExistente2);
        when(produtoRepository.findAll()).thenReturn(produtos);

        List<Produto> resultado = produtoService.listarProdutos();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Smartphone X", resultado.get(0).getNome());
        assertEquals("Smartwatch Z", resultado.get(1).getNome());
        verify(produtoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia quando não há produtos")
    void deveRetornarListaVaziaQuandoNaoHaProdutos() {
        when(produtoRepository.findAll()).thenReturn(Arrays.asList());

        List<Produto> resultado = produtoService.listarProdutos();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(produtoRepository, times(1)).findAll();
    }

    // --- Testes para deletarProduto (recebe String id) ---
    @Test
    @DisplayName("Deve deletar um produto pelo ID com sucesso")
    void deveDeletarProdutoComSucesso() {
        String idParaDeletar = produtoExistente1.getId();
        when(produtoRepository.existsById(idParaDeletar)).thenReturn(true);

        // Para métodos void, use doNothing().when()
        doNothing().when(produtoRepository).deleteById(idParaDeletar);

        assertDoesNotThrow(() -> produtoService.deletarProduto(idParaDeletar));

        verify(produtoRepository, times(1)).existsById(idParaDeletar);
        verify(produtoRepository, times(1)).deleteById(idParaDeletar);
    }

    @Test
    @DisplayName("Deve lançar RuntimeException ao tentar deletar produto inexistente")
    void deveLancarExcecaoAoDeletarProdutoInexistente() {
        String idInexistente = "id-nao-existe";
        when(produtoRepository.existsById(idInexistente)).thenReturn(false);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> produtoService.deletarProduto(idInexistente),
                "Esperava-se RuntimeException para produto inexistente");

        assertTrue(thrown.getMessage().contains("Produto com ID " + idInexistente + " não encontrado."));
        verify(produtoRepository, times(1)).existsById(idInexistente);
        verify(produtoRepository, never()).deleteById(anyString());
    }
}
package com.galleria.backend.service;

import com.galleria.backend.dto.request.ProdutoRequestDTO;
import com.galleria.backend.dto.response.ProdutoResponseDTO;
import com.galleria.backend.model.Produto;
import com.galleria.backend.repository.ItemPedidoRepository;
import com.galleria.backend.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final ItemPedidoRepository itemPedidoRepository;

    public ProdutoService(ProdutoRepository produtoRepository, ItemPedidoRepository itemPedidoRepository) {
        this.produtoRepository = produtoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
    }

    @Transactional
    public ProdutoResponseDTO cadastrar(ProdutoRequestDTO dto) {
        Produto produto = new Produto();
        produto.setDescricao(dto.descricao());
        produto.setValor(dto.valor());
        produto.setAtivo(true);

        return toResponseDTO(produtoRepository.save(produto));
    }

    @Transactional(readOnly = true)
    public ProdutoResponseDTO buscarPorId(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com id: " + id));
        return toResponseDTO(produto);
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> listarTodos() {
        return produtoRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional
    public ProdutoResponseDTO atualizar(Long id, ProdutoRequestDTO dto) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com id: " + id));

        produto.setDescricao(dto.descricao());
        produto.setValor(dto.valor());

        return toResponseDTO(produtoRepository.save(produto));
    }

    @Transactional
    public void remover(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com id: " + id));

        if (itemPedidoRepository.existsByProdutoId(id)) {
            throw new IllegalStateException("Não é possível remover o produto pois já integra itens de pedido.");
        }

        produtoRepository.delete(produto);
    }

    private ProdutoResponseDTO toResponseDTO(Produto produto) {
        return new ProdutoResponseDTO(
                produto.getId(),
                produto.getDescricao(),
                produto.getValor(),
                produto.getAtivo()
        );
    }
}
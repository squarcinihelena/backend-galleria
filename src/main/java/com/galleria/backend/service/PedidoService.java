package com.galleria.backend.service;

import com.galleria.backend.dto.request.PedidoRequestDTO;
import com.galleria.backend.dto.response.ItemPedidoResponseDTO;
import com.galleria.backend.dto.response.PedidoResponseDTO;
import com.galleria.backend.model.Cliente;
import com.galleria.backend.model.ItemPedido;
import com.galleria.backend.model.Pedido;
import com.galleria.backend.model.Produto;
import com.galleria.backend.repository.ClienteRepository;
import com.galleria.backend.repository.PedidoRepository;
import com.galleria.backend.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         ClienteRepository clienteRepository,
                         ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional
    public PedidoResponseDTO cadastrar(PedidoRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.clienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com id: " + dto.clienteId()));

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setDescricao(dto.descricao());

        List<ItemPedido> itens = dto.itens().stream()
                .map(itemDto -> {
                    Produto produto = produtoRepository.findById(itemDto.produtoId())
                            .orElseThrow(() -> new RuntimeException("Produto não encontrado com id: " + itemDto.produtoId()));

                    ItemPedido item = new ItemPedido();
                    item.setPedido(pedido);
                    item.setProduto(produto);
                    item.setQuantidade(itemDto.quantidade());
                    item.setPrecoUnitario(produto.getValor());
                    return item;
                })
                .toList();

        pedido.getItens().addAll(itens);

        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        if (dto.numero() == null || dto.numero().isBlank()) {
            pedidoSalvo.setNumero(String.valueOf(pedidoSalvo.getId()));
        } else {
            pedidoSalvo.setNumero(dto.numero());
        }

        return toResponseDTO(pedidoSalvo);
    }

    @Transactional(readOnly = true)
    public PedidoResponseDTO buscarPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado com id: " + id));
        return toResponseDTO(pedido);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> listarTodos() {
        return pedidoRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private PedidoResponseDTO toResponseDTO(Pedido pedido) {
        List<ItemPedidoResponseDTO> itensDTO = pedido.getItens().stream()
                .map(item -> new ItemPedidoResponseDTO(
                        item.getProduto().getId(),
                        item.getProduto().getDescricao(),
                        item.getQuantidade(),
                        item.getPrecoUnitario(),
                        item.getSubtotal()
                ))
                .toList();

        return new PedidoResponseDTO(
                pedido.getId(),
                pedido.getNumero(),
                pedido.getDataEmissao(),
                pedido.getDescricao(),
                pedido.getCliente().getId(),
                pedido.getCliente().getNome(),
                itensDTO,
                pedido.getValorTotal()
        );
    }
}
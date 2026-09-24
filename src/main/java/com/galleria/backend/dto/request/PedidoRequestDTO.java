package com.galleria.backend.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PedidoRequestDTO(
        String numero,
        String descricao,

        @NotNull(message = "O cliente é obrigatório")
        Long clienteId,

        @NotEmpty(message = "O pedido deve ter pelo menos 1 produto")
        List<ItemPedidoRequestDTO> itens
) {
}
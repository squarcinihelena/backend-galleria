package com.galleria.backend.dto.response;

import java.math.BigDecimal;

public record ItemPedidoResponseDTO(
        Long produtoId,
        String descricaoProduto,
        Integer quantidade,
        BigDecimal precoUnitario,
        BigDecimal subtotal
) {
}
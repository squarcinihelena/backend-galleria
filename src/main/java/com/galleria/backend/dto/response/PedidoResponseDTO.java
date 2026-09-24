package com.galleria.backend.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponseDTO(
        Long id,
        String numero,
        LocalDateTime dataEmissao,
        String descricao,
        Long clienteId,
        String nomeCliente,
        List<ItemPedidoResponseDTO> itens,
        BigDecimal valorTotal
) {
}
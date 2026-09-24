package com.galleria.backend.dto.response;

import java.math.BigDecimal;

public record ProdutoResponseDTO(
        Long id,
        String descricao,
        BigDecimal valor,
        Boolean ativo
) {
}
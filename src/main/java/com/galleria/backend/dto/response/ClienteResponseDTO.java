package com.galleria.backend.dto.response;

public record ClienteResponseDTO(
        Long id,
        String nome,
        String cpf,
        String telefone,
        Boolean ativo
) {
}
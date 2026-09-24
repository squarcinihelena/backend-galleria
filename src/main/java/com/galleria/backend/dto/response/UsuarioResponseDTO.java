package com.galleria.backend.dto.response;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String login,
        Boolean ativo
) {
}
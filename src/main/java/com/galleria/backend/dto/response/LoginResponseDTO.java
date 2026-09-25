package com.galleria.backend.dto.response;

public record LoginResponseDTO(
        Long id,
        String nome,
        String login,
        String token
) {
}
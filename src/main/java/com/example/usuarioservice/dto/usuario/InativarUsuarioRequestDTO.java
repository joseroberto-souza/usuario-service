package com.example.usuarioservice.dto.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Transporta a justificativa obrigatória para inativar um usuário.
 */
public record InativarUsuarioRequestDTO(
        @NotBlank(message = "A justificativa de inativação é obrigatória.")
        @Size(max = 255) String motivo
) {}

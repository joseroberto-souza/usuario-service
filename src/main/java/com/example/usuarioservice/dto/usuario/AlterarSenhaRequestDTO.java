package com.example.usuarioservice.dto.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Transporta a senha atual e a nova senha para alteração pelo próprio usuário.
 */
public record AlterarSenhaRequestDTO(
        @NotBlank(message = "A senha atual é obrigatória.") String senhaAtual,
        @NotBlank(message = "A nova senha é obrigatória.") @Size(min = 6) String novaSenha
) {}

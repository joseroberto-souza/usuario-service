package com.example.usuarioservice.dto.login;

import jakarta.validation.constraints.NotBlank;

/**
 * Transporta as credenciais de login enviadas pelo cliente.
 */
public record LoginRequestDTO(
        @NotBlank(message = "O login (e-mail ou matrícula) é obrigatório.") String login,
        @NotBlank(message = "A senha é obrigatória.") String senha
) {}

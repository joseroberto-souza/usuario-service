package com.example.usuarioservice.dto.usuario;

import com.example.usuarioservice.enums.PerfilAcesso;
import jakarta.validation.constraints.*;
import java.util.Set;

/**
 * Transporta os dados de entrada para atualizar um usuário existente.
 */
public record UsuarioUpdateRequestDTO(
        @NotBlank(message = "A matrícula é obrigatória.") @Size(max = 30) String matricula,
        @NotBlank(message = "O nome completo é obrigatório.") @Size(max = 150) String nomeCompleto,
        @NotBlank(message = "O e-mail é obrigatório.") @Email @Size(max = 100) String email,
        @NotBlank(message = "O telefone é obrigatório.") @Size(max = 20) String telefone,
        @NotNull(message = "O Centro Acadêmico vinculado é obrigatório.") Long centroAcademicoId,
        @NotEmpty(message = "O usuário deve possuir ao menos um perfil de acesso.") Set<PerfilAcesso> perfis
) {}

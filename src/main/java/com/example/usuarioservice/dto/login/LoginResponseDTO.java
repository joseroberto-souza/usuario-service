package com.example.usuarioservice.dto.login;

import com.example.usuarioservice.enums.PerfilAcesso;
import java.util.Set;

/**
 * Devolve o token JWT e os dados básicos do usuário após a autenticação.
 */
public record LoginResponseDTO(
        String token,
        String tipo,
        Long usuarioId,
        String nome,
        String email,
        String matricula,
        Set<PerfilAcesso> perfis
) {}

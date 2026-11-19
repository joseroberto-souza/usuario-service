package com.example.usuarioservice.enums;

/**
 * Define os papéis de controle de acesso suportados pelo sistema.
 * Um usuário pode acumular múltiplos perfis simultaneamente.
 */
public enum PerfilAcesso {
    ADMINISTRADOR, // Gerencia entidades globais, usuários e logs do sistema.
    COLABORADOR,   // Gerencia espaços e modera solicitações de reserva ou software.
    USUARIO        // Solicita reservas de espaços e instalação de softwares.
}

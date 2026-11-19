package com.example.usuarioservice.enums;

/**
 * Define os estados possíveis do ciclo de vida cadastral de um usuário.
 */
public enum StatusCadastro {
    ATIVO,  // Indica que o cadastro está ativo e liberado para autenticação.
    INATIVO // Indica que o cadastro foi desativado logicamente sem apagar o histórico.
}

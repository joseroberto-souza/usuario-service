package com.example.usuarioservice.client;

/**
 * Representa os dados essenciais de um Centro Acadêmico obtidos de outro microsserviço.
 */
public record CentroAcademicoExternoDTO(
        Long id,      // Identifica o Centro Acadêmico no serviço acadêmico.
        String nome,  // Armazena o nome do Centro Acadêmico para exibição na resposta.
        String status // Indica a situação cadastral (ATIVO ou INATIVO) para validação de regra de negócio.
) {}
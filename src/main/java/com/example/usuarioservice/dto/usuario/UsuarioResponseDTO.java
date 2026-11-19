package com.example.usuarioservice.dto.usuario;

import com.example.usuarioservice.enums.PerfilAcesso;
import com.example.usuarioservice.enums.StatusCadastro;
import com.example.usuarioservice.model.Usuario;
import java.time.LocalDate;
import java.util.Set;

/**
 * Devolve os dados consolidados de um usuário nas respostas da API.
 */
public record UsuarioResponseDTO(
        Long id,
        String matricula,
        String nomeCompleto,
        String email,
        String telefone,
        LocalDate dataCadastramento,
        StatusCadastro status,
        String motivoInativacao,
        Long centroAcademicoId,
        String centroAcademicoNome,
        Set<PerfilAcesso> perfis
) {
    public static UsuarioResponseDTO fromEntity(Usuario usuario, String centroAcademicoNome) {
        // Mapeia a entidade Usuario para o DTO de resposta combinando o nome remoto do Centro Acadêmico.
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getMatricula(),
                usuario.getNomeCompleto(),
                usuario.getEmail(),
                usuario.getTelefone(),
                usuario.getDataCadastramento(),
                usuario.getStatus(),
                usuario.getMotivoInativacao(),
                usuario.getCentroAcademicoId(),
                centroAcademicoNome,
                usuario.getPerfis()
        );
    }
}
package com.example.usuarioservice.config;

import com.example.usuarioservice.enums.PerfilAcesso;
import com.example.usuarioservice.enums.StatusCadastro;
import com.example.usuarioservice.model.Usuario;
import com.example.usuarioservice.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * Popula o banco de dados com usuários iniciais de teste na inicialização do serviço.
 */
@Configuration
public class InicializadorDados {

    @Bean
    CommandLineRunner iniciarDB(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Verifica se a tabela está vazia antes de inserir os usuários padrão.
            if (usuarioRepository.count() == 0) {
                Usuario admin = Usuario.builder()
                        .matricula("ADM-001")
                        .nomeCompleto("Administrador Geral")
                        .email("admin@ucsal.br")
                        .telefone("71988887777")
                        .senha(passwordEncoder.encode("admin123"))
                        .dataCadastramento(LocalDate.now())
                        .status(StatusCadastro.ATIVO)
                        .centroAcademicoId(1L) // Referencia o ID 1 do Centro Acadêmico remoto.
                        .perfis(Set.of(PerfilAcesso.ADMINISTRADOR, PerfilAcesso.COLABORADOR, PerfilAcesso.USUARIO))
                        .build();

                Usuario professor = Usuario.builder()
                        .matricula("DOC-20261")
                        .nomeCompleto("Prof. Carlos Eduardo")
                        .email("carlos.docente@ucsal.br")
                        .telefone("71977776666")
                        .senha(passwordEncoder.encode("docente123"))
                        .dataCadastramento(LocalDate.now())
                        .status(StatusCadastro.ATIVO)
                        .centroAcademicoId(1L)
                        .perfis(Set.of(PerfilAcesso.COLABORADOR, PerfilAcesso.USUARIO))
                        .build();

                usuarioRepository.saveAll(List.of(admin, professor));
                System.out.println("Usuários iniciais cadastrados com sucesso no usuario-service.");
            }
        };
    }
}

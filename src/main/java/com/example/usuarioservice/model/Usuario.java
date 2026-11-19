package com.example.usuarioservice.model;

import com.example.usuarioservice.enums.PerfilAcesso;
import com.example.usuarioservice.enums.StatusCadastro;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Representa a entidade principal de autenticação e cadastro de usuários.
 * Implementa a interface UserDetails para integrar com o Spring Security.
 */
@Entity
@Table(name = "tb_usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Identifica o registro único do usuário no banco de dados.

    @Column(nullable = false, unique = true, length = 30)
    private String matricula; // Armazena o registro funcional único do usuário.

    @Column(nullable = false, length = 150)
    private String nomeCompleto; // Armazena o nome completo do usuário.

    @Column(nullable = false, unique = true, length = 100)
    private String email; // Armazena o e-mail institucional usado no login.

    @Column(nullable = false, length = 20)
    private String telefone; // Armazena o telefone de contato do usuário.

    @Column(nullable = false)
    private String senha; // Armazena o hash criptográfico da senha do usuário.

    @Column(nullable = false)
    private LocalDate dataCadastramento; // Registra a data de criação do cadastro.

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusCadastro status; // Controla se a conta está ativa ou inativa.

    @Column(length = 255)
    private String motivoInativacao; // Registra a justificativa obrigatória ao inativar a conta.

    @Column(name = "centro_academico_id")
    private Long centroAcademicoId; // Guarda apenas o ID do Centro Acadêmico pertencente a outro microsserviço.

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "tb_usuarios_perfis", joinColumns = @JoinColumn(name = "usuario_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "perfil", nullable = false)
    @Builder.Default
    private Set<PerfilAcesso> perfis = new HashSet<>(); // Armazena os privilégios de acesso do usuário.

    @PrePersist
    public void prePersist() {
        // Define a data atual e o status ATIVO antes de salvar um novo registro.
        if (this.dataCadastramento == null) {
            this.dataCadastramento = LocalDate.now();
        }
        if (this.status == null) {
            this.status = StatusCadastro.ATIVO;
        }
    }

    public void inativar(String motivoInativacao) {
        // Bloqueia a inativação caso nenhuma justificativa seja informada.
        if (motivoInativacao == null || motivoInativacao.isBlank()) {
            throw new IllegalArgumentException("A justificativa para inativação é obrigatória.");
        }
        this.status = StatusCadastro.INATIVO;
        this.motivoInativacao = motivoInativacao;
    }

    public void reativar() {
        // Restaura o acesso da conta e limpa o motivo da inativação anterior.
        if (this.status == StatusCadastro.ATIVO) {
            throw new IllegalStateException("O usuário já está com o cadastro ativo.");
        }
        this.status = StatusCadastro.ATIVO;
        this.motivoInativacao = null;
    }

    public boolean possuiPerfil(PerfilAcesso perfil) {
        // Verifica se o usuário possui um determinado privilégio de acesso.
        return this.perfis != null && this.perfis.contains(perfil);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Converte cada enum PerfilAcesso para o padrão ROLE_ exigido pelo Spring Security.
        return this.perfis.stream()
                .map(perfil -> new SimpleGrantedAuthority("ROLE_" + perfil.name()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Impede o login caso o usuário esteja inativado logicamente.
        return this.status == StatusCadastro.ATIVO;
    }

    @Override
    public boolean isEnabled() {
        return this.status == StatusCadastro.ATIVO;
    }
}

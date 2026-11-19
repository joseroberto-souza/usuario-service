package com.example.usuarioservice.repository;

import com.example.usuarioservice.enums.StatusCadastro;
import com.example.usuarioservice.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Gerencia o acesso e as consultas da entidade Usuario no banco de dados.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Busca um usuário pelo e-mail institucional ou pela matrícula durante o login.
    @Query("SELECT u FROM Usuario u WHERE LOWER(u.email) = LOWER(:login) OR UPPER(u.matricula) = UPPER(:login)")
    Optional<Usuario> findByEmailOrMatricula(@Param("login") String login);

    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByMatricula(String matricula);
    List<Usuario> findByStatus(StatusCadastro status);
    List<Usuario> findByCentroAcademicoId(Long centroAcademicoId);

    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
    boolean existsByMatricula(String matricula);
    boolean existsByMatriculaAndIdNot(String matricula, Long id);
}

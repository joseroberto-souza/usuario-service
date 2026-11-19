package com.example.usuarioservice.service;

import com.example.usuarioservice.client.CentroAcademicoClient;
import com.example.usuarioservice.client.CentroAcademicoExternoDTO;
import com.example.usuarioservice.dto.usuario.*;
import com.example.usuarioservice.enums.StatusCadastro;
import com.example.usuarioservice.model.Usuario;
import com.example.usuarioservice.repository.UsuarioRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

/**
 * Gerencia as regras de negócio e o ciclo de vida cadastral dos usuários.
 */
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final CentroAcademicoClient centroAcademicoClient;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        // Retorna todos os usuários cadastrados convertidos para DTO.
        return usuarioRepository.findAll().stream()
                .map(u -> UsuarioResponseDTO.fromEntity(u, null))
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id) {
        // Busca um usuário pelo ID e consulta o nome do Centro Acadêmico vinculado.
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com o ID: " + id));
        String nomeCentro = buscarNomeCentroSilencioso(usuario.getCentroAcademicoId());
        return UsuarioResponseDTO.fromEntity(usuario, nomeCentro);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarPorStatus(StatusCadastro status) {
        // Filtra os usuários pelo status cadastral informado.
        return usuarioRepository.findByStatus(status).stream()
                .map(u -> UsuarioResponseDTO.fromEntity(u, null))
                .toList();
    }

    @Transactional
    public UsuarioResponseDTO cadastrar(UsuarioRequestDTO requestDTO) {
        // Verifica unicidade de e-mail e matrícula antes de salvar.
        if (usuarioRepository.existsByEmailIgnoreCase(requestDTO.email())) {
            throw new IllegalArgumentException("Já existe um usuário cadastrado com este e-mail.");
        }
        if (usuarioRepository.existsByMatricula(requestDTO.matricula())) {
            throw new IllegalArgumentException("Já existe um usuário cadastrado com esta matrícula.");
        }

        // Valida remotamente se o Centro Acadêmico existe e está ativo.
        CentroAcademicoExternoDTO centro = validarCentroAcademico(requestDTO.centroAcademicoId());

        Usuario usuario = Usuario.builder()
                .matricula(requestDTO.matricula())
                .nomeCompleto(requestDTO.nomeCompleto())
                .email(requestDTO.email())
                .telefone(requestDTO.telefone())
                .senha(passwordEncoder.encode(requestDTO.senha()))
                .dataCadastramento(LocalDate.now())
                .status(StatusCadastro.ATIVO)
                .centroAcademicoId(centro.id())
                .perfis(new HashSet<>(requestDTO.perfis()))
                .build();

        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        return UsuarioResponseDTO.fromEntity(usuarioSalvo, centro.nome());
    }

    @Transactional
    public UsuarioResponseDTO editar(Long id, UsuarioUpdateRequestDTO requestDTO) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com o ID: " + id));

        if (usuarioRepository.existsByEmailIgnoreCaseAndIdNot(requestDTO.email(), id)) {
            throw new IllegalArgumentException("Já existe outro usuário cadastrado com este e-mail.");
        }
        if (usuarioRepository.existsByMatriculaAndIdNot(requestDTO.matricula(), id)) {
            throw new IllegalArgumentException("Já existe outro usuário cadastrado com esta matrícula.");
        }

        CentroAcademicoExternoDTO centro = validarCentroAcademico(requestDTO.centroAcademicoId());

        usuario.setMatricula(requestDTO.matricula());
        usuario.setNomeCompleto(requestDTO.nomeCompleto());
        usuario.setEmail(requestDTO.email());
        usuario.setTelefone(requestDTO.telefone());
        usuario.setCentroAcademicoId(centro.id());
        usuario.setPerfis(new HashSet<>(requestDTO.perfis()));

        Usuario usuarioAtualizado = usuarioRepository.save(usuario);
        return UsuarioResponseDTO.fromEntity(usuarioAtualizado, centro.nome());
    }

    @Transactional
    public void inativar(Long id, InativarUsuarioRequestDTO requestDTO) {
        // Desativa logicamente a conta do usuário mediante justificativa.
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com o ID: " + id));
        usuario.inativar(requestDTO.motivo());
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void reativar(Long id) {
        // Restaura o acesso de um usuário inativo.
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com o ID: " + id));
        usuario.reativar();
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void alterarSenhaPropria(String emailUsuario, AlterarSenhaRequestDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com o e-mail: " + emailUsuario));

        if (!passwordEncoder.matches(dto.senhaAtual(), usuario.getSenha())) {
            throw new IllegalArgumentException("A senha atual informada está incorreta.");
        }
        if (passwordEncoder.matches(dto.novaSenha(), usuario.getSenha())) {
            throw new IllegalArgumentException("A nova senha não pode ser igual à senha atual.");
        }

        usuario.setSenha(passwordEncoder.encode(dto.novaSenha()));
        usuarioRepository.save(usuario);
    }

    private CentroAcademicoExternoDTO validarCentroAcademico(Long centroId) {
        try {
            // Consulta o microsserviço acadêmico via OpenFeign.
            CentroAcademicoExternoDTO centro = centroAcademicoClient.buscarPorId(centroId);
            if ("INATIVO".equalsIgnoreCase(centro.status())) {
                throw new IllegalStateException("Não é possível vincular um usuário a um Centro Acadêmico inativo.");
            }
            return centro;
        } catch (FeignException.NotFound e) {
            throw new IllegalArgumentException("Centro Acadêmico não encontrado com o ID: " + centroId);
        } catch (FeignException e) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Serviço de Centros Acadêmicos indisponível no momento. Tente novamente mais tarde."
            );
        }
    }

    private String buscarNomeCentroSilencioso(Long centroId) {
        if (centroId == null) return null;
        try {
            return centroAcademicoClient.buscarPorId(centroId).nome();
        } catch (Exception e) {
            return "Centro Acadêmico ID " + centroId;
        }
    }
}

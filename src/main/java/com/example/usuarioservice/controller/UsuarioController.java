package com.example.usuarioservice.controller;

import com.example.usuarioservice.dto.usuario.*;
import com.example.usuarioservice.enums.StatusCadastro;
import com.example.usuarioservice.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Expõe os endpoints REST para o gerenciamento cadastral de usuários.
 */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UsuarioResponseDTO> cadastrar(@Valid @RequestBody UsuarioRequestDTO requestDTO) {
        // Cadastra um novo usuário no sistema.
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.cadastrar(requestDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COLABORADOR', 'USUARIO')")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        // Busca os dados de um usuário pelo identificador único.
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COLABORADOR')")
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos(@RequestParam(required = false) StatusCadastro status) {
        // Lista todos os usuários ou filtra pelo status cadastral informado.
        if (status != null) {
            return ResponseEntity.ok(usuarioService.listarPorStatus(status));
        }
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UsuarioResponseDTO> editar(@PathVariable Long id, @Valid @RequestBody UsuarioUpdateRequestDTO requestDTO) {
        // Atualiza os dados cadastrais de um usuário existente.
        return ResponseEntity.ok(usuarioService.editar(id, requestDTO));
    }

    @PatchMapping("/{id}/inativar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> inativar(@PathVariable Long id, @Valid @RequestBody InativarUsuarioRequestDTO requestDTO) {
        // Desativa logicamente a conta do usuário com justificativa obrigatória.
        usuarioService.inativar(id, requestDTO);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reativar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> reativar(@PathVariable Long id) {
        // Reativa a conta de um usuário previamente inativado.
        usuarioService.reativar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/alterar-senha")
    @PreAuthorize("hasAnyRole('USUARIO', 'COLABORADOR', 'ADMINISTRADOR')")
    public ResponseEntity<Void> alterarMinhaSenha(@Valid @RequestBody AlterarSenhaRequestDTO dto, Authentication authentication) {
        // Permite que o usuário autenticado altere sua própria senha.
        usuarioService.alterarSenhaPropria(authentication.getName(), dto);
        return ResponseEntity.noContent().build();
    }
}

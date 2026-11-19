package com.example.usuarioservice.controller;

import com.example.usuarioservice.config.security.TokenService;
import com.example.usuarioservice.dto.login.LoginRequestDTO;
import com.example.usuarioservice.dto.login.LoginResponseDTO;
import com.example.usuarioservice.model.Usuario;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Expõe o endpoint REST para autenticação e emissão de tokens JWT.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AutenticacaoController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO request) {
        // Valida as credenciais informadas usando o AuthenticationManager do Spring Security.
        var authToken = new UsernamePasswordAuthenticationToken(request.login(), request.senha());
        Authentication authentication = authenticationManager.authenticate(authToken);

        Usuario usuario = (Usuario) authentication.getPrincipal();
        String tokenJWT = tokenService.gerarToken(usuario);

        return ResponseEntity.ok(new LoginResponseDTO(
                tokenJWT, "Bearer", usuario.getId(), usuario.getNomeCompleto(),
                usuario.getEmail(), usuario.getMatricula(), usuario.getPerfis()
        ));
    }
}

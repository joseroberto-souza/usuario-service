package com.example.usuarioservice.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.usuarioservice.model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Emite e valida tokens JWT para autenticação de usuários.
 */
@Service
public class TokenService {

    @Value("${api.security.token.secret:segredo-padrao-espacos-academicos-2026}")
    private String secret; // Define a chave secreta usada para assinar e verificar o token.

    private static final String ISSUER = "espacos-academicos-api"; // Identifica o emissor oficial do token.

    public String gerarToken(Usuario usuario) {
        try {
            Algorithm algoritmo = Algorithm.HMAC256(secret);
            // Gera o token JWT contendo os dados básicos e os perfis de acesso do usuário.
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withSubject(usuario.getEmail())
                    .withClaim("id", usuario.getId())
                    .withClaim("nome", usuario.getNomeCompleto())
                    .withClaim("matricula", usuario.getMatricula())
                    .withClaim("perfis", usuario.getPerfis().stream().map(Enum::name).toList())
                    .withExpiresAt(gerarDataExpiracao())
                    .sign(algoritmo);
        } catch (JWTCreationException e) {
            throw new RuntimeException("Erro ao gerar token JWT de autenticação.", e);
        }
    }

    public String validarToken(String tokenJWT) {
        try {
            Algorithm algoritmo = Algorithm.HMAC256(secret);
            // Verifica a assinatura do token e devolve o e-mail do usuário autenticado.
            return JWT.require(algoritmo)
                    .withIssuer(ISSUER)
                    .build()
                    .verify(tokenJWT)
                    .getSubject();
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    private Instant gerarDataExpiracao() {
        // Define o prazo de expiração do token para 4 horas no fuso horário UTC-3.
        return LocalDateTime.now().plusHours(4).toInstant(ZoneOffset.of("-03:00"));
    }
}

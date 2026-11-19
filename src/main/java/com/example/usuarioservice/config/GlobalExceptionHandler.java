package com.example.usuarioservice.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Intercepta exceções da aplicação e converte em respostas HTTP padronizadas.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Object> handleIllegalState(IllegalStateException ex) {
        // Retorna 409 quando ocorre conflito de regra de negócio (ex.: Centro Acadêmico inativo).
        return construirResposta(HttpStatus.CONFLICT, "Conflito de Regra de Negócio", ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex) {
        // Retorna 400 quando os dados informados são inválidos ou duplicados.
        return construirResposta(HttpStatus.BAD_REQUEST, "Requisição Inválida", ex.getMessage());
    }

    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    public ResponseEntity<Object> handleBadCredentials(Exception ex) {
        // Retorna 401 quando o login ou a senha estão incorretos.
        return construirResposta(HttpStatus.UNAUTHORIZED, "Não Autorizado", "Credenciais inválidas: e-mail/matrícula ou senha incorretos.");
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handleResponseStatus(ResponseStatusException ex) {
        // Propaga o status específico lançado pelo serviço (ex.: 503 quando o Feign falha).
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        return construirResposta(status, status.getReasonPhrase(), ex.getReason());
    }

    private ResponseEntity<Object> construirResposta(HttpStatus status, String erro, String mensagem) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", erro);
        body.put("message", mensagem);
        return ResponseEntity.status(status).body(body);
    }
}

package com.example.usuarioservice.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Configura a propagação automática do token JWT nas chamadas internas do OpenFeign.
 */
@Configuration
public class FeignClientConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                // Recupera os atributos da requisição HTTP atual recebida pelo servidor.
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attributes != null) {
                    HttpServletRequest request = attributes.getRequest();
                    String authHeader = request.getHeader("Authorization");

                    // Copia o cabeçalho Authorization (Bearer token) para a requisição Feign de saída.
                    if (authHeader != null && !authHeader.isBlank()) {
                        template.header("Authorization", authHeader);
                    }
                }
            }
        };
    }
}
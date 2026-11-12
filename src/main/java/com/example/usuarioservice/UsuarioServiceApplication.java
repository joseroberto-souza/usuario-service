package com.example.usuarioservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Inicia a aplicação do microsserviço de usuários.
 * Este serviço gerencia as regras de negócio de usuários e registra a sua disponibilidade no Eureka.
 */
@SpringBootApplication // Habilita a configuração automática e a inicialização do Spring Boot.
@EnableDiscoveryClient // Registra este microsserviço no servidor Eureka para descoberta na rede.
public class UsuarioServiceApplication {

    /**
     * Executa o ponto de entrada principal da aplicação Java.
     *
     * @param args Argumentos de linha de comando passados na inicialização.
     */
    public static void main(String[] args) {
        // Inicia o contexto do Spring e sobe o microsserviço na porta configurada.
        SpringApplication.run(UsuarioServiceApplication.class, args);
    }

}
